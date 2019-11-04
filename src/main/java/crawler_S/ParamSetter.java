package crawler_S;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;

public class ParamSetter {
	private int threadNum = 20;
	private int timeout = 10000;
	private String charset = "gbk";
	private String workfile = "D:/crawler/";
	
	public  void initialByWebId(String webId)
	{
		/*新建estimate条目*/
		String[]pValue=new String[]{webId,0+"",1+"","0.00%"};
		DBUtil.insertIgnore("estimate",new String[]{"estiId,result,walkTimes,rateBar"},pValue);

		/*update status*/
		DBUtil.update("estimate",new String[]{"status"},new String[]{"start"}, new String[]{"estiId"},new String[]{webId});


		/*updata rateBar*/
		DBUtil.update("estimate",new String[]{"rateBar"},new String[]{"waiting"}, new String[]{"estiId"},new String[]{webId});


		 /*update pid */
		String name = ManagementFactory.getRuntimeMXBean().getName();
		String pid = name.split("@")[0];
		System.out.println("pid is "+pid);
		DBUtil.update("estimate", new String[]{"pid"}, new String[]{pid}, new String[]{"estiId"}, new String[]{webId});

	}
	//5.1.1+5.1.2
	public  boolean setInterface(String[] param, String[] paramValue ){
		
		boolean websiteParam = DBUtil.insert("website", param, paramValue);
		if(!websiteParam)return false;//if can't insert, return directly
		String webId = DBUtil.getLastWebId()+"";
		String[] filePara = {"workFile"};
		workfile = DBUtil.select("website", filePara, Integer.parseInt(webId))[0][0];

		initialByWebId(webId);//initiate database
		createNewfile( workfile, webId);
		return websiteParam;
	}
	public  void createNewfile(String workfile, String webId){

		//create work dir
		File workFilff =  Paths.get(workfile).toFile();;

		if(!workFilff.exists()){
			workFilff.mkdirs();
		}
		//subpage dir
		
		File subpage = Paths.get(workfile+"/estimate_structed/"+webId+"/subpage").toFile();;
			//System.out.println(subpage);
		if(!subpage.exists())	
			subpage.mkdirs();
		File subpageMD5 = new File(workfile+"/estimate_structed/"+webId+"/subpage/subpageMD5.txt");
		if(!subpageMD5.exists())
		{
			try {
				subpageMD5.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		//html dir
		File f = new File(workfile+"/estimate_structed/"+webId+"/html");
		if(!f.exists())
			f.mkdirs();



		f = new File(workfile+"/estimate_structed/"+webId+"/query");
		if(!f.exists())
			f.mkdir();
		File queries = new File(workfile+"/estimate_structed/"+webId+"/query/queries.txt");
		if(queries.exists())
			queries.delete();
			try {
				queries.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		File unqueries = new File(workfile+"/estimate_structed/"+webId+"/query/unusedQueries.txt");
		if(unqueries.exists())
			unqueries.delete();
			try {
				unqueries.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		File usedQueries = new File(workfile+"/estimate_structed/"+webId+"/query/usedQueries.txt");
		if(usedQueries.exists())
			usedQueries.delete();
			try {
				usedQueries.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		File logging = new File(workfile+"/estimate_structed/"+webId+"/logging.txt");
		if(!logging.exists())
			try {
				logging.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		f = new File(workfile+"/estimate_structed/"+webId+"/failedQueryLinks.txt");
		if(!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		f = new File(workfile+"/estimate_structed/"+webId+"/ParamValuelist.txt");
		if(!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		f = new File(workfile+"/estimate_structed/"+webId+"/dataMD5.txt");
		if(!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	//5.1.3
	public boolean createPattern(String webId, String patternName, String xpath, String indexPath, String type, String formula){
		String[] params = {"webId","patternName","xpath","indexPath","type","formula"};
		String[] params_value = {webId,patternName,xpath,indexPath,type,formula};
		boolean templeteParam=	DBUtil.insert("pattern", params, params_value);
		return templeteParam;
	}

	

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
	

}
