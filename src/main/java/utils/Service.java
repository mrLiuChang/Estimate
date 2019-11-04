package utils;

import org.ansj.splitWord.analysis.NlpAnalysis;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Service {
    private basic.Walker serviceWalker;

    public Service(basic.Walker walker) {
        this.serviceWalker = walker;
    }

    public String buildQueryLink(String keyword, int cur) {
        String queryLink = this.serviceWalker.BuildQueryLink(keyword, cur + "");
        return queryLink;
    }

    /**
     * judge that whether the two page are similar
     * this method use NLP method to judge
     *
     * @param doc1
     * @param doc2
     * @return
     */
    public static boolean isSimilarity(String doc1, String doc2) {
        Set<String> result = new HashSet<>();
        String[] page1 = NlpAnalysis.parse(doc1).toString().split(",");
        String[] page2 = NlpAnalysis.parse(doc2).toString().split(",");
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();
        set1.addAll(Arrays.asList(page1));
        set2.addAll(Arrays.asList(page2));
        double or = 0;
        result.addAll(set1);
        result.retainAll(set2);
        or = (double) result.size() / set1.size();
        return or > 0.95;
    }

    /**
     * incremental to get the first empty page number
     *
     * @param keyword
     * @return
     */
    private int incrementNum(String keyword) throws IOException, InterruptedException {
        int cur = 1;
        String testURL = buildQueryLink(keyword, cur);
        String preContent = Utils.getBodyByUrl(testURL);
        String curContent;
        while (true) {
            cur *= 2;
            testURL = buildQueryLink(keyword, cur);
            curContent = Utils.getBodyByUrl(testURL);
            if (isSimilarity(preContent, curContent))
                break; //if current page is similar with the pre page, it seems that this two page are empty pages
            preContent = curContent;
        }
        return cur / 2;//return this first empty page number
    }

    /**
     * confirm the total number of query link corresponding to the specified keyword
     *
     * @param keyword
     * @return
     */
    public int getTotalPageNum(String keyword) throws IOException, InterruptedException {
        int endNum = this.incrementNum(keyword);//incremental to get the first empty page
        if (endNum == 1) return 0;
        int startNum = endNum / 2;
        return getEndPageNum(startNum, endNum, keyword);
    }

    private int getEndPageNum(int startNum, int endNum, String keyword) throws IOException, InterruptedException {

        String endContent = Utils.getBodyByUrl(buildQueryLink(keyword, endNum));
        while (startNum < endNum) {
            int mid = (startNum + endNum) / 2;
            String midContent = Utils.getBodyByUrl(buildQueryLink(keyword, mid));
            if (isSimilarity(midContent, endContent)) {
                endNum = mid;
            } else {
                startNum = mid;
            }
            if (endNum - startNum == 1) break;
        }
        return startNum;
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        String linkPrefix = "http://www.zhaoan.gov.cn/cms/siteresource/search.shtml?";
        String queryParam = "key";
        String pageParam = "page";
        String pageConf = "1,1";
        String keyword = "县委书记";
        String links_xpath = "wz-list";
        String pages_info_id = "";
        String content_class_name = "zw";
        String otherParams = "searchSiteId,siteId,pageName";
        String otherValues = "60427348114130001,60427348114130001,quickSiteSearch";
        String resPerPage = "10";
        String querySendChoice = "post";
        String contentLocation = "";

        /*
         * ###############################################################
         * */
//        String linkPrefix = "http://www.nanyang.gov.cn/search/searchResultGJ.jsp?";
//        String queryParam = "q";
//        String pageParam = "p";
//        String pageConf = "1,1";
//        String keyword = "书记";
//        String links_xpath = "tpxw";
//        String pages_info_id = "";
//        String content_class_name = "";
//        String pageBarXpath = "";
//        String otherParams = "t_id,site_id";
//        String otherValues = "41,CMSnany";
//        String resPerPage = "10";
        basic.Walker walker2 = new basic.Walker(linkPrefix, queryParam, keyword, pageParam, pageConf, otherParams, otherValues, querySendChoice, links_xpath, content_class_name, resPerPage, contentLocation);

        /*
         * The way to get the total page num
         * */
        for (int i = 0; i < 100; i++) {
            int a = (int) (Math.random() * 5000L);
            System.out.println(a);
            Thread.sleep(a);
            Service myService = new Service(walker2);
            int totalNum = myService.getTotalPageNum("的");
            System.out.println("totalNum" + totalNum);
        }
    }


}
