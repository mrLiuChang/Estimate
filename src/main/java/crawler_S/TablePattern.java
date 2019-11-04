package crawler_S;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TablePattern {

	public static void main(String[] args) {
		
		List<String> d;
		try {
			d = getPatternData( "F:\\table\\81\\subpage\\0","pcObj");
			for(String s:d){
				System.out.println(s);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		;
	      
	}
	public static List<String> getPatternData(String htmlpath, String json) throws Exception {
		File direc = new File(htmlpath);
		List<String> data=new ArrayList<String>();
		ArrayList<Integer> flag=new ArrayList<Integer>();
		File[] htmls = direc.listFiles();
		//System.out.println(htmls.length);
		String jsonData="";
		for(int i=0;i<htmls.length;i++){
			String con=readToString(htmls[i]);
			org.jsoup.nodes.Document doc = Jsoup.parse(con);
			try{
				jsonData=con.substring(con.indexOf(json+"={")+7,con.indexOf("};", con.indexOf(json+"={")));
			}catch(Exception e){
				continue;
			}
			String[] text=jsonData.split("\n");
			String name="";
			String value="";
			//System.out.println(jsonData);
			for(int j=0;j<text.length;j++){
				if(text[j].trim().length()==0)continue;
				
				if(i==0){
					String id=text[j].split(":")[0];
					
					Element nameElement= doc.getElementById(id.substring(id.indexOf("\"")+1, id.lastIndexOf("\"")).toUpperCase());
					
					try{
						name+=nameElement.parent().previousElementSibling().text().trim().replace(" ", "")+",";
					}catch(Exception e){
						//System.out.println(j);
						flag.add(j);
						continue;
					}
				}
				 if(!flag.contains(j)){
//					 System.out.println(text[j].split(":")[0]);
					//System.out.println(text[j].split(":")[1]);
					 text[j].split(":")[1].replace(",", "、");
					if(text[j].split(":")[1].contains("\""))
						value+=text[j].split(":")[1].substring(text[j].split(":")[1].indexOf("\"")+1, text[j].split(":")[1].lastIndexOf("\""))+",";
					else value+=text[j].split(":")[1];
					
				 }
			}
			if(i==0) data.add(name);
			//System.out.println(value);
			data.add(value);
		}
		return data;
	}
	public static String readToString(File file ) {
        String encoding = "UTF-8";
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];  
        try {  
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);  
            in.close();  
        } catch (FileNotFoundException e) {
            e.printStackTrace();  
        } catch (IOException e) {
            e.printStackTrace();  
        }  
        try {  
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();  
            return null;  
        }  
    }
}
