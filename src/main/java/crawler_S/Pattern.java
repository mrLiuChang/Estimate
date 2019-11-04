package crawler_S;

import opt.RAMMD5Dedutor;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//import nlpir.NlpirAnalyzer;


public class Pattern {
	private static String workFile = "D:/crawler/";
	private static String charset = "utf-8";
   
	// 3.1
	public void buildPatterns(int webId, String htmlPath, RAMMD5Dedutor dedu) {
		
		workFile = DBUtil.select("website",new String[] {"workFile"}, webId)[0][0];
		charset= DBUtil.select("extraConf", new String[] {"charset"}, webId)[0][0];
	
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//璁剧疆鏃ユ湡鏍煎紡

		String[][] patterns = getPatterns(webId,"userDefined");//"patternName", "xpath","headerXPath"
		
		
		for (int i = 0; i < patterns.length; i++) {
			//System.out.println(patterns[i][0]+" "+patterns[i][1]+" "+patterns[i][2]);			
				buildPattern(webId,patterns[i][0],patterns[i][1],patterns[i][2],htmlPath);	
		}
		String[][] tablepatterns = getPatterns(webId,"json");
		for (int i = 0; i < tablepatterns.length; i++) {
		//	System.out.println(tablepatterns[i][0]+" "+tablepatterns[i][1]);			
			bulidtablePattern(webId,tablepatterns[i][0],tablepatterns[i][1],htmlPath,dedu);	
		}
		
		exit(webId);

		
		
	}
	public void buildPattern(int webId, String patternName, String xpath, String headerXpath, String html){
		ArrayList<String> patternData=new ArrayList<String>();

		File direc = new File(html);
		File[] htmls = direc.listFiles();

		String headerContent = "";
		for (int i = 0; i < htmls.length; i++) {				
				String content="";
				
				if(xpath.contains("#")&&headerXpath.contains("#")){
					String[] value = xpath.split("#");
					String[] header = headerXpath.split("#");
					for (int j = 0; j < header.length; j++) {
						content += extractHtml(value[j], htmls[i].getPath()).replaceAll("[\\pP|~|$|^|<|>|\\||\\+|=|\"|']*", "").trim()+",";
						if(i==0)headerContent+=extractHtml(header[j], htmls[i].getPath()).replaceAll("[\\pP|~|$|^|<|>|\\||\\+|=|\"|']*", "").trim()+",";
					}//System.out.println(content);
				}	
				else if(!xpath.contains("#")){
					if(i==0)	
						headerContent = extractHtml(xpath,htmls[i].getPath()).replaceAll("[\\pP|~|$|^|<|>|\\||\\+|=|\"|']*", "").trim();
					content = extractHtml(headerXpath, htmls[i].getPath()).replaceAll("[\\pP|~|$|^|<|>|\\||\\+|=|\"|']*", "").trim();
				}		
				content=content.replace(",,", ",null,");
				headerContent.replace(",,", ",null,");
				if(headerContent.length()>0&&!patternData.contains(content)){
					if(i==0)patternData.add(headerContent);
					patternData.add(content);
					//System.out.println(content);
					//DBUtil.insert(patternName,  headerContent.split(","),content.split(","));
				}
		}
		if(patternData.size()>0)
			savePatternData(patternName+webId,patternData);
		exit(webId);
		
	}
	private void savePatternData(String tableName, List<String> patternData) {
		// TODO Auto-generated method stub
		//System.out.print(patternData.size());
		//String[] datapara = {"dataParamList"};
		String[] dataParamList= patternData.get(0).split(",");//DBUtil.select("queryParam", datapara, webId)[0][1].split(",");
		for(int i=0;i<dataParamList.length;i++){
			dataParamList[i]=dataParamList[i].replaceAll("[\\pP|~|$|^|<|>|\\||\\+|=|\"|']*", "").trim();
		}
		patternData.remove(0);
		boolean flag=false;
		flag= DBUtil.createTable(tableName,dataParamList);
		if(flag)		
			DBUtil.insertBatch(tableName, dataParamList, patternData);
	}
	// 3.1.1
		public static String[][] getPatterns(int webId, String type) {
			// read pattern information from DB to 'patterns'
			String[][] patterns = null;
			String table = "pattern_structed";
			String params[] = {"patternName", "xpath","headerXPath" };
			String[] con = {"webId","type"};
			String[] conV = {webId+"",type};
			patterns = DBUtil.select(table, params, con,conV);
			return patterns;
		}


		//锟截讹拷xpath锟斤拷应锟斤拷锟斤拷
		private String extractHtml(String xpath, String fpath) {
			if(xpath.contains("/html")){
				xpath = xpath.replace("/html", "");
			}
			
			String text = "";
			HtmlCleaner hc = new HtmlCleaner();
			TagNode tagNode = null;
			try {
				tagNode = hc.clean(new File(fpath), charset );
			} catch (IOException e) {
				System.out.println("html锟侥硷拷锟斤拷取锟斤拷锟斤拷");
				e.printStackTrace();
			}
			Object[] tbodyNodeArray = null;
			try {
				tbodyNodeArray = tagNode.evaluateXPath(xpath);
			} catch (XPatherException e) {
				System.out.println("xpath锟斤拷锟斤拷");
				e.printStackTrace();
			}
			for (Object tbodyNode : tbodyNodeArray) {
				TagNode tbody = (TagNode) tbodyNode;
				if(xpath.contains("input"))
					text = tbody.getAttributeByName("value").toString().trim();
				else text = tbody.getText().toString().trim();
				//System.out.println("\ttext=" + text);
			}
			
			return text;
		}


	
		
	
	// 3.1.4
	private void exit(int webId) {
		// invoke DBUtil.update

	}
	
	
	public void bulidtablePattern(int webId, String patternName, String json, String htmlpath, RAMMD5Dedutor dedu){
		try {
			
			List<String> data=TablePattern.getPatternData(htmlpath, json);
		
			List<String> newData =data.stream().filter(d-> {//remove the repeated data
            	return dedu.add(d);
		}).collect(Collectors.toList());
			savePatternData(patternName+webId,newData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*public static String content(org.jsoup.nodes.Document docment) {
		
		try {
			String body = docment.text().replace(Jsoup.parse("&nbsp;").text(), " ").replaceAll("\\s+([\u4e00-\u9fa5])", "$1").replaceAll("\\s+", " ");
			if (!"".equals(body.replaceAll("\\s*", "")) && body != null)
				return body.trim();
			else
				return "";
		}
		catch (Exception e){
			e.printStackTrace();
			return "";
		}
		
	}*/
	public static void main(String[] args){
		Pattern p = new Pattern();
		//(int webId,String patternName, String xpath,String headerXpath,String html)
		//p.buildPattern(4, "poorfamily", "/html/body/table[1]/tbody/tr[2]/td/div[2]/table/tbody/tr[1]/td[2]/input#/html/body/table[1]/tbody/tr[2]/td/div[2]/table/tbody/tr[3]/td[4]/input", "/html/body/table[1]/tbody/tr[2]/td/div[2]/table/tbody/tr[1]/td[1]#/html/body/table[1]/tbody/tr[2]/td/div[2]/table/tbody/tr[3]/td[3]", "F:/test_table/2_old/2_2(榛樿妯″紡鍏�)/subpage/3");
		p.buildPatterns(138,"E:\\table\\poverty\\138\\subpage\\0", null);
		//p.bulidtablePatt8ern(81, "tyh", "pcObj", "F:\\table\\81\\subpage\\0");//(, "F:\\table\\81\\subpage\\0");//System.out.println(iDealer.extractHtml_text("D:/a.html"));
		//iDealer.buildPatternIndex(4, "D:/crawler/4/html");
		
	}

}

