package model;

public class JsonBase {
    private long id;

    private long webId;

   

    
    private String pageSize;

 
    
    private String totalAddress;
    private String contentAddress;

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

    public String getcontentAddress() {
        return contentAddress;
    }

    public void setcontentAddress(String contentAddress) {
        this.contentAddress = contentAddress;
    }
    public String gettotalAddress() {
        return totalAddress;
    }

    public void settotalAddress(String totalAddress) {
        this.totalAddress = totalAddress;
    }


  
    public String getpageSize() {
        return pageSize;
    }

    public void setpageSize(String pageSize) {
        this.pageSize = pageSize;
    }

  
  
}
