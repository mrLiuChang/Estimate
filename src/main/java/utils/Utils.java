package utils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;

public class Utils {



    /**
     * @param url
     * @param docXpath
     * @return
     */
    public static String getDocStr(String url, String docXpath) throws IndexOutOfBoundsException{
        Document doc = BaseUtils.GetDocByUrl(url);
        Elements contents = doc.getElementsByClass(docXpath);
        String docStr="";
        if (contents.size()!=0){
        docStr= contents.get(0).text();
        }else {
            docStr=doc.body().text();
        }
        return  docStr;
    }


    /**
     * @param url
     * @param link_xpath
     * @return linksList ArrayList<String>
     * @throws IOException
     */
    public static ArrayList<String> getLinks(String url, String link_xpath) throws IOException {
        Document pageDoc = BaseUtils.GetDocByUrl(url);
        Elements contents = pageDoc.getElementsByClass(link_xpath);
        ArrayList<String> linksList = new ArrayList<>();
        for (int i = 0; i < contents.size(); i++) {
            Elements targetElements = contents.get(i).select("a");
            linksList.add(targetElements.get(0).attr("abs:href"));
        }
        return linksList;
    }

    /**
     * @param pageDoc
     * @param links_xpath
     * @return linksList ArrayList<String>
     * @throws IOException
     */
    public static ArrayList<String> getLinks(Document pageDoc, String links_xpath) throws IOException {
        Elements contents = pageDoc.getElementsByClass(links_xpath);
        Elements htmlContents = contents.select("a[href*=htm]");
        ArrayList<String> linksList = new ArrayList<>();
        for (int i = 0; i < htmlContents.size(); i++) {
//            System.out.println("contents size is " + htmlContents.size());
//            System.out.println(htmlContents.get(i).html());
//            Elements targetElements = htmlContents.get(i).select("a[href*=htm]");
//            System.out.println(targetElements.html());
            linksList.add(htmlContents.get(i).attr("abs:href"));
        }
        return linksList;
    }


    /**
     * @param url
     * @return String bodyStr
     * @throws IOException
     * @throws InterruptedException
     */
    public static String getBodyByUrl(String url) throws IOException, InterruptedException {
        Document doc = BaseUtils.GetDocByUrl(url);
        Element body = doc.body();
        String bodyStr = body.text();
        return bodyStr;
    }

    /**
     * @param totalNum
     * @return int totalNum
     */
    public static int GetRandNum(int totalNum) {
        return (int) (Math.random() * totalNum) + 1;
    }

    /**
     * @param docStr
     * @return The unique words number of a doc str.
     * @throws IOException
     */
    public static int getDocSize(String docStr) throws IOException {
        HashSet<String> hSet = BaseUtils.Wash(docStr);
        return hSet.size();
    }

    /**
     * @param docStr
     * @return The unique words of a doc str.
     */
    public static ArrayList<String> ExtrKwList(String docStr) {
        HashSet<String> hSet = null;
        try {
            hSet = BaseUtils.Wash(docStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert hSet != null;
        ArrayList<String> keywordList1 = new ArrayList<String>(hSet);
        return keywordList1;
    }




    public static void main(String[] args) throws IOException {
//        String url = "http://www.cufe.edu.cn/cms/search/searchResults.jsp?query=%25E6%2595%2599%25E5%258A%25A1%25E5%25A4%2584&siteID=4&offset=10&rows=10&flg=1";
//        ArrayList<String> links = getLinks(BaseUtils.GetDocByUrl(url), "con03");
//        String dbUrl=args[0];
//        String dbUser=args[1];
//        String dbPass=args[2];
//        String estiId=args[3];
//        String jarPath="E:\\0\\dc-web-1\\TestJar1\\target\\import_tool-jar-with-dependencies.jar";
//        ProcessBuilder builder = new ProcessBuilder("java","-Xmx1024m","-Xms256m","-jar",jarPath, dbUrl,dbUser,dbPass,estiId);
        String keyword="智能";
        try {
            keyword = URLEncoder.encode(keyword, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            //ignored
        }
        System.out.println(keyword);


//
//        File logFile = Paths.get("logFile.txt").toFile();
//        File logErr = Paths.get("logErr.txt").toFile();
//        if(logFile.exists()) {
//            logFile.delete();
//        }
//        if(logErr.exists()) {
//            logErr.delete();
//        }
//
//        builder.redirectOutput(logFile);
//        builder.redirectError(logErr);
//        Process p = builder.start();

    }
}
