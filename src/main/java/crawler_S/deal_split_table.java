package crawler_S;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;


public class deal_split_table {
	public static int index = 0;
	static HashMap<String, ArrayList<String>> table=new HashMap<String, ArrayList<String>>();
	static ArrayList<String> result=new ArrayList<String>();
	
	public static ArrayList<String> getAllTable(String webId, String filepath, String[] con, String[] conV){
		HashMap<String, String> path=new HashMap<String, String>();
		ArrayList<String> formula=new ArrayList<String>();
		String[] Para = {"patternName","xpath"};
		
		String[][] pathParas = DBUtil.select("pattern_structed", Para, con,conV);
		for(int i=0;i<pathParas.length;i++){
			path.put(pathParas[i][0], pathParas[i][1]);
		}
		String[] fPara = {"formula"};
		char[] fParas = DBUtil.select("pattern_structed", fPara,con,conV)[0][0].toCharArray();
		System.out.println(fParas.length);
		formula.add("(");
		for(int i=0;i<fParas.length;i++){
			formula.add(fParas[i]+"");
		}
		formula.add(")");
		System.out.println(formula);
		ArrayList<String> lastResult=new ArrayList<String>();
		WebClient client = new WebClient(BrowserVersion.FIREFOX_52);
        client.getOptions().setJavaScriptEnabled(true);    //榛樿鎵цjs锛屽鏋滀笉鎵цjs锛屽垯鍙兘浼氱櫥褰曞け璐ワ紝鍥犱负鐢ㄦ埛鍚嶅瘑鐮佹闇�瑕乯s鏉ョ粯鍒躲��
        client.getOptions().setCssEnabled(false);
        client.getOptions().setThrowExceptionOnScriptError(false); //js杩愯閿欒鏃讹紝鏄惁鎶涘嚭寮傚父         
        client.getOptions().setTimeout(20000);  
        client.setAjaxController(new NicelyResynchronizingAjaxController());
        client.waitForBackgroundJavaScript(50000);
        client.setJavaScriptTimeout(50000);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        //  client.getOptions().setThrowExceptionOnScriptError(false);        
        org.apache.commons.logging.impl.Jdk14Logger logger =(org.apache.commons.logging.impl.Jdk14Logger) LogFactory.getLog("com.gargoylesoftware.htmlunit");
        	logger.getLogger().setLevel(Level.OFF);

    	HtmlPage page = null;
	
		File file = new File(filepath);;
		if (file.isDirectory()) {
          //  
            String[] filelist = file.list();
            //System.out.println(filelist.length);
            for (int i = 0; i < filelist.length; i++) {
                //    File readfile = new File("file:///"+filepath + "/" + filelist[i]); 
                	try {
            			page = (HtmlPage) client.getPage("file:///"+filepath + "/" + filelist[i]);
            			//System.out.println(page.asText());
            					return_table( page, path);
            				explain_formula( formula);
            				if(i!=0)result.remove(0);
            				//if(i==0)System.out.println(result);
            				lastResult.addAll(result);
                	} catch (Exception e) {
                		continue;
            		}
            }

    }
		return lastResult;
	}
	public static ArrayList<String> getTable(HtmlPage page, HashMap<String, String> path, ArrayList<String> formula){
		 ArrayList<String> lastResult=new ArrayList<String>();
		return_table( page, path);
		explain_formula( formula);
	//	System.out.print(index);
	//	for(String s1:table.get(index)){System.out.println(s1);}
		return result;
	}
	private static void return_table(HtmlPage page, HashMap<String, String> path){
	//	HashMap<String,ArrayList> table =new HashMap<String,ArrayList>();
		ArrayList<String> t=new ArrayList<String>();
		for (Entry<String, String> entry : path.entrySet()) {
			t=get_single_table(entry.getValue(),page);
			table.put(entry.getKey(), t);
	//		 System.out.println(entry.getKey()+t);
		 }  
		//return table;
	}
	private static ArrayList get_single_table(String path, HtmlPage page){
		ArrayList<String> table_content = new ArrayList<String>();
		HtmlTable table = (HtmlTable) page.getFirstByXPath(path);
		String text="";
		for (HtmlTableRow row : table.getRows()) {
			text="";
    	//	System.out.println("Found row"+row.getCells().size());
    //		System.out.println("rows:"+row.asText());
    		for (HtmlTableCell cell : row.getCells()) {
    			//System.out.println("   Found cell: " + cell.getChildNodes().get(0).getNodeName());
    			//if(!cell.asText().equals(""));
    			text+=cell.asText().replace(",", "、")+",";
    			}
    		while(text.contains(",,")){
    			text=text.replace(",,", ", ,");
    		}
    		table_content.add(text);
    	//  System.out.println("text:"+text.split(",").length);
    		}
		
		return table_content;
	}
	private static void explain_formula(ArrayList<String> formula){
		
		if(formula.size()==1){
			// Integer.parseInt(formula.get(0));
		//	for(String s1:table.get(formula.get(0))){System.out.println(s1);}
	//		System.out.println("sum:"+formula.get(0));
			result=table.get(formula.get(0));
			return ;
		}
		if(formula.size()==3){
			// Integer.parseInt(formula.get(0));
		//	for(String s1:table.get(formula.get(0))){System.out.println(s1);}
	//		System.out.println("sum:"+formula.get(0));
			result=table.get(formula.get(1));
			return ;
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
					result=table.get(sub_formula.get(0));
					return ;
				}
			//	System.out.println(sub_formula);
				if(sub_formula.contains("+")){
					//System.out.println(sub_formula);
					mid_result = get_add(sub_formula );
					//
					break;
				}
				else if(sub_formula.contains("*")){
					mid_result = get_multi(sub_formula);
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
		//System.out.println("end");
		explain_formula(next_formula);
	}
	
	private static int get_add(ArrayList<String> formula){
		index++;
		ArrayList<String> temp_table=new ArrayList<String>();
		String s= "";
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
	private static int get_multi(ArrayList<String> formula){
		index++;
		String s= "";
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
	public static void main(String[] args){
		//getAllTable("5","E:/test_table/5/table");
		String[] fPara = {"formula"};
		String[] con1 = {"webId","type"};
		String[] conV1 = {"81"+"","formula"};
		DataProcess dp = new DataProcess("81");
		ArrayList<String> subdata= deal_split_table.getAllTable("81"+"","F:/table/81/html/0",con1,conV1);
		try {
			System.out.println(subdata.size());
			dp.Data_process(subdata, "F:/table/81/dataMD5.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println(DBUtil.select("pattern", fPara,Integer.parseInt("15"))[0][0].toCharArray());
	/*	ArrayList<String> formula = new ArrayList<String>();
		formula.add("(");
		formula.add("(");
		formula.add("a");
		formula.add("+");
		formula.add("b");
		formula.add(")");
		formula.add("*");
		formula.add("(");
		formula.add("c");
		formula.add("+");
		formula.add("d");
		formula.add(")");
		formula.add(")");
	/*	for(int i=0;i<formula.size();i++){
			if(!formula.get(i).equals(")")){
				//System.out.println(")"+i);
			}(
		}*/
	//	System.out.println(formula.toString().replace(",", ""));
		//explain_formula(formula);
	//	System.out.println(s[0]);
		
//		WebClient client = new WebClient(BrowserVersion.INTERNET_EXPLORER_11);
//        client.getOptions().setJavaScriptEnabled(true);    //榛樿鎵цjs锛屽鏋滀笉鎵цjs锛屽垯鍙兘浼氱櫥褰曞け璐ワ紝鍥犱负鐢ㄦ埛鍚嶅瘑鐮佹闇�瑕乯s鏉ョ粯鍒躲��
//        client.getOptions().setCssEnabled(false);
//        client.getOptions().setThrowExceptionOnScriptError(false); //js杩愯閿欒鏃讹紝鏄惁鎶涘嚭寮傚父         
//        client.getOptions().setTimeout(20000);  
//        client.setAjaxController(new NicelyResynchronizingAjaxController());
//        client.waitForBackgroundJavaScript(50000);
//        client.setJavaScriptTimeout(50000);
//        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
//        //  client.getOptions().setThrowExceptionOnScriptError(false);        
//        org.apache.commons.logging.impl.Jdk14Logger logger =(org.apache.commons.logging.impl.Jdk14Logger) LogFactory.getLog("com.gargoylesoftware.htmlunit");
//        	logger.getLogger().setLevel(Level.OFF);
//
//    	HtmlPage page = null;
//		try {
//			page = (HtmlPage) client.getPage("file:///E:/3.html");
//			//System.out.println(page.asText());
//		} catch (  IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}//
//		HashMap<String,String> path=new HashMap<String,String>();
//		path.put("a", "/html/body/div[3]/div/div[1]/div[1]/div[1]/div/table");//((a+b)*(c+d))
//		path.put("b", "/html/body/div[3]/div/div[1]/div[2]/div[1]/div/table");
//		path.put("c", "/html/body/div[3]/div/div[1]/div[1]/div[2]/div/table");
//		path.put("d", "/html/body/div[3]/div/div[1]/div[2]/div[2]/table");
//		ArrayList<String> r=getAllTable("E:/test_table",path,formula);	  
//		for(String s1:r){
//			System.out.println(r.size()+"r:"+s1);	
//		}
//		// get_single_table("/html/body/div[3]/div/div[1]/div[2]/div[1]/div/table",page);
//		 ///html/body/div[3]/div/div[1]/div[1]/div[1]/div/table
//		 ///html/body/div[3]/div/div[1]/div[2]/div[1]/div/table
//		 ///html/body/div[3]/div/div[1]/div[1]/div[2]/div/table
//		 ///html/body/div[3]/div/div[1]/div[2]/div[2]/table
//	//	HtmlTable table2 = (HtmlTable)page.getFirstByXPath("Xpath");
		
	}

}
