package utils;

import basic.Walker;

import java.io.IOException;

public class PostService {
    private Walker serviceWalker;

    public PostService(Walker walker) {
        this.serviceWalker = walker;
    }

    public Walker getServiceWalker() {
        return serviceWalker;
    }

    public void setServiceWalker(Walker serviceWalker) {
        this.serviceWalker = serviceWalker;
    }


    /**
     * judge that whether the two page are similar
     * this method use NLP method to judge
     *
     * @param doc1
     * @param doc2
     * @return
     */

    /**
     * incremental to get the first empty page number
     *
     * @param keyword
     * @return
     */
    private int incrementNum(String keyword) throws IOException, InterruptedException {
        int cur = 1;

        String preContent =this.getServiceWalker().sendPostQeury(keyword,cur+"").body().text();
        String curContent;
        while (true) {
            cur *= 2;
            curContent=this.getServiceWalker().sendPostQeury(keyword,cur+"").body().text();
            if (Service.isSimilarity(preContent, curContent))
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

        String endContent = this.getServiceWalker().sendPostQeury(keyword,endNum+"").body().text();
        while (startNum < endNum) {
            int mid = (startNum + endNum) / 2;
            String midContent = this.getServiceWalker().sendPostQeury(keyword,mid+"").body().text();
            if (Service.isSimilarity(midContent, endContent)) {
                endNum = mid;
            } else {
                startNum = mid;
            }
            if (endNum - startNum == 1) break;
        }
        return startNum;
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        String linkPrefix = "http://www.caai.cn/index.php?s=/home/article/search.html";
        String queryParam = "title";
        String pageParam = "p";
        String pageConf = "1,1";
        String keyword = "书记";
        String links_xpath = "clear";
        String pages_info_id = "";
        String content_class_name = "articleContent";
        String otherParams = "rows,flg,siteID";
        String otherValues = "10,1,4";
        String resPerPage = "10";
        String querySendChoice="";
        String contentLocation="";

        Walker walker = new Walker(
                linkPrefix, queryParam, keyword, pageParam,
                pageConf, otherParams, otherValues,
                querySendChoice, links_xpath, content_class_name, resPerPage,contentLocation);

        PostService aPoster=new PostService(walker);
        int aNum=aPoster.getTotalPageNum("智能");
        System.out.println("TotalPageNum is "+aNum);


    }
}
