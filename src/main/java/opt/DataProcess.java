package opt;

import crawler_S.DBUtil;
import crawler_S.Scheduler;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class DataProcess {

	private int webId;
	private static int threadNum ;//=100;
	private static int timeout  ;
	private static String charset;//= "utf8";//
	private static int times = 1;
	private int flag = 0;
	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	private static HashMap<String, String> allDataMD5=null;
	private List<String> infoLinks;
//	private Downloader dowmloader ;
	
	/*
	 * it's my own path it should be changed by the machine
	 */
	private static String nowRound = ""; //round number ;
	private static String workFile = "D:/crawler/";
	private static String saveFolder ;
//	private static String saveFolder = workpath + "/infoLinks/" + "1-1" +"/";
	private static String failedQueryLinksPath = "";
	private static String infoLinkIndexPath = "";
	public static int count = 0;
	
	public DataProcess(String webId){
		this.webId = Integer.parseInt(webId);
		String[] filePara = {"workFile"};
		workFile = DBUtil.select("website", filePara, Integer.parseInt(webId))[0][0];
		String[] r = {"round"};
		nowRound =DBUtil.select("estimate", new String[]{"walkTimes"}, new String[]{"estiId"}, new String[]{webId+""})[0][0];

		String[] pa = {"SampleData_sum"};
		int SampleData_sum = Scheduler.ItemSize;
	    inc(SampleData_sum);
	}
	
	// ***************************************************
//	public HashMap<String, String> getAllDataMD5(){
//		return allDataMD5;
//	}
	public synchronized static void inc(int num) {
		count+=num;
		}
	public int Data_process(List<String> data, String MD5Path, RAMMD5Dedutor dedu) throws Exception {
		String title=data.get(0);
		
		data.remove(0);
		
		int type=0;
		if(MD5Path.contains("subpage"))
			type=1;
		
		if(data.isEmpty())
			return 0;
		List<String> newData = data.stream().filter(d-> {//remove the repeated data
            	return dedu.add(d);
		}).collect(Collectors.toList());
		
//
		int dataSize=newData.size();
		 try {
			//System.out.println(data.size()+"-"+dataSize);
			exit( dataSize,data.size()-dataSize,webId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dataSize;
	}

	private void exit(int sNum, int fNum, int webId) throws SQLException {

	}



	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String data="633,ttyyh";
		/*new PrintStream(new File(Controller_Structured.workFile+"/"+webId+"/output.txt"), "utf-8");/*DataProcess iLinks = new DataProcess("1");
		List<String> info = new List<String> ();
		info.add("12345");	info.add("12346");	info.add("34125");
		HashMap<String,String> oldDataMD5=new HashMap<String,String>();
		try {
			 oldDataMD5=getDataMD5("E:/MD5.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//
		List<String> newData=deduplicate(oldDataMD5,info,"E:/MD5.txt");
		for(String s:newData){
			System.out.println(s);
		}*/
	//	iLinks.saveNewData(infoLinks);
		/*List<String> newInfoLinks = null;
		List<String> infoLinks = new List<String> ();
		infoLinks.add("http://jtzb.cecic.corp/jtzb/dzbx.nsf/3c33479f138922674825788e003be426/b4067354d27127dd482580ce0025004d?OpenDocument&view=undefined&start=undefined&count=undefined&Highlight=0");
		try {
			newInfoLinks = iLinks.deduplicate( workFile+"22/infoLinkIndex",infoLinks);
			iLinks.downloadLinks(Integer.parseInt("22"), newInfoLinks, 0);
		} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		InfoLinks info = new InfoLinks();
		List<String> InfoLinks = new List<String>();
		for(int pageindex=1; pageindex<10;pageindex++ ){
			String url = "http://search.ccgp.gov.cn/dataB.jsp?searchtype=2&page_index=" + pageindex
					+ "&bidSort=0&buyerName=&projectId=&pinMu=0&bidType=0&dbselect=bidx&kw=&start_time=2016%3A04%3A01&end_time=2016%3A06%3A30&timeType=6&displayZone=&zoneId=&pppStatus=&agentName=";
			// get the index page
			String menuHtml = Downloader.getPage(url);
			// extract all urls on the index page
			Vector<String> urls =Downloader.extractUrls(menuHtml);
			for(String u:urls){
				System.out.println(u);
				InfoLinks.add(u);
			}
		}
		System.out.println("InfoLinks size is :\t"+InfoLinks.size());
		Map<String, Integer> map = info.download(InfoLinks);
		Iterator<Map.Entry<String, Integer>> it= map.entrySet().iterator();
		while (it.hasNext()) {  
		    Map.Entry<String, Integer> entry = it.next();  
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  
		  
		}  
		*/
	}

}
