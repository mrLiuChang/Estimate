package crawler_S;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import opt.RAMMD5Dedutor;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.http.HttpStatus;
import model.ExtraConf;
import model.JsonBase;
import model.UrlBase;
import model.WebSite;
import org.yaml.snakeyaml.util.UriEncoder;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;


public class QueryLink {

    /**
     * @param args
     */

    private ArrayList<String> queries = new ArrayList<String>();

    private int startPageNum = 0;
    private int stepSize = 0;
    private static int round = 0;
    private ArrayList<String> paramslist = new ArrayList<String>();
    private ArrayList<String> paramValues = new ArrayList<String>();
    //
    private static String webId = "";
    private int pageNum = 60;
    private static String workFile = "D:/crawler/";
    private ArrayList<String> failedQueryLinks = new ArrayList<String>();
    private int flag = 0;
    private static String cookie = "";
    private static String nowRound = ""; //round number ;
    private static String saveFolder;
    private static String failedQueryLinksPath = "";
    private static HashMap<String, String> allDataMD5 = null;
    private UrlBase urlbase = null;
    private JsonBase jsonbase = null;
    private WebSite website = null;
    private ExtraConf extraConf = null;
    private String requestMethod = "";
    private static RAMMD5Dedutor dedu = null;
    private static int totalResNum = 0;


    public int getResTotal() {
        ArrayList<String> queryLinks = new ArrayList<String>();

        JsonObject json = new JsonObject();
        for (int i = 0; i < paramslist.size(); i++) {
            json.addProperty(paramslist.get(i), paramValues.get(i));
        }
        String[] paramName = urlbase.getparamQuery().split(",");
        if (queries.size() != 0) {

            for (int i = 0; i < queries.size(); i++) {

                String[] paramValue = queries.get(i).split(",");
                int n = 0;
                for (n = 0; n < paramName.length && n < paramValue.length; n++) {
                    json.addProperty(paramName[n], paramValue[n]);
                }
                for (; n < paramName.length; n++) {
                    json.addProperty(paramName[n], "");
                }
                json.addProperty(urlbase.getparamPage(), startPageNum);


                //此处是总条数的来源。
                totalResNum = (int) getCorrectPage_post(urlbase.getprefix(), json.toString());//
            }
        }
        return totalResNum;
    }

    //private String cookie = "DomAuth=; myusername=zhangsan02; LtpaToken=AAECAzU4NEQ0RDVCNTg0REQ5RkJDTj0T1cUTyP0wMi9PPWNlY2ljWyT9D6d2U4nqPO19jqTxmnnwfmg=";
    //"DomAuth=; myusername=zhangsan02; LtpaToken=AAECAzU4NEQ1QTFBNTg0REU2QkFDTj0T1cUTyP0wMi9PPWNlY2lj4Zm7xES9KVFqCeBsSWkjA42ocpk="
    public QueryLink(String webId, RAMMD5Dedutor dedu, String requestMethod) {
        this.webId = webId;
        this.dedu = dedu;
        init(Integer.parseInt(webId), requestMethod);
        failedQueryLinksPath = workFile + "/" + webId + "/failedQueryLinks.txt";
    }
    //1.1.1 & 1.2.1 get the values of all the attributes except queries

    private void init(Integer webId, String requestMethod) {
        int N = 1;
        int C = 0;
        String[] params = {"*"};
        String[] roundParam = {"walkTimes"};
        this.requestMethod = requestMethod;
        website = (WebSite) DBUtil.selectMutilpara("website", params, webId, WebSite.class).get(0);
        round = Integer.parseInt(DBUtil.select("estimate", roundParam, new String[]{"estiId"}, new String[]{webId + ""})[0][0].trim());
        extraConf = (ExtraConf) DBUtil.selectMutilpara("extraConf", params, webId, ExtraConf.class).get(0);
        if ("post".equals(requestMethod)&&Controller_Jsonbase.cookie.equals(""))
            cookie=Controller_Jsonbase.getCookie(webId+"");
        else cookie=Controller_Jsonbase.cookie;

        workFile = website.getWorkFile();
        urlbase = (UrlBase) DBUtil.selectMutilpara("urlBaseConf", params, webId, UrlBase.class).get(0);
        List json = DBUtil.selectMutilpara("jsonBase", params, webId, JsonBase.class);
        if (json.size() > 0)
            jsonbase = (JsonBase) json.get(0);
        startPageNum = Integer.parseInt(urlbase.getstartPageNum().split(",")[0]);
        stepSize = Integer.parseInt(urlbase.getstartPageNum().split(",")[1]);
        if (urlbase.getparamList() != null && urlbase.getparamQuery() != null && urlbase.getparamQuery().length() > 0) {
            String[] parameter = urlbase.getparamList().split(",");
            for (int i = 0; i < parameter.length; i++) {
                paramslist.add(parameter[i]);
            }
            String[] parameterValue = urlbase.getparamValueList().split(",");
            for (int i = 0; i < parameterValue.length; i++) {
                paramValues.add(parameterValue[i].trim());
            }
            String[] filePara = {"dataParamList"};
            String allparamsValues = DBUtil.select("queryParam", filePara, webId)[0][0];
            //System.out.println(allparamsValues);
            String[] parameterValues = allparamsValues.split(";");
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(workFile + "/" + webId + "/ParamValuelist.txt");
            } catch (FileNotFoundException e) {
                File ParamValuelist = new File(workFile + "/" + webId + "/ParamValuelist.txt");
                System.out.println(workFile + "/" + webId + "/ParamValuelist.txt");
                if (!ParamValuelist.exists())
                    try {
                        ParamValuelist.createNewFile();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
            }
            int num = 0;
            for (int i = 0; i < parameterValues.length; i++) {
                num = parameterValues[i].split(",").length;
                pw.append(parameterValues[i] + "\r\n");//+"\t"+clickButton+"\r\n");
                pw.flush();
                if (num > C) C = num;
                if (num != 0) N = N * num;
            }
            C = (int) (C > (Math.log(N) / Math.log(2)) ? C + 1 : (Math.log(N) / Math.log(2)));
            String[] paName = {"N", "C"};
            String[] paValue = {N + "", C + ""};
            DBUtil.update("queryParam", paName, paValue, webId);
            pw.close();
        }
    }

    //1.1
    public ArrayList<String> GetQueryLinks(String webId) {
        //1.1.1
        if (urlbase.getparamQuery().length() == 0)
            return null;
        //getInterfaces(webId);			//1.1.2
        ArrayList<String> queryLinks = null;
        getQueries(webId);//1.1.3
        if (requestMethod.equals("get")) {
            queryLinks = generate();    //1.1.4
        } else if (requestMethod.equals("post")) {
            //此处是总量信息的来源
            queryLinks = generate_json();
        }

        try {
            exit(Integer.parseInt(webId));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return queryLinks;
    }

    private static String getParamPage(HtmlPage page) {//ArrayList<HtmlPage>
        HtmlPage page1 = null;
        DomNodeList<DomElement> nodeList = page.getElementsByTagName("a");
        String paramPage = "";
        List<DomElement> inputsList = page.getElementsByTagName("input");
        DomElement btn = null;
        for (DomElement in : inputsList) {
            // if( in.hasAttribute("type")&&in.getAttribute("type").equals("submit")){
            if (in.hasAttribute("type") && in.getAttribute("type").equals("submit")) {
                //System.out.println(n.getOnClickAttribute());
                btn = in;
                break;
            }
        }
        try {
            page1 = btn.click();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // System.out.println(page1.getUrl());
        String[] param = page.getUrl().toString().split("&");
        ArrayList<String> paramList = new ArrayList<String>();
        if (page1 != null && page1.getUrl().toString().contains("&")) {
            String[] param1 = page1.getUrl().toString().split("&");
            for (String p : param)
                paramList.add(p);
            for (String p : param1)
                if (!paramList.contains(p))
                    paramPage = p.split("=")[0];
                else if (p.toLowerCase().contains("page")) {
                    paramPage = p.split("=")[0];
                    break;
                }

            //	System.out.println(paramPage);
        }
        return paramPage;
    }

    //1.1.2 get the value of queries
    private void getQueries(String webId) {
        String line = null;
        FileReader fr;
        String table = "estimate";
        String[] params = {"walkTimes"};
        String roundNum = null;
        try {
            String[][] rs = DBUtil.select(table, params, new String[]{"estiId"}, new String[]{webId});
            roundNum = rs[0][0];
        } catch (NumberFormatException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        round = Integer.parseInt(roundNum);


        if (round == 0)
            return;
        ArrayList<String[]> paramValuelist = new ArrayList<String[]>();
        try {
            paramValuelist = getParamValuelist(workFile + "/" + webId + "/ParamValuelist.txt");
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        if (round != 0) {
            try {
                fr = new FileReader(workFile + "/" + webId + "/query/queries.txt");
                BufferedReader br = new BufferedReader(fr);
                while ((line = br.readLine()) != null) {
                    String[] indexNum = line.split(",");
                    String q = "";
                    //	System.out.println("read query "+line);
                    for (int i = 0; i < indexNum.length; i++) {
                        q += paramValuelist.get(i)[Integer.parseInt(indexNum[i])] + ",";
                    }
                    queries.add(q);
                }
                fr.close();
                br.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public static ArrayList<String[]> getParamValuelist(String path) throws Exception {
        ArrayList<String[]> paramValuelist = new ArrayList<String[]>();
        InputStreamReader read = new InputStreamReader(new FileInputStream(new File(path)));
        BufferedReader br = new BufferedReader(read);
        String lineTxt;
        while ((lineTxt = br.readLine()) != null) {
            String[] paramValue = lineTxt.split(",");
            paramValuelist.add(paramValue);
        }
        br.close();
        return paramValuelist;
    }

    //1.1.3 generate queryLinks
    private ArrayList<String> generate() {
        ArrayList<String> queryLinks = new ArrayList<String>();


        String allParam = "";
        for (int i = 0; i < paramslist.size(); i++) {
            allParam += "&" + paramslist.get(i) + "=" + paramValues.get(i);
        }
        //System.out.println("allParam:"+allParam);
        String[] paramName = urlbase.getparamQuery().split(",");
        System.out.println(queries.size() != 0);
        if (queries.size() != 0) {

            for (int i = 0; i < queries.size(); i++) {
                String url = urlbase.getprefix();
                String[] paramValue = queries.get(i).split(",");
                try {
                    for (int n = 0; n < paramName.length; n++) {
                        url += URLEncoder.encode(paramName[n], "utf8") + "=" + URLEncoder.encode(paramValue[n], "utf8") + "&";
                    }
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                url = url.substring(0, url.length() - 1);
                url = url + allParam;
                WebClient client = Login.jnoa(webId);
                pageNum = getCorrectPage(url, urlbase.getparamPage(), startPageNum, stepSize, client);//333queryLink,String ,int startPageNum,int stepSize,WebClient client
                //	System.out.println("pageNum:"+pageNum);urlbase.getstartPageNum().split(",")[0]
                for (int n = startPageNum; n < pageNum; n = n + stepSize) {
                    String url1 = url + "&" + urlbase.getparamPage() + "=" + n;
                    queryLinks.add(url1);


                }
            }
        } else if (queries.size() == 0) {
            String url = urlbase.getprefix();
            //System.out.println("1:"+website.getIndexUrl());
            for (int n = 0; n < paramName.length; n++) {

                url += paramName[n] + "=" + "&";


            }
            url = url.substring(0, url.length() - 1);
            url = url + allParam;

            WebClient client = Login.jnoa(webId);

            //pageNum=3;
            pageNum = (int) getCorrectPage(url, urlbase.getparamPage(), startPageNum, stepSize, client);//333queryLink,String ,int startPageNum,int stepSize,WebClient client
            System.out.println(pageNum);
            for (int n = 1; n < pageNum; n = n + stepSize) {
                String url1 = url + "&" + urlbase.getparamPage() + "=" + n;
                queryLinks.add(url1);

            }
        }
        //System.out.println("menu urls has been generated -- "+urlsOfMenue.size()+" in total");
        return queryLinks;

        //********************************************************/
    }

    private ArrayList<String> generate_json() {
        ArrayList<String> queryLinks = new ArrayList<String>();

        JsonObject json = new JsonObject();
        for (int i = 0; i < paramslist.size(); i++) {
            json.addProperty(paramslist.get(i), paramValues.get(i));
        }
        String[] paramName = urlbase.getparamQuery().split(",");
        if (queries.size() != 0) {

            for (int i = 0; i < queries.size(); i++) {

                String[] paramValue = queries.get(i).split(",");
                int n = 0;
                for (n = 0; n < paramName.length && n < paramValue.length; n++) {
                    json.addProperty(paramName[n], paramValue[n]);
                }
                for (; n < paramName.length; n++) {
                    json.addProperty(paramName[n], "");
                }
                json.addProperty(urlbase.getparamPage(), startPageNum);


                //此处是总条数的来源。
                totalResNum = (int) getCorrectPage_post(urlbase.getprefix(), json.toString());//333queryLink,String ,int startPageNum,int stepSize,WebClient client
                System.out.println(totalResNum);
            }


        }
        return queryLinks;
    }

    private int getCorrectPage_post(String url, String json) {

        //String jsonArrayName=DBUtil.select("pattern_structed", new String[] {"xpath"},  new String[] {"webId","type"}, new String[] {webId+"","jsonArray"})[0][0];
        int page = 1;
        String[] total = jsonbase.gettotalAddress().split("/");
        int resultTotal = 0;

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        HttpResponse res = null;
        JsonObject response = null;
        try {
            StringEntity s = new StringEntity(json.toString());
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");
            //  post.setEntity(s);
            post.setHeader("Cookie", cookie);
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            post.setHeader("Accept", "application/json");
            post.setEntity(new StringEntity(json, Charset.forName("UTF-8")));
            res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.OK_200) {
                HttpEntity entity = res.getEntity();
                Gson g = new Gson();
                byte[] bytes = EntityUtils.toByteArray(entity);
                String html = new String(bytes, "utf-8");
                response = g.fromJson(html, JsonObject.class);

                for (int i = 1; i < total.length - 1; i++) {
                    response = response.getAsJsonObject(total[i]);
                }
                //page = Integer.parseInt(response.get(total[total.length - 1]).toString()) / Integer.parseInt(jsonbase.getpageSize());
                resultTotal = Integer.parseInt(response.get(total[total.length - 1]).toString());

                Scheduler.ItemSize = resultTotal + Scheduler.ItemSize;
                resultTotal = Scheduler.ItemSize;
                System.out.println("累加结果条数为 " + resultTotal);
//                DBUtil.update("estimate", new String[]{"status"}, new String[]{"stop"}, new String[]{"estiId"}, new String[]{webId});
//                DBUtil.update("estimate", new String[]{"rateBar"}, new String[]{"100.0%"}, new String[]{"estiId"}, new String[]{webId});

                EntityUtils.consume(entity);
                //  response = new JsonObject(new JsonTreeReader(new InputStreamReader(entity.getContent(),charset)));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return resultTotal;
    }

    private static int getCorrectPage(String queryLink, String paramPage, int startPageNum, int stepSize, WebClient client) {

        boolean flag = true;
        int n = 1;
        HtmlPage temp = null;
        try {
//            System.out.println("pre:"+queryLink+"&"+paramPage+"="+startPageNum);
            temp = client.getPage(queryLink + "&" + paramPage + "=" + startPageNum);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        HtmlPage page = null;
        //HtmlPage start_page=null;
        while (flag) {
            String q = queryLink + "&" + paramPage + "=" + (int) (Math.pow(2, n) * stepSize);
            try {
                //System.out.println("url:"+q);
                //System.out.println("temp:"+temp.asText());
                page = client.getPage(q);
                //System.out.println("page:"+page.asText());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (!temp.equals(null) && !page.equals(null)) {
                double s = similar(temp, page);
                //System.out.println("s: "+s);
                if (s < 0.8) {

                    temp = page;
                    n++;
                } else flag = false;
            } else if (page.equals(null)) flag = false;
        }
        n--;
        int end = (int) (Math.pow(2, n) * stepSize);
        n--;
        int start = (int) (Math.pow(2, n) * stepSize);
        //System.out.println("pre:"+start+" "+end);

        int middle = 0;


        while (start < end) {

            //中间位置
            //相当于(start+end)/2
            //中值
            middle = (start + end) >> 1;
            //System.out.println("  "+start+"  mid:"+middle+"  "+end);
            try {
                page = client.getPage(queryLink + "&" + paramPage + "=" + middle);
                //start_page=client.getPage(queryLink+"&"+paramPage+"="+start);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (similar(temp, page) > 0.95) {
                //小于中值时在中值前面找
                end = middle - 1;
                temp = page;

                //	System.out.println(middle);
            } else {
                //大于中值在中值后面找
                start = middle + 1;


            }
            //if(similar(temp,page)<0.8&&similar(start_page,page)<0.8)
        }
        client.close();
        //System.out.println("page:"+middle);
        return middle + 3;
    }

    public static double similar(HtmlPage page1, HtmlPage page2) {
        org.apache.commons.logging.impl.Jdk14Logger logger = (org.apache.commons.logging.impl.Jdk14Logger) LogFactory.getLog("org.ansj");
        logger.getLogger().setLevel(Level.OFF);
        Set<String> result = new HashSet<String>();
        String[] pageText1 = ToAnalysis.parse(page1.asText()).toString().split(",");
        String[] pageText2 = ToAnalysis.parse(page2.asText()).toString().split(",");
        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();
        set1.addAll(Arrays.asList(pageText1));
        set2.addAll(Arrays.asList(pageText2));
        double or = 0;
        double and = 0;
        result.clear();
        result.addAll(set1);
        result.retainAll(set2);
        or = result.size();
        //   System.out.println("交集："+result);
        //    System.out.println("set1："+set1);

		        /*result.clear();
		        result.addAll(set1);
		        result.addAll(set2);*/
        // and=result.size();
        // System.out.println("rate:"+or/set2.size());
        return or / set1.size();
    }

    public ArrayList<String> reRequest() {
        ArrayList<String> queryLinks = new ArrayList<String>();

        String line;
        FileReader fr;
        try {
            fr = new FileReader(workFile + "/" + webId + "/failedQueryLinks.txt");
            //getInterfaces(webId);
            BufferedReader br = new BufferedReader(fr);
            int i = 0;
            while ((line = br.readLine()) != null && !queryLinks.contains(line)) {
                //	System.out.println("read query "+line);
                queryLinks.add(line);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return queryLinks;
    }

    private void exit(int webId) throws SQLException {

    }

    // 2.2
    public void downloadLinks(int webId, ArrayList<String> newInfoLinks, int flag) {
        String page = "";
        if (flag == 0) {
            page = "mainPage";
        } else if (flag == 1) {
            page = "subpage";
        }
        this.flag = flag;

        // 2.2.2
        Map<String, Integer> map = this.download(newInfoLinks, flag, cookie);


        // 2.2.3
        try {
            //  Map<String, Integer> map1 = this.reDownload(map, flag, cookie);
            if (flag == 0)
                this.logging(webId, map);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private Map<String, Integer> reDownload(Map<String, Integer> map, int flag, String cookie) {
        ArrayList<String> queryLinks = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() < 200 || entry.getValue() >= 300)
                queryLinks.add(entry.getKey());
        }
        Map<String, Integer> remap = download(queryLinks, flag, cookie);
        for (Map.Entry<String, Integer> entry : remap.entrySet())
            map.put(entry.getKey(), entry.getValue());
        return map;
    }

    // 2.2.2 download infoLinks multithreadedly
    /*
     * 请注意此处的下载是多线程进行的。*/
    private Map<String, Integer> download(ArrayList<String> queryLinks, int flag, String cookie) {
        //flag = 0 save to html file
        //flag = 1 save to attachment file
        Downloader dowmloader = new Downloader(webId, extraConf, dedu);
        String type = "";
        if (flag == 0) {
            type = "html";
        } else if (flag == 1) {
            type = "subpage";
        }
        String[] r = {"walkTimes"};
        nowRound = (DBUtil.select("estimate", r, new String[]{"estiId"}, new String[]{webId}))[0][0];
        saveFolder = workFile + "/" + webId + "/" + type + "/" + nowRound;
        File f = new File(saveFolder);
        if (!f.exists()) {
            f.mkdirs();
        }

        Map<String, Integer> map = new HashMap<String, Integer>();
        int[] StatusCode = null;
        /*
         * 注意此处的状态码statusCode是和queryLink一一对应的，所以对于所有的queryLink(有序的)
         * 就有一个有序的状态号码的数组。因此下载连接返回了一个数组 StatusCode[]。
         * */
        if ("get".equals(requestMethod)) {
            System.out.println("get download");
            StatusCode = dowmloader.htmlDownload_GET(queryLinks, extraConf, saveFolder, workFile + "/" + webId + "/subpage/links" + nowRound + ".txt");
        } else if ("post".equals(requestMethod)) {
            System.out.println("post download");
            StatusCode = dowmloader.htmlDownload_POST(urlbase.getprefix(), queryLinks, extraConf, cookie, saveFolder);
        }
        for (int j = 0; j < queryLinks.size(); j++) {
            map.put(queryLinks.get(j), StatusCode[j]);
        }
        return map;
    }

    // 2.2.3
    private void logging(int webId, Map<String, Integer> map) throws IOException, SQLException {
        // find failedInfoLinks.txt file and infoLinkIndex according to webId
        int sNum = 0, fNum = 0;
        ArrayList<String> successfulQueryLinks = new ArrayList<String>();
        ArrayList<String> failQueryLinks = new ArrayList<String>();
        InputStreamReader ir = new InputStreamReader(new
                FileInputStream(new File(failedQueryLinksPath)));
        BufferedReader br = new BufferedReader(ir);
        String lineTxt;
        while ((lineTxt = br.readLine()) != null) {
            failQueryLinks.add(lineTxt);
        }
        br.close();

        FileWriter fr_fail = new FileWriter(
                new File(failedQueryLinksPath));
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() >= 200 && entry.getValue() < 300) {
                successfulQueryLinks.add(entry.getKey());
                sNum++;
            } else {
                boolean isDuplicate = false;
                for (String s : failQueryLinks) {
                    if (s.equals(entry.getKey())) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate) {
                    fr_fail.write(entry.getKey());
                    fr_fail.write("\r\n");
                    fr_fail.flush();
                    fNum++;
                }
            }
        }
        for (String s : failQueryLinks) {
            fr_fail.write(s);
            fr_fail.write("\r\n");
            fr_fail.flush();

        }
        fr_fail.close();
//        String round_current = DBUtil.select("estimate", new String[]{"walkTimes"}, new String[]{"estiId"},new String[]{webId+""})[0][0];
        JsonProcess.reset();
        System.out.println("***********************snum-" + sNum + "\tfnum" + fNum);
        this.exit(sNum, fNum, webId);

    }

    private void exit(int sNum, int fNum, int webId) throws SQLException {
    }

    public HashMap<String, String> getAllDataMD5() {
        return allDataMD5;
    }


    public static void main(String[] args) throws Exception {
        QueryLink q = new QueryLink("149", null, "post");


        String json = "{\"pagenumber\":1,\"pagesize\":30,\"poorproperty\":\"\",\"poorcause\":\"缺水\",\"planOutPoor\":\"\",\"realname\":\"\",\"name6\":\"\",\"basicArea\":\"\",\"txtYear\":\"\",\"Aad105\":\"\",\"isHelp\":\"\",\"isHelpPeople\":\"\",\"isImmigrant\":\"\",\"isPlan\":\"\",\"isNull\":\"0\",\"AreaType\":\"\",\"Aah006\":\"\",\"Aad003\":\"\",\"condition\":\"\",\"membercondition\":\"\",\"orders\":\"\",\"sorts\":\"\",\"poorFamilyType\":\"0\"}";
        Gson g = new Gson();
        JsonObject j = g.fromJson(json, JsonObject.class);
        q.getCorrectPage_post("http://ai.inspur.com/Archive/PoorFamilyList-GetPoorFamilyData", json);

    }
}

