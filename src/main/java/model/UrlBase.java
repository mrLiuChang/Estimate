package model;

public class UrlBase {
    private long id;

    private long webId;

    private String paramQuery;

    
    private String paramPage;

   
    private String startPageNum;

   
    private String prefix;

    
    private String paramList;
    
    private String paramValueList;
    
   

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getWebId() {
        return webId;
    }

    public void setWebId(long webId) {
        this.webId = webId;
    }

   

    public String getparamQuery() {
        return paramQuery;
    }

    public void setparamQuery(String paramQuery) {
        this.paramQuery = paramQuery;
    }

    public String getparamPage() {
        return paramPage;
    }

    public void setparamPage(String paramPage) {
        this.paramPage = paramPage;
    }

    public String getstartPageNum() {
        return startPageNum;
    }

    public void setstartPageNum(String startPageNum) {
        this.startPageNum = startPageNum;
    }

    public String getprefix() {
        return prefix;
    }

    public void setprefix(String prefix) {
        this.prefix = prefix;
    }

    public String getparamList() {
        return paramList;
    }

    public void setparamList(String paramList) {
        this.paramList = paramList;
    }

    public String getparamValueList() {
        return paramValueList;
    }

    public void setparamValueList(String paramValueList) {
        this.paramValueList = paramValueList;
    }

  
}
