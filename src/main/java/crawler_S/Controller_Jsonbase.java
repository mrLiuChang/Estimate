package crawler_S;


import model.ExtraConf;
import opt.RAMMD5Dedutor;
import org.eclipse.jetty.websocket.api.util.QuoteUtil;

import javax.management.Query;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/*
 * 基于json数据的爬取。
 * */
public class Controller_Jsonbase implements Runnable {
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    static String webId = "";
    static double pi = 1;
    static double queryNum = 0;
    public static String workFile = "";
    static String round = "";
    static int totalSize = 0;
    static String type = "";
    public static RAMMD5Dedutor dedu = null;
    public static String cookie = "";



    public Controller_Jsonbase(String webId) {
        this.webId = webId;
        round = "1";
        totalSize = Integer.parseInt(DBUtil.select("extraConf", new String[]{"databaseSize"}, Integer.parseInt(webId))[0][0]);
        dedu = new RAMMD5Dedutor(Paths.get(workFile + "/estimate_structed/" + webId + "/"));


    }


    public  static  String getCookie(String webId){
        ExtraConf extraConf = (ExtraConf) DBUtil.selectMutilpara("extraConf", new String[]{"*"}, Integer.parseInt(webId), ExtraConf.class).get(0);

        try {
             cookie = Login.getCookie(extraConf);
            System.out.println(cookie);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return cookie;
    }
    public static void continueCrawling(String webId){

        while (true) {
            /*end condition check*/
            QueryLink qLink = new QueryLink(webId, dedu, "post");

            if ((Queries.getN(webId) - queryNum) < Queries.getC(webId)) {
                break;
            }
            /*get the round num*/
            round = DBUtil.select("estimate", new String[]{"walkTimes"}, new String[]{"estiId"}, new String[]{webId})[0][0];

            /*add the round num*/

            /*get the queryNum */
            Queries query = new Queries(webId);
            queryNum = query.selectQueries((int)Queries.getC(webId));
         //   Scheduler.ItemSize=0;
            /*Get the queryLinks*/
            System.out.println("GetQueryLinks");
            ArrayList<String> queryLinks = qLink.GetQueryLinks(webId);
            System.out.println("compute");
            check(webId);
            int t = Integer.parseInt(round) + 1;
            round = t + "";

            DBUtil.update("estimate", new String[]{"walkTimes"}, new String[]{round}, new String[]{"estiId"}, new String[]{webId});



        }
    }


    private static void check(String webId) {
        round = DBUtil.select("estimate", new String[]{"walkTimes"}, new String[]{"estiId"}, new String[]{webId})[0][0];
        double SampleData = Scheduler.ItemSize;
        double N = Queries.getN(webId);
        double C = Queries.getC(webId);
        double i = Integer.parseInt(round) * C;
        pi = queryNum / i;
        double dataSize = (1 + (N - i) / i * pi) * SampleData;
        System.out.println("dataSize:" + dataSize);
        int walkTimes=(int)Math.ceil(N/C);
        setRate(Integer.parseInt(round),walkTimes);
        DBUtil.update("estimate", new String[]{"result"}, new String[]{(int)dataSize + ""}, new String[]{"estiId"}, new String[]{webId});
    }
    public static String setRate(int curTime, int walkTimes) {
        /*To show the RateBar*/
        double rate = (double) curTime / walkTimes;
        DecimalFormat decimalFormat = new DecimalFormat("00.00%");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String rateStr = decimalFormat.format(rate);//format 返回的是字符串
        DBUtil.update("estimate", new String[]{"rateBar"}, new String[]{rateStr}, new String[]{"estiId"}, new String[]{webId});
        return rateStr;
    }

    @Override
    public void run() {
        continueCrawling(webId);
        try {
            exit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void exit() {
        String pid=0+"";
        DBUtil.update("estimate", new String[]{"pid"}, new String[]{pid}, new String[]{"estiId"}, new String[]{webId});
        DBUtil.update("estimate",new String[]{"status"},new String[]{"stop"}, new String[]{"estiId"},new String[]{webId});
        DBUtil.update("estimate",new String[]{"rateBar"},new String[]{"100.0%"}, new String[]{"estiId"},new String[]{webId});
    }

    private static class Exitor extends Thread {
        @Override
        public void run() {
            System.out.println("start the exit thread");
            //when process come to exit, set the current pid in db to 0
//            String[] pidvalue = {"0"};
//            DBUtil.update("current", new String[]{"run"}, pidvalue, Integer.parseInt(webId));

            System.out.println("start to close resource");
            try {
                if (dedu != null)
                    dedu.close();//dedu data save

            } catch (IOException ex) {
                //nothing to do
            }

            System.out.println("finish to close resource");

            System.out.println("结束" + df.format(new Date()));

        }
    }


    public static void main(String[] args) {
        webId = "127";
        String dbURL = "";
        String dbUser = "";
        String dbPass = "";

        /*在main函数传参的时候进入*/
        if (args.length >= 1) {
            webId = args[0];
            if (args.length != 1 && args.length != 4) {
                System.exit(1);
            }
            if (args.length == 4) {
                dbURL = args[1];
                dbUser = args[2];
                dbPass = args[3];
                DBUtil.config(dbURL, dbUser, dbPass);
            }
        }
        workFile = DBUtil.select("website", new String[]{"workFile"}, Integer.parseInt(webId))[0][0];
        ParamSetter paramSetter = new ParamSetter();
        paramSetter.initialByWebId(webId);//initiate database
        paramSetter.createNewfile(workFile, webId);


        Controller_Jsonbase c = new Controller_Jsonbase(webId);
        new Thread(c).start();
    }


}
