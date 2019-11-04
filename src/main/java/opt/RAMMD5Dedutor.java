package opt;


import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/**
 * 根据md5去重的内存去重器
 */
public class RAMMD5Dedutor  {
    private Set<String> deduSet;
    private MessageDigest md5;
    private Path dataPath; //数据存储文件夹
    protected int newV;
    protected int costV;
    protected final String DATA_FILE_NAME = "dedu.dat";
    
    /**
     * 不使用数据存储文件
     */
    public RAMMD5Dedutor() {
        deduSet = new HashSet<>();
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex){
         //   //logger.error("can't find the md5 algorithm");//此错误不可能发生
        }
    }

    /**
     * 使用数据存储文件
     * @param dataPath
     */
    public RAMMD5Dedutor(Path dataPath){
    	
        this.dataPath = dataPath;
        deduSet = null;
        File f  = dataPath.resolve(DATA_FILE_NAME).toFile();
        if (f.exists()) {//如果对象文件存在
            //logger.info("read dedu from file {}", f.getAbsolutePath());
            try(ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(f))) {
                deduSet = (Set<String>) inputStream.readObject();
            } catch (Exception ex) {
                //logger.error("Exception happen when read dedu object file");
            } finally {
                f.delete();
            }
        }
        /**
         * 如果读取失败，则忽略此文件的存在
         */
        if (deduSet == null) {
            deduSet = new HashSet<>();
        }
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex){
            //logger.error("can't find the md5 algorithm");//此错误不可能发生
        }
    }

    /**
     * 通过简单加锁保证线程安全
     * @param o
     * @return
     */
    
    public synchronized boolean add(String o) {
        costV++;
        if(o!=null){
	        byte[] osmd5 = md5.digest(o.getBytes());
	        String hexMd5 = DatatypeConverter.printHexBinary(osmd5);
	        if (deduSet.add(hexMd5)) {
	            newV++;
	            return true;
	        }
	    }
        return false;
    }

    
    public int getTotal() {
        return deduSet.size();
    }

    /**
     * 用于去重对象的收尾操作
     * @throws IOException
     */
    
    public void close() throws IOException {
        if (dataPath == null) return;
        String fileName = DATA_FILE_NAME;
        File f = this.dataPath.resolve(fileName).toFile();
        if (f.exists()) {//如果该轮次的备份文件已经存在，删除
            f.delete();
        }
        //直接写入
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(f))) {
            outputStream.writeObject(deduSet);
        }
    }
}
