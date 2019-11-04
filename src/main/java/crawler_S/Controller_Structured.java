package crawler_S;

import opt.RAMMD5Dedutor;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Controller_Structured implements Runnable {
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    static String webId = "";
    static double pi = 1;
    static double queryNum = 0;

    static String workFile = "";
    static String round = "1";
    static int totalSize = 0;
    static String type = "";
    public static RAMMD5Dedutor dedu = null;
    public static String cookie = "";

    public Controller_Structured(String webId) {

        //拿到当前项目的存储路径
        this.webId = webId;
        String[] filePara = {"workFile"};
        workFile = DBUtil.select("website", filePara, Integer.parseInt(webId))[0][0];
        dedu = new RAMMD5Dedutor(Paths.get(workFile + "/estimate_structed/" + webId + "/"));
    }

    private static void continueCrawling(String webId) {

        String[] NPara = {"N"};
        String[] CPara = {"C"};

        while (true) {
            QueryLink qLink = new QueryLink(webId, dedu, "get");

            double N = Integer.parseInt(DBUtil.select("queryParam", NPara, Integer.parseInt(webId))[0][0]);
            double C = Integer.parseInt(DBUtil.select("queryParam", CPara, Integer.parseInt(webId))[0][0]);

            if ((N - queryNum) < C) {
                System.out.println("N-queryNum is " + (N - queryNum));
                break;
            }

            round = DBUtil.select("estimate", new String[]{"walkTimes"}, new String[]{"estiId"}, new String[]{webId})[0][0];


            System.out.println("check:true");
            Queries query = new Queries(webId);
            String[] cparam = {"C"};
            String c = DBUtil.select("queryParam", cparam, Integer.parseInt(webId))[0][0];
            //query.select_random(webId);
            int c_value = 0;
            if (c != null && c.length() > 0)
                c_value = Integer.parseInt(c);
            queryNum = query.selectQueries(c_value);


            //part 2
            System.out.println("GetQueryLinks");
            ArrayList<String> queryLinks = qLink.GetQueryLinks(webId);


            //part3
            System.out.println("downloadLinks");
            qLink.downloadLinks(Integer.parseInt(webId), queryLinks, 0);

            check(webId);

            int t = Integer.parseInt(round) + 1;
            round = t + "";
            DBUtil.update("estimate", new String[]{"walkTimes"}, new String[]{round}, new String[]{"estiId"}, new String[]{webId});



        }
    }


    private static void check(String webId) {

        round = DBUtil.select("estimate", new String[]{"walkTimes"}, new String[]{"estiId"}, new String[]{webId})[0][0];
        String[] sPara = {"result"};
        double SampleData = Scheduler.ItemSize;
        double N = Queries.getN(webId);
        double C = Queries.getC(webId);
        double i = Integer.parseInt(round) * C;

        double dataSize = 0;
        pi = queryNum / i;
        dataSize = (1 + (N - i) / i * pi) * SampleData;//
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
    //5.3.2
    public static void startCrawling(String webId) {

        try {
            FileWriter fw = new FileWriter(workFile + "/estimate_structed/" + webId + "/logging.txt", true);
            fw.append(df.format(new Date()) + "\t" + "startCrawling:" + "\t");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        continueCrawling(webId);
    }

    @Override
    public void run() {
        startCrawling(webId);
        try {
            exit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void exit() {

        String pid = 0 + "";
        DBUtil.update("estimate", new String[]{"pid"}, new String[]{pid}, new String[]{"estiId"}, new String[]{webId});
        DBUtil.update("estimate", new String[]{"status"}, new String[]{"stop"}, new String[]{"estiId"}, new String[]{webId});
        DBUtil.update("estimate", new String[]{"rateBar"}, new String[]{"100.0%"}, new String[]{"estiId"}, new String[]{webId});

    }

    public static void main(String[] args) {
//        long pid = ProcessHandle.current().pid();
        webId = "148";
        String dbURL = "";
        String dbUser = "";
        String dbPass = "";
        if (args.length >= 1) {
            webId = args[0];
            if (args.length != 1 && args.length != 4) {
                System.exit(1);
            }
            dbURL = args[1];
            dbUser = args[2];
            dbPass = args[3];
            DBUtil.config(dbURL, dbUser, dbPass);
        }


        workFile = DBUtil.select("website", new String[]{"workFile"}, Integer.parseInt(webId))[0][0];
        ParamSetter paramSetter = new ParamSetter();
        paramSetter.initialByWebId(webId);//initiate database
        paramSetter.createNewfile(workFile, webId);

        Controller_Structured c = new Controller_Structured(webId);
        new Thread(c).start();
        //DaemonService service=new DaemonService(c,c.workFile+"/"+webId+"/"+"map.txt");
        //new Thread(service).start();


        //System.out.println("总耗时"+(e-s)/1000/60/60+"小时");

    }


}
