package model;

public class Struct {
	/**
     * the unique ID of Struct
     */
   private long webId;
   private String navValue ;
   private String iframeCon ;
   private String searchButton ;
   private String resultRow ;
   private String nextPageXPath;
   private String pageNumXPath ;
   private String iframeSubParam ;
   private String arrow ;
   private String loginButton;
   private String iframeNav;
   private String paramValueList;
   private String paramList;
   public String getparamValueList() {
       return this.paramValueList;
   }

   public void setparamValueList(String paramValueList) {
       this.paramValueList = paramValueList;
   }
   public String getparamList() {
       return paramList;
   }

   public void setparamList(String paramList) {
       this.paramList = paramList;
   }

 
    public long getWebId() {
        return this.webId;
    }

    public void setWebId(long webId) {
        this.webId = webId;
    }

    public String getnavValue() {
        return navValue;
    }

    public void setnavValue(String navValue) {
        this.navValue = navValue;
    }

    public String getiframeCon() {
        return iframeCon;
    }

    public void setiframeCon(String iframeCon) {
        this.iframeCon = iframeCon;
    }

    public String getsearchButton() {
        return searchButton;
    }

    public void setsearchButton(String searchButton) {
        this.searchButton = searchButton;
    }

    public String getresultRow() {
        return resultRow;
    }

    public void setresultRow(String resultRow) {
        this.resultRow = resultRow;
    }

    public String getnextPageXPath() {
        return nextPageXPath;
    }

    public void setnextPageXPath(String nextPageXPath) {
        this.nextPageXPath = nextPageXPath;
    }

    public String getiframeSubParam() {
        return iframeSubParam;
    }

    public void setiframeSubParam(String iframeSubParam) {
        this.iframeSubParam = iframeSubParam;
    }

    public String getpageNumXPath() {
        return pageNumXPath;
    }

    public void setpageNumXPath(String pageNumXPath) {
        this.pageNumXPath = pageNumXPath;
    }

    public String getarrow() {
        return arrow;
    }

    public void setarrow(String arrow) {
        this.arrow = arrow;
    }

    public String getloginButton() {
        return loginButton;
    }

    public void setloginButton(String loginButton) {
        this.loginButton = loginButton;
    }

    public String getiframeNav() {
        return iframeNav;
    }

    public void setiframeNav(String iframeNav) {
        this.iframeNav = iframeNav;
    }

  
}
