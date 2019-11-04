package crawler_S;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


public class DataProcess {

	private int webId;// 涓婚敭锛宨nt鍨嬭嚜鍔ㄧ疮鍔狅紝缃戠珯鏍囪瘑
	private static int threadNum ;//=100;
	private static int timeout  ;
	private static String charset;//= "utf8";//
	private static int times = 1;
	private int flag = 0;
	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	private static HashMap<String, String> allDataMD5=null;
	private ArrayList<String> infoLinks;
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
	static FileWriter fw = null;
	
//	private static StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
//	private static IndexWriter writer;

	public DataProcess(String webId){
		this.webId = Integer.parseInt(webId);
		String[] filePara = {"workFile"};
		workFile = DBUtil.select("website", filePara, Integer.parseInt(webId))[0][0];
		//failedQueryLinksPath = workFile + webId + "/failedQueryLinks.txt";
		//infoLinkIndexPath = workFile  + "infoLinkIndex";
		String[] r = {"round"};
		nowRound =DBUtil.select("estimate", new String[]{"walkTimes"}, new String[]{"estiId"}, new String[]{webId+""})[0][0];

		try {
			 fw= new FileWriter(workFile+"/estimate_structed/"+webId+"/logging.txt",true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//	System.out.print(infoLinkIndexPath);
//		dowmloader = new Downloader(webId);
	}
	
	// ***************************************************
	public HashMap<String, String> getAllDataMD5(){
		return allDataMD5;
	}
	public int Data_process(ArrayList<String> data, String MD5Path) throws Exception {
		int type=0;
		if(MD5Path.contains("subpage"))
			type=1;
		try {
		
			fw.append("\t"+df.format(new Date())+" Data_process begin"+"\t\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		ArrayList<String> newData=new ArrayList<String>();
		System.out.println("getNewData begin:"+df.format(new Date()));
	    newData= getNewData( data, MD5Path);
		int dataSize=newData.size()-1;
		
		System.out.println("getNewData end:"+df.format(new Date()));
		 if(dataSize>0)saveNewData(newData,type); 
		 updateMD5(allDataMD5,MD5Path);
		 System.out.println("saveNewData end:"+df.format(new Date()));
		 try {		
				fw.append("\t\t"+" deduplicate Data:"+(data.size()-newData.size())+"\t\n");				
				fw.append("\t"+df.format(new Date())+" Data_process end"+"\t\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
		 try {
			exit( dataSize,data.size()-dataSize,webId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data.size()-dataSize;
	}
	public static ArrayList<String> getNewData(ArrayList<String> data, String MD5Path) throws Exception {
		HashMap<String, String> oldDataMD5=new HashMap<String, String>();
		try {
			 oldDataMD5=getDataMD5(MD5Path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//
		ArrayList<String> newData=new ArrayList<String>();
		
	    newData=deduplicate(oldDataMD5,data,MD5Path);
	    return newData;
	}

	private static HashMap<String, String> getDataMD5(String MD5Path) throws IOException {
		HashMap<String, String> MD5list = new HashMap<String, String>();
		InputStreamReader read = new InputStreamReader(new FileInputStream(new File(MD5Path)));
		BufferedReader br = new BufferedReader(read);
		String lineTxt;
		while ((lineTxt = br.readLine()) != null) {
			MD5list.put(lineTxt,"");
		}
		br.close();
		return MD5list;
	}
	public static ArrayList<String> deduplicate(HashMap<String, String> dataMD5, ArrayList<String> data, String MD5Path)throws Exception {
		ArrayList<String> newData=new ArrayList<String>();
	//	System.out.println("dep:"+data.size());
		//System.out.println("data:"+data.size());
	//	String temp="";
		if(nowRound.equals("0")){
			for(int i=0;i<data.size();i++){ 
				String md5=DigestUtils.md5Hex(data.get(i).substring(data.get(i).indexOf(",")+1, data.get(i).length()));
				dataMD5.put(md5, "");
				newData.add(data.get(i));
			}
		}
		else if(!nowRound.equals("0")&&data.size()>0){
			newData.add(data.get(0));
			for(int i=1;i<data.size();i++){
				String md5=DigestUtils.md5Hex(data.get(i).substring(data.get(i).indexOf(",")+1, data.get(i).length()));
				//	System.out.print(!dataMD5.containsKey(md5));
				if(!dataMD5.containsKey(md5)){
					dataMD5.put(md5, "");
					newData.add(data.get(i));
					//	System.out.print(!dataMD5.containsKey(md5));
				}
				//else System.out.println(data.get(i));
		}}
		System.out.println("newData："+newData.size());
		allDataMD5=dataMD5;
		return newData;
	}
	
	public static void updateMD5(HashMap<String, String> dataMD5, String MD5Path) {
		// TODO Auto-generated method stub
		PrintWriter pw=null;
		try {
			 pw=new PrintWriter(MD5Path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Iterator iter = dataMD5.entrySet().iterator();
		while (iter.hasNext()) {
		    Map.Entry entry = (Map.Entry) iter.next();
		    pw.write( entry.getKey().toString()+"\n");
		   pw.flush();
		}
		pw.close();
	}
	private void saveNewData(ArrayList<String> newData, int type) {
		// TODO Auto-generated method stub
		//System.out.print(newData.size());
		String[] para = {"webName"};
		//String[] datapara = {"dataParamList"};
		String[] roundPara = {"round"};
		 int round1= Integer.parseInt(DBUtil.select("estimate", new String[]{"walkTimes"}, new String[]{"estiId"}, new String[]{webId+""})[0][0]);

		String tableName = DBUtil.select("website", para, webId)[0][0];
		if(newData.get(0).startsWith(",")&&!newData.get(1).startsWith(",")){
			newData.set(0, "Num"+newData.get(0));
		}
		for(int i=0;i<newData.size();i++){
			if(newData.get(i).startsWith(",")){
				newData.set(i, newData.get(i).substring(1));
			}	
		}
		String[] dataParamList= newData.get(0).trim().replace(",,", ",").split(",");//DBUtil.select("queryParam", datapara, webId)[0][1].split(",");
		
		for(int i=0;i<dataParamList.length;i++){
			//System.out.println(dataParamList[i]);
			dataParamList[i]=	dataParamList[i].trim();
		}
		if(type==1)tableName+="_1";
		boolean flag=false;
		if(round1==0){
			flag=DBUtil.createTable(tableName,dataParamList);
			//System.out.print(flag);
		}
		else flag=true;
		if(round1==1)DBUtil.createTable(tableName,dataParamList);
		if(flag){
			 System.out.println("saveNewData begin:"+df.format(new Date()));
			 DBUtil.insertBatch(tableName, dataParamList, newData);
//			for(int i=1;i<newData.size();i++){
//			//	System.out.println(dataParamList+"  "+newData.get(i).split(",").length);
//			//	System.out.println(newData.get(i).split(","));
//				DBUtil.insert(tableName, dataParamList,newData.get(i).replace(",,", ",").split(","));
//			}
		}
		try {
			fw.append("\t\t"+df.format(new Date())+" add new data "+newData.size()+"\t\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}
	private void exit(int sNum, int fNum, int webId) throws SQLException {

		
		String round_current =DBUtil.select("estimate", new String[]{"walkTimes"}, new String[]{"estiId"}, new String[]{webId+""})[0][0];



		if(!round_current.equals("0"))
			Scheduler.ItemSize=Scheduler.ItemSize+sNum;

		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*	// 2.1.1
	private ArrayList<String> getInfoLinks(String infoLinksPath) throws IOException {
		// read file and return all infoLinks
		ArrayList<String> list = new ArrayList<String>();
		InputStreamReader read = new InputStreamReader(new FileInputStream(new File(infoLinksPath)));
		BufferedReader br = new BufferedReader(read);
		String lineTxt;
		while ((lineTxt = br.readLine()) != null) {
			list.add(lineTxt);
		}
		br.close();
		return list;
	}

	// 2.1.2
	public ArrayList<String> deduplicate(String infoLinkIndex, ArrayList<String> infoLinks)
			throws CorruptIndexException, IOException {
		 //int deduplicateLink=0;
		File infoLinkIndexs = new File(infoLinkIndex);
		System.out.println(infoLinkIndex);
		if(infoLinkIndexs.exists())	{
			HashSet<String> set = new HashSet<String>();
			set.addAll(infoLinks);
			Object[] links = set.toArray();
			ArrayList<String> newInfoLinks = new ArrayList<String>();
			ArrayList<String> deduplicateLink = new ArrayList<String>();
			IndexReader reader = IndexReader.open(FSDirectory.open(infoLinkIndexs));
			IndexSearcher searcher = new IndexSearcher(reader);		
			FileWriter fw = null;
			try {
				 fw= new FileWriter(workFile+"/"+webId+"/logging.txt",true);			
				fw.append("part2"+"\t\n\t"+df.format(new Date())+"deduplicate begin:"+"\t\n");
				fw.append("\t\t"+"total infoLinks number"+ links.length+"\t\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i = 0 ; i < links.length; i ++ ){
				String url = (String)links[i];		
				Query query=new TermQuery(new Term("infoLink",url));;
				BooleanQuery query1=new BooleanQuery();
				query1.add(query,Occur.MUST);
				ScoreDoc[] hits = searcher.search(query1,10000000).scoreDocs;
				//System.out.println(hits.length+"-- "+url);	
				if(hits.length==0){	
					newInfoLinks.add(url);
				//	System.out.println("-- "+url+" is new");				
				}
				else if(hits.length!=0){
					deduplicateLink.add(url);;
					//System.out.println("-- "+url+" has already been crawled.");
				}
			}
			fw.append("\t\t"+"new infoLink number:"+newInfoLinks.size()+"\t\t"+"deduplicateLink number:"+deduplicateLink.size()+"\t\n\t"+df.format(new Date())+"deduplicate end"+"\t\n");
			fw.close();
			//System.out.println("\t\t"+"deduplicateLink:"+deduplicateLink);
			searcher.close();
			return newInfoLinks;
		}
		else return infoLinks;
	}
*/
	/***************************************************

	// 2.2
	public void downloadLinks(int webId, ArrayList<String> newInfoLinks, int flag){
		// !!!!!!!!!!!------change_2鍑芥暟杩斿洖void锛岃�涓嶆槸涓嬭浇鍚庣殑html鍐呭
		FileWriter fw = null;
		try {
			 fw= new FileWriter(workFile+"/"+webId+"/logging.txt",true);			
			fw.append("\t"+df.format(new Date())+" downloadLinks begin"+"\t\n");		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.flag = flag;
		// 2.2.1
		try {
			this.getParams(webId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 2.2.2
		Map<String, Integer> map = this.download(newInfoLinks,flag);
		// 2.2.3
		try {
			this.logging(webId, map);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fw.append("\t"+df.format(new Date())+" downloadLinks end:"+"\t\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	//
	// 2.2.1 read multithread parameters from DB
	private void getParams(int webId) throws SQLException {
		// invoke DBUtil.select();
		String[] paras = { "threadNum", "timeout", "charset" };
		String[][] Param = DBUtil.select("website", paras, webId);
		threadNum = Integer.parseInt(Param[0][0]);
		timeout = Integer.parseInt(Param[0][1]);
		charset = Param[0][2];
	}

	// 2.2.2 download infoLinks multithreadedly
	private Map<String, Integer> download(ArrayList<String> InfoLinks,int flag){
		// !!!!!!!!!!!------change_1鍑芥暟杩斿洖涓嬭浇鎴愬姛鐨刪tml鐨剈rl锛岃�涓嶆槸涓嬭浇鍚庣殑html鍐呭
		//flag = 0 save to html file
		//flag = 1 save to attachment file
		Downloader	dowmloader = new Downloader(webId+"");
		String type = "";
		if(flag ==0 ){
			type = "html";
		}
		else if(flag==1){
			type = "attachment";
		}
		String[] r = {"round"};
		nowRound =(DBUtil.select("current", r , webId))[0][0]; 
		saveFolder = workFile+"/"+webId+"/"+type+"/"+nowRound;
		File f = new File(saveFolder);
		if(!f.exists()){
			f.mkdirs();
		}
		try {
			this.getParams(webId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> InfoLinks_part = new ArrayList<String>();
		int StatusCode[] = new int[threadNum];
		Map<String, Integer> map = new HashMap<String, Integer>();
		int num = 1; 
		int round = 1;
		for (int i = 0; i < InfoLinks.size(); i++) {
			if (i >= (round - 1) * threadNum && i < round * threadNum)
				InfoLinks_part.add(InfoLinks.get(i));
			else {
			//	System.out.println(i);
				StatusCode = dowmloader.htmlDownload(InfoLinks_part, threadNum, saveFolder, num, workFile+"/"+webId+"/attachment/att"+nowRound+".txt");
				for (int j = 0; j < InfoLinks_part.size(); j++) {
					map.put(InfoLinks_part.get(j), StatusCode[j]);
				}
				
				System.out.println("round " + round + " is done (every round has " + InfoLinks_part.size() +" files)");
				InfoLinks_part.clear();
				round++;
				num += threadNum;
			}
		}
		if (InfoLinks_part.size() > 0) {
			StatusCode = dowmloader.htmlDownload(InfoLinks_part, threadNum, saveFolder, num, workFile+"/"+webId+"/attachment/att"+nowRound+".txt");
			for (int j = 0; j < InfoLinks_part.size(); j++) {
				map.put(InfoLinks_part.get(j), StatusCode[j]);
			}
			
			System.out.println("round " + round + " is done (every round has " + InfoLinks_part.size() +" files)");
			InfoLinks_part.clear();
		}
		return map;
	}

	// 2.2.3
	private void logging(int webId, Map<String, Integer> map) throws IOException, SQLException {
		// find failedInfoLinks.txt file and infoLinkIndex according to webId
		int sNum = 0, fNum = 0;
		ArrayList<String> successfulQueryLinks = new ArrayList<String>();
		ArrayList<String> failQueryLinks = new ArrayList<String>();
		
		InputStreamReader ir= new InputStreamReader(new 
				FileInputStream( new File(failedQueryLinksPath)));
		BufferedReader br = new BufferedReader(ir);
		String lineTxt;
		while((lineTxt = br.readLine())!=null){
			failQueryLinks.add(lineTxt);
		}
		br.close();
		
		FileWriter fr_fail = new FileWriter(
				new File(failedQueryLinksPath));
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			if (entry.getValue() >= 200 && entry.getValue() < 300) {
				successfulQueryLinks.add(entry.getKey());
				sNum++;
			} else {
				boolean isDuplicate = false;
				for(String s:failQueryLinks){
					if(s.equals(entry.getKey())){
						isDuplicate = true;
						break;
					}
				}
				if(!isDuplicate){
					fr_fail.write(entry.getKey());
					fr_fail.write("\r\n");
					fr_fail.flush();
					fNum++;
				}
			}
		}
		for(String s:failQueryLinks){
			fr_fail.write(s);
			fr_fail.write("\r\n");
			fr_fail.flush();
			
		}
		fr_fail.close();
		// 2.2.4
		//this.updateInfoLinkIndex(successfulInfoLinks, infoLinkIndexPath);
		// 2.2.5
		// oooooooops, I just can't figure out where the method is ....
		try {
			FileWriter fw= new FileWriter(workFile+"/"+webId+"/logging.txt",true);
			fw.append("\t\t"+"download snum:"+sNum+"\tfnum:"+fNum+"\t\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("***********************snum-"+sNum+"\tfnum"+fNum);
		this.exit(sNum, fNum, webId);
	}

	/* //2.2.4
	private void updateInfoLinkIndex(ArrayList<String> successfulInfoLinks, String infoLinkIndex) throws IOException {
		// use standard analyzer
		File infoLinkIndexs = new File(infoLinkIndex);
		System.out.println(infoLinkIndex);
		if(infoLinkIndexs.exists())	{
			System.out.println(infoLinkIndex);
			FSDirectory location = FSDirectory.open(infoLinkIndexs);
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
			writer = new IndexWriter(location, config);
	
			int numDocs = writer.numDocs();
			for (String InforLink : successfulInfoLinks) {
				try {
					Document doc = new Document();
				//	System.out.println("****"+InforLink);
					doc.add(new Field("infoLink", InforLink, Field.Store.YES, Index.NOT_ANALYZED));
					doc.add(new Field("time", df.format(new Date()), Field.Store.YES, Index.NOT_ANALYZED));
					writer.addDocument(doc);
					//System.out.println("Added " + InforLink);
				} catch (Exception e) {
					System.out.println("Couldn't add " + InforLink);
				}
			}
			int newNumDocs = writer.numDocs();
			IndexReader reader = IndexReader.open(FSDirectory.open(new File(infoLinkIndex)));
		//	System.out.print(reader.document(newNumDocs-2));
			System.out.println("");
			System.out.println("########################");
			System.out.println((newNumDocs - numDocs) + " records added");
			System.out.println("########################");
			writer.optimize();
			writer.forceMerge(1);
			writer.commit();
			writer.close();
		}
		else if(!infoLinkIndexs.exists())	{
			Directory dir = null;
			IndexWriter writer = null;
			try {
				dir = FSDirectory.open(new File(workFile+"/infoLinkIndex"));
				//NlpirAnalyzer analyzer = new NlpirAnalyzer();
				StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
				IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_36,analyzer);
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
				writer = new IndexWriter(dir, iwc);
				for (String InforLink : successfulInfoLinks) {
					try {
						Document doc = new Document();
					//	System.out.println("****"+InforLink);
						doc.add(new Field("infoLink", InforLink, Field.Store.YES, Index.NOT_ANALYZED));
						doc.add(new Field("time", df.format(new Date()), Field.Store.YES, Index.NOT_ANALYZED));
						writer.addDocument(doc);
						//System.out.println("Added " + InforLink);
					} catch (Exception e) {
						System.out.println("Couldn't add " + InforLink);
					}
				}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Couldn't build index for "+"infoLink");
					e.printStackTrace();
				}
				finally{
					writer.optimize();
					writer.close();
				}
		}
	}*/

	// 2.2.5
	

	// ***************************************************

	/*// 2.3
	public void reDownloadLinks(int webId) {
		// !!!!!!!!!!!------change_2鍑芥暟杩斿洖void锛岃�涓嶆槸涓嬭浇鍚庣殑html鍐呭
		// 2.3.1
		ArrayList<String> failedLinks;
		try {
			// 2.3.2
			failedLinks = this.getFailedList(failedInfoLinksPath);
			
			// 2.3.3
			Map<String, Integer> map = this.download(failedLinks, 0);
			this.logging(webId, map);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		
	}

	// 2.3.1
	private ArrayList<String> getFailedList(String failedInfoLinksPath) throws IOException{

		ArrayList<String> list = new ArrayList<String>();
		InputStreamReader read = new InputStreamReader(new FileInputStream(new File(failedInfoLinksPath)));
		BufferedReader br = new BufferedReader(read);
		String lineTxt;
		while ((lineTxt = br.readLine()) != null) {
			list.add(lineTxt);
		}
		br.close();
		list = this.deduplicate(infoLinkIndexPath, list);
		return list;
	}
	*/
	// ***************************************************

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String data="633,ttyyh";
		/*new PrintStream(new File(Controller_Structured.workFile+"/"+webId+"/output.txt"), "utf-8");/*DataProcess iLinks = new DataProcess("1");
		ArrayList<String> info = new ArrayList<String> ();
		info.add("12345");	info.add("12346");	info.add("34125");
		HashMap<String,String> oldDataMD5=new HashMap<String,String>();
		try {
			 oldDataMD5=getDataMD5("E:/MD5.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//
		ArrayList<String> newData=deduplicate(oldDataMD5,info,"E:/MD5.txt");
		for(String s:newData){
			System.out.println(s);
		}*/
	//	iLinks.saveNewData(infoLinks);
		/*ArrayList<String> newInfoLinks = null;
		ArrayList<String> infoLinks = new ArrayList<String> ();
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
		ArrayList<String> InfoLinks = new ArrayList<String>();
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
