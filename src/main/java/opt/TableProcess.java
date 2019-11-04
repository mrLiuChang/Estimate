package opt;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import crawler_S.DBUtil;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;


public class TableProcess {
	public static int index = 0;
	//HashMap<String,ArrayList<String>> table=new HashMap<String,ArrayList<String>>();
	//static ArrayList<String> result=new ArrayList<String>();
	static ArrayList<String> formula=new ArrayList<String>();
	static HashMap<String, String> path=new HashMap<String, String>();
	public TableProcess(String[] con, String[] conV){
		String[] fPara = {"formula"};
		String[] Para = {"patternName","xpath"};
		String[][] pathParas = DBUtil.select("pattern_structed", Para, con,conV);
		for(int i=0;i<pathParas.length;i++){
			this.path.put(pathParas[i][0], pathParas[i][1]);
		}
		char[] fParas = DBUtil.select("pattern_structed",fPara,con,conV)[0][0].toCharArray();
		this.formula.add("(");
		for(int i=0;i<fParas.length;i++){
			this.formula.add(fParas[i]+"");
		}
		this.formula.add(")");
	}
	public static ArrayList<String> getAllTable(String txt, String scheme){
		WebClient client=WebClientinitialValue();
		
		HtmlPage page = null;
		try {
			page = HTMLParser.parseHtml(new StringWebResponse(txt, new URL(scheme+"localhost/")), client.getCurrentWindow());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashMap<String, ArrayList<String>> table=return_table(page, path);
		client.close();
		return  explain_formula(formula,table);
	}
	public static WebClient WebClientinitialValue() {
        WebClient client = new WebClient(BrowserVersion.BEST_SUPPORTED);
        client.getOptions().setCssEnabled(false);//headless browser no need to support css
        client.getOptions().setDownloadImages(false);//headless browser no need to support download imgs
        client.getOptions().setJavaScriptEnabled(true);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);//wouldn't log when access error
        client.getOptions().setThrowExceptionOnScriptError(false);//wouldn't log when js run error
        client.getOptions().setDoNotTrackEnabled(true);
        client.getOptions().setHistoryPageCacheLimit(1);//limit the cache number
        client.getOptions().setHistorySizeLimit(1);
        client.setAjaxController(new NicelyResynchronizingAjaxController());
        org.apache.commons.logging.impl.Jdk14Logger logger =(org.apache.commons.logging.impl.Jdk14Logger) LogFactory.getLog("com.gargoylesoftware.htmlunit");
        	logger.getLogger().setLevel(Level.OFF);
        return client;
    }
	/*
	public static ArrayList<String> getTable(HtmlPage page,HashMap<String,String> path,ArrayList<String> formula){
		 ArrayList<String> lastResult=new ArrayList<String>();
		return_table( page, path);
		return explain_formula(formula);
	}*/
	private static HashMap<String, ArrayList<String>> return_table(HtmlPage page, HashMap<String, String> path){
		HashMap<String, ArrayList<String>> table=new HashMap<String, ArrayList<String>>();
		ArrayList<String> t=new ArrayList<String>();
		for (Entry<String, String> entry : path.entrySet()) {
			t=get_single_table(entry.getValue(),page);
			table.put(entry.getKey(), t);
		 } 
		return table;
	}
	private static ArrayList get_single_table(String path, HtmlPage page){
		ArrayList<String> table_content = new ArrayList<String>();
		
		HtmlTable table = (HtmlTable) page.getFirstByXPath(path);
		String text="";
		for (HtmlTableRow row : table.getRows()) {
			text="";
    		for (HtmlTableCell cell : row.getCells()) {
    			text+=cell.asText().replace(",", "和")+",";
    			}
    		while(text.contains(",,")){
    			text=text.replace(",,", ", ,");
    		}
    		table_content.add(text);
   		}
		
		return table_content;
	}
	private static ArrayList<String> explain_formula(ArrayList<String> formula, HashMap<String, ArrayList<String>> table){
		
		if(formula.size()==1){
			return table.get(formula.get(0));
		}
		if(formula.size()==3){
			return table.get(formula.get(1));
		}
		String[] stack = new String[formula.size()];
		ArrayList<String> sub_formula = new ArrayList<String>();
		//int index = 0;
		int start = 0;
		int end = 0;
		int mid_result = 0;
		for(int i=0;i<formula.size();i++){
			if(!formula.get(i).equals(")")){
				stack[i] = formula.get(i);
				end = i;
				//System.out.println(")");
			}
			else{
				//System.out.println(")"+i);
				for(int j = i-1;j>0;j--){
					if(formula.get(j).equals("(")){
						start = j;	break;
					}
				}	for(int n = start+1;n<end+1;n++){
					sub_formula.add(formula.get(n));
				}
				if(sub_formula.size()==1){
					return table.get(sub_formula.get(0));
				}
			//	System.out.println(sub_formula);
				if(sub_formula.contains("+")){
					//System.out.println(sub_formula);
					mid_result = get_add(sub_formula,table );
					//
					break;
				}
				else if(sub_formula.contains("*")){
					mid_result = get_multi(sub_formula,table);
					//System.out.println("success");
					break;
				}
			}
		}
		//System.out.println(mid_result);
		ArrayList<String> next_formula = new ArrayList<String>();
	//	System.out.println("start:"+start+" end:"+end);
		for(int k=0;k<formula.size();k++){
			if(k<start){
				next_formula.add(formula.get(k));
			}
			else if(k==start){
				next_formula.add(String.valueOf(mid_result));
			}
			else if(k>end+1){
				next_formula.add(formula.get(k));
			//	System.out.println(formula.get(k));
			}
		}
		return explain_formula(next_formula,table);
	}
	
	private static int get_add(ArrayList<String> formula, HashMap<String, ArrayList<String>> table){
		index++;
		ArrayList<String> temp_table=new ArrayList<String>();
		
		String ss = "+";
		formula.remove(ss);
		ArrayList<String> table1=table.get(formula.get(0));
		ArrayList<String> table2=table.get(formula.get(1));
		for(int i=0;i<table1.size();i++){
		    temp_table.add(table1.get(i)+table2.get(i));
		}
		table.put(index+"", temp_table);
	//	System.out.println("+:"+temp_table);
		return index;
	}
	private static int get_multi(ArrayList<String> formula, HashMap<String, ArrayList<String>> table){
		index++;
		String ss = "*";
		formula.remove(ss);
		ArrayList<String> table1=table.get(formula.get(0));
		ArrayList<String> table2=table.get(formula.get(1));
		for(String t:table2){
		    table1.add(t);
		}
		//System.out.println("*:"+table1.size());
		table.put(index+"", table1);
		return index;
	}
	
	public static void main(String[] args) throws IOException {
		//getAllTable("5","E:/test_table/5/table");
		String[] fPara = {"formula"};
		String[] con1 = {"webId","type"};
		String[] conV1 = {"138"+"","formula"};
		DataProcess dp = new DataProcess("138");
		TableProcess tp=new TableProcess(con1,conV1);
		File file = new File("E:\\table\\poverty\\138\\html\\0\\1552890452546.html");
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];  
     
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);  
            in.close();  
            System.setProperty("illegal-access","permit");
		String txt=new String(filecontent, "utf-8");
		WebClient client = new WebClient(BrowserVersion.FIREFOX_52);
		 client.getOptions().setJavaScriptEnabled(true);    //姒涙顓婚幍褑顢慾s閿涘苯顩ч弸婊�绗夐幍褑顢慾s閿涘苯鍨崣顖濆厴娴兼氨娅ヨぐ鏇炪亼鐠愩儻绱濋崶鐘辫礋閻€劍鍩涢崥宥呯槕閻焦顢嬮棁锟界憰涔痵閺夈儳绮崚韬诧拷锟�
	     client.getOptions().setCssEnabled(false);
	     client.getOptions().setThrowExceptionOnScriptError(false); //js鏉╂劘顢戦柨娆掝嚖閺冭绱濋弰顖氭儊閹舵稑鍤鍌氱埗         
	        client.getOptions().setTimeout(20000);  
	        client.setAjaxController(new NicelyResynchronizingAjaxController());
	        client.waitForBackgroundJavaScript(50000);
	        client.setJavaScriptTimeout(50000);
	        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
	        //  client.getOptions().setThrowExceptionOnScriptError(false);        
	        org.apache.commons.logging.impl.Jdk14Logger logger =(org.apache.commons.logging.impl.Jdk14Logger) LogFactory.getLog("com.gargoylesoftware.htmlunit");
	        	logger.getLogger().setLevel(Level.OFF);

		URL url = new URL("http://1");
		StringWebResponse response = new StringWebResponse(txt, url);
		HtmlPage page = HTMLParser.parseHtml(response, client.getCurrentWindow());
		RAMMD5Dedutor dedu=new RAMMD5Dedutor(Paths.get("F:/table/81/"));
		ArrayList<String> subdata=tp.getAllTable(txt,"http://");
		try {
			System.out.println(subdata.size());
			System.out.println(dp.Data_process(subdata, "F:/table/81/dataMD5.txt",dedu));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
