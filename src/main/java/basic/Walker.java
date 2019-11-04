package basic;

import crawler_S.DBUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import utils.BaseUtils;
import utils.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;

public class Walker {
    private String linkPrefix;
    private String queryParam;
    private String keyword;
    private String pageParam;
    private String pageConf;
    private String otherParams;
    private String otherValues;
    private String querySend;
    private String linksTableXpath;
    private String contentStrXpath;
    private String curPageNum = "";
    private String resPerPage;
    private ArrayList<String> kwList = new ArrayList<>();
    private basic.DocList docList;
    private String curDocLink;
    private String contentLocation;
    private long startTime;
    private String walkTimes;
    private String estiId;

    public Walker(String linkPrefix, String queryParam, String keyword, String pageParam, String pageConf, String otherParams, String otherValues,
                  String querySend, String linksTableXpath, String contentStrXpath, String resPerPage, String contentLocation) {
        this.linkPrefix = linkPrefix;
        this.queryParam = queryParam;
        this.keyword = keyword;
        this.pageParam = pageParam;
        this.pageConf = pageConf;
        this.otherParams = otherParams;
        this.otherValues = otherValues;
        this.querySend = querySend;
        this.linksTableXpath = linksTableXpath;
        this.contentStrXpath = contentStrXpath;
        this.resPerPage = resPerPage;
        this.contentLocation = contentLocation;
        this.kwList = new ArrayList<String>();
        docList = new basic.DocList();
        this.startTime = System.currentTimeMillis();
    }

    public Walker(HashMap<String, String> walkPairs) {
        this.linkPrefix = walkPairs.get("prefix");
        this.queryParam = walkPairs.get("paramQuery");
        this.keyword = walkPairs.get("startWord");
        this.pageParam = walkPairs.get("paramPage");
        this.pageConf = walkPairs.get("startPageNum");
        this.otherParams = walkPairs.get("paramList");
        this.otherValues = walkPairs.get("paramValueList");
        this.querySend = walkPairs.get("querySend");
        this.linksTableXpath = walkPairs.get("linksXpath");
        this.contentStrXpath = walkPairs.get("contentXpath");
        this.contentLocation = walkPairs.get("contentLocation");
        this.estiId = walkPairs.get("estiId");
        this.walkTimes = walkPairs.get("walkTimes");
        this.kwList = new ArrayList<String>();
        docList = new basic.DocList();
        this.startTime = System.currentTimeMillis();
    }


    public static Walker initWalker(String estiIdStr) {


        String[] params1 = new String[]{
                "linksXpath", "contentXpath", "startWord", "walkTimes", "contentLocation", "querySend"
        };
        String[] confInfo1 = DBUtil.select(
                "estimate", params1,
                new String[]{"estiId"}, new String[]{estiIdStr})[0];

        System.out.println(Arrays.toString(confInfo1));
        String[] params2 = new String[]{
                "prefix", "paramQuery", "paramPage", "startPageNum",
                "paramList", "paramValueList"
        };
//        System.out.println(DBUtil.getDbPass());

        System.out.println("estiIdStr is "+estiIdStr);
        String[] confInfo2 = DBUtil.select(
                "urlBaseConf", params2,
                new String[]{"webId"}, new String[]{estiIdStr})[0];


        HashMap<String, String> WalkPairs = new HashMap<>();
        for (int i = 0; i < params1.length; i++) {
            WalkPairs.put(params1[i], confInfo1[i]);
        }
        for (int i = 0; i < params2.length; i++) {
            WalkPairs.put(params2[i], confInfo2[i]);
        }

        WalkPairs.put("estiId", estiIdStr);
        Walker walker = new Walker(WalkPairs);
        System.out.println("WalkerPairs are as bellow:");
        System.out.println(WalkPairs.toString());
        return walker;
    }

    public String getLinkPrefix() {
        return linkPrefix;
    }

    public void setEstiId(String estiId) {
        this.estiId = estiId;
    }

    public String setRate(int curTime, int walkTimes) {
        /*To show the RateBar*/
        double rate = (double) curTime / walkTimes;
        DecimalFormat decimalFormat = new DecimalFormat("00.00%");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String rateStr = decimalFormat.format(rate);//format 返回的是字符串
        DBUtil.update("estimate", new String[]{"rateBar"}, new String[]{rateStr}, new String[]{"estiId"}, new String[]{estiId});
        return rateStr;
    }

    public void setLinkPrefix(String linkPrefix) {
        this.linkPrefix = linkPrefix;
    }

    public String getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(String queryParam) {
        this.queryParam = queryParam;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getPageParam() {
        return pageParam;
    }

    public void setPageParam(String pageParam) {
        this.pageParam = pageParam;
    }

    public String getPageConf() {
        return pageConf;
    }

    public void setPageConf(String pageConf) {
        this.pageConf = pageConf;
    }

    public String getOtherParams() {
        return otherParams;
    }

    public void setOtherParams(String otherParams) {
        this.otherParams = otherParams;
    }

    public String getOtherValues() {
        return otherValues;
    }

    public void setOtherValues(String otherValues) {
        this.otherValues = otherValues;
    }

    public String getLinksTableXpath() {
        return linksTableXpath;
    }

    public void setLinksTableXpath(String linksTableXpath) {
        this.linksTableXpath = linksTableXpath;
    }

    public String getContentStrXpath() {
        return contentStrXpath;
    }

    public void setContentStrXpath(String contentStrXpath) {
        this.contentStrXpath = contentStrXpath;
    }

    public String getContentLocation() {
        return this.contentLocation;
    }

    public String getCurPageNum() {
        return curPageNum;
    }

    public void setCurPageNum(String curPageNum) {
        this.curPageNum = curPageNum;
    }

    public String getResPerPage() {
        return resPerPage;
    }

    public void setResPerPage(String resPerPage) {
        this.resPerPage = resPerPage;
    }

    public int getWalkTimes() {
//        String walkTimesStr = DBUtil.select("estimate", new String[]{"walkTimes"}, new String[]{"estiId"}, new String[]{estiId})[0][0];
        int walkTimes = Integer.parseInt(this.walkTimes);
        return walkTimes;
    }

    public String BuildQueryLink(String keyword, String curPageNum) {
        String QueryLink = null;
        String linkPrefix = this.getLinkPrefix();
        String queryParam = this.getQueryParam();
        String pageParam = this.pageParam;
        String pageConf = this.pageConf;
        String otherParams = this.otherParams;
        String otherValues = this.otherValues;
        String[] pageConfArray = pageConf.split(",");
        String startPage = pageConfArray[0];
        String pageInterval = pageConfArray[1];
        try {
            keyword = URLEncoder.encode(keyword, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            //ignored
        }
        if (!linkPrefix.endsWith("?")) {
            QueryLink = linkPrefix + "?";
        } else {
            QueryLink = linkPrefix;
        }

        List<String> paramList = new ArrayList<>();
        paramList.add(queryParam + "=" + keyword);

        String[] otherParamsArray = otherParams.split(",");
        String[] otherValuesArray = otherValues.split(",");
        if(otherParamsArray.length!=0) {
            if((!otherParams.equals(""))&&(!otherParams.isEmpty())){
                for (int i = 0; i < otherParamsArray.length; i++) {
                    paramList.add(otherParamsArray[i] + "=" + otherValuesArray[i]);
                }
            }
        }
        if (!curPageNum.equals("")) {
            int startNum = Integer.parseInt(startPage);//the start number of pageNum，maybe 1 or 0
            int numInterval = Integer.parseInt(pageInterval);//the interval number of different pageNum corresponding to the neighbour query link
            int pgV = (Integer.parseInt(curPageNum) - 1) * numInterval + startNum;//the final value occur in the query link
            paramList.add(pageParam + "=" + pgV);
        }

        QueryLink += StringUtils.join(paramList, "&");
        return QueryLink;
    }

    /**
     * @return the total pages num of this query.
     * @throws IOException
     */
    public int getPageNum() throws IOException, InterruptedException {
        if (this.querySend.equals("post")) {
            return new utils.PostService(this).getTotalPageNum(this.keyword);
        } else if (this.querySend.equals("get")) {
            return new utils.Service(this).getTotalPageNum(this.keyword);
        } else {
            return 0;
        }
    }

    public Document sendPostQeury(String keyword, String pageNum) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(this.queryParam, keyword);
        paramMap.put(this.pageParam, pageNum);
        //"http://www.caai.cn/index.php?s=/home/article/search.html"
        String url = this.linkPrefix;
        Document htmlDoc = BaseUtils.GetDocByPost(url, paramMap);
//        System.out.println(htmlDoc.body());
        return htmlDoc;
    }




    public Document fetchDocument(String keyword, int totalPageNum) {
        int randPageNum = Utils.GetRandNum(totalPageNum);
        if (this.querySend.equals("post")) {
            Document pageDoc = this.sendPostQeury(keyword, randPageNum + "");
            return pageDoc;
        } else {
            String QueryLink2 = this.BuildQueryLink(this.getKeyword(), randPageNum + "");
            System.out.println("QueryLink2 is " + QueryLink2);
            return BaseUtils.GetDocByUrl(QueryLink2);
        }
    }

    public String getRandTitle(Document pageDoc, String links_xpath) {
        Elements contents = pageDoc.getElementsByClass(links_xpath);
        Elements htmlContents = contents.select("a[href*=htm]");
//        System.out.println(htmlContents.size());
        ArrayList<String> linksList = new ArrayList<>();
        ArrayList<String> titlesList = new ArrayList<>();

        for (int i = 0; i < htmlContents.size(); i++) {
            linksList.add(htmlContents.get(i).attr("abs:href"));
            titlesList.add(htmlContents.get(i).text());
        }
        int randNum = Utils.GetRandNum(linksList.size()) - 1;

        this.setCurDocLink(linksList.get(randNum));
        return titlesList.get(randNum);
    }

    public String getRandText(List<String> linksList) throws IOException, InterruptedException {
        //get a rand_docLink for links_list.
        String rand_docStr = "";
        String rand_docLink = "";
        int i = 0;
        if (linksList.size() != 0) {
            do {
                try {
                    rand_docLink = linksList.get(Utils.GetRandNum(linksList.size()) - 1);
                    System.out.println(rand_docLink);
                    rand_docStr = Utils.getDocStr(rand_docLink, this.contentStrXpath);
                    i++;
                } catch (IndexOutOfBoundsException e) {

                }
                i++;
            } while (rand_docStr.equals("") && i < 3);
        }
        if (!rand_docLink.equals("")) {
            this.setCurDocLink(rand_docLink);
        }
//        if (!(rand_docStr.equals("")||rand_docStr.isEmpty())){
//            rand_docStr=
//        }
        return rand_docStr;

    }

    public String startWalk(String estiIdStr) {
        int iter_num = 0;
        int walkTimes = this.getWalkTimes();
        System.out.println("start walking");

        while (iter_num < walkTimes) {

            System.out.println("start "+iter_num+"次循环");
            setRate(iter_num, walkTimes);

            try {

                iter_num = iter_num + 1;
                /*Get a rand page link*/
                int totalPageNum = getPageNum();
                System.out.println("totalPageNum is " + totalPageNum);

                if (totalPageNum != 0) {
                    //此处整合业务逻辑，变为从关键词和总页数拿到一个随机页面的 Document pageDoc.
                    //函数名：fetchDocument
                    //函数内分为策略1，2。通过QuerySendChoice来判断。
                    Document randPage = fetchDocument(getKeyword(), totalPageNum);

                    /*Get all links of this page*/
                    List<String> docLinks = Utils.getLinks(randPage, getLinksTableXpath());
                    System.out.println("docLinks size is " + docLinks.size());

                    /*Genr a rand docLink,and fetch the docStr*/

//                String docLink = docLinks.get(Utils.GetRandNum(docLinks.size()) - 1);
//                System.out.println("docLink is "+docLink);
                    String randDocStr = "";
                    if (getContentLocation().equals("title")) {
                        randDocStr = getRandTitle(randPage, getLinksTableXpath());
                    } else {
                        randDocStr = getRandText(docLinks);
                    }

                    /*
                    Since the docStr may be null.
                    The if condition make sure we get a not null randDocStr
                    * by going into next loop with another kw*/
                    if (randDocStr.equals("")) {
                        changeWord();

                    } else {
                        //Normal arrival:
                        getKwList().add(getKeyword());
                        ArrayList<String> KwList = Utils.ExtrKwList(randDocStr);
                        setKwList(KwList);
                        int docSize = KwList.size();
                        System.out.println("docSize is " + docSize);
                        String newKw = KwList.get(Utils.GetRandNum(docSize) - 1);
                        System.out.println("newKw is " + newKw);
                        setKeyword(newKw);
                        getDocList().addDoc(new Doc(getCurDocLink(), docSize, 0));
                    }
                } else {
                    changeWord();
                }

                System.out.println("第" + iter_num + "次循环");

                setRate(iter_num, walkTimes);
                SetEstiRes();

            } catch (Exception ignored) {
                    ignored.printStackTrace();
            }
        }

        return "";
    }

    private void changeWord() {
        int keywordNum = getKwList().size();
        if (keywordNum != 0) {
            /*I store last loop's docStr
             * So I can change another word from the kwList of walker*/
            String randKw = getKwList().get(Utils.GetRandNum(keywordNum) - 1);
            setKeyword(randKw);
            System.out.println("change kw to " + randKw);
        }
    }

    public void PrintEstiRes() {
        /*
         * compute the total num
         * */
        long endTime = System.currentTimeMillis(); // 获取结束时间
        System.out.println("================================================================");
        System.out.println("程序运行时间： " + ((endTime - this.startTime) / 1000f) / 60f + "min");
        System.out.println("num of documents:" + this.getDocList().getSize());
        System.out.println("the C now is " + this.getDocList().getC());
        long sumDoc = this.getDocList().getSum();
        System.out.println("the estimated population is [" + sumDoc + "]");
        System.out.println("================================================================");
    }


    public String SetEstiRes() {
        String result = this.getDocList().getSum() + "";
        System.out.println(this.getDocList().toFreqs());
        System.out.println("current result is "+result);
        DBUtil.update("estimate", new String[]{"result"}, new String[]{result}, new String[]{"estiId"}, new String[]{estiId});
        return result;
    }

    public ArrayList<String> getKwList() {
        return kwList;
    }

    public void setKwList(ArrayList<String> kwList) {
        this.kwList = kwList;
    }

    public String getCurDocLink() {
        return curDocLink;
    }

    public void setCurDocLink(String curDocLink) {
        this.curDocLink = curDocLink;
    }

    public basic.DocList getDocList() {
        return docList;
    }

    public void setDocList(basic.DocList docList) {
        this.docList = docList;
    }

    public void setWalkTimes(String walkTimes) {
        this.walkTimes = walkTimes;
    }
}

