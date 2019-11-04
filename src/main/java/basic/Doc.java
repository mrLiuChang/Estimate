package basic;

import java.security.NoSuchAlgorithmException;

public class Doc {
    private String docId;
    private int docDegree;
    private int docFreq;

    public Doc(String docId, int docDegree, int docFreq) {
        this.docId = docId;
        this.docDegree = docDegree;
        this.docFreq = docFreq;
    }

    /**
     * 重写equals方法,最佳实践就是如下这种判断顺序
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Doc))
            return false;
        if (obj == this)
            return true;
        Doc aDoc = (Doc) obj;
        MD5Encoder md5Encoder = null;
        try {
            md5Encoder = new MD5Encoder();
        } catch (NoSuchAlgorithmException e) {
            e.getStackTrace();
        }

        if (md5Encoder.encode(this.getDocId()).equals(md5Encoder.encode(aDoc.getDocId()))) {
            return true;
        }
        return false;
    }


    /**
     * 重写hashCode方法
     */
    @Override
    public int hashCode() {
        String hashCodeMsg = this.getDocId();
        return hashCodeMsg.hashCode();
    }


    public String getDocId() {
        return docId;
    }

    public int getDocDegree() {
        return docDegree;
    }

    public int getDocFreq() {
        return docFreq;
    }

    public void addFreq() {
        this.docFreq++;
    }

    public void setDocDegree(int docDegree) {
        this.docDegree = docDegree;
    }

}
