package crawler_S;

import java.io.*;
import java.util.*;


public class Queries {
	
	static String workFile = "F:/test_table/";
	static int qNum = 50;
	static String webId = "";
	static String nowRound="";
	static ArrayList<String> unusedQueries=new ArrayList<String>();
	static ArrayList<String> usedQueries=new ArrayList<String>();

	public Queries(String webId){
		Queries.webId=webId;
		String[] filePara = {"workFile",};
		String[] params={"walkTimes"};
		workFile = DBUtil.select("website", filePara, Integer.parseInt(webId))[0][0];
		nowRound=DBUtil.select("estimate", params, new String[]{"estiId"},new String[]{webId})[0][0];

	}
	
	public static double selectQueries(int queryNum){
		String qPath1 =workFile+"/estimate_structed/"+webId+"/query/usedQueries.txt";
	//	ArrayList<String>  usedQueries=new ArrayList<String>();
		if(nowRound.equals("0")) {
			exit(webId);
			return 0;
		}
		if(nowRound.equals("1")){
			unusedQueries=	getAllQueries(workFile+"/estimate_structed/"+webId+"/ParamValuelist.txt");
		} else
			try {
				unusedQueries=getQueries(workFile+"/estimate_structed/"+webId+"/query/unusedQueries.txt");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		if(unusedQueries.size()==0){
			File f3 = new File(	workFile+"/estimate_structed/"+webId+"/query/queries.txt");
			FileWriter selectedQ = null;
			try {
				selectedQ = new FileWriter(f3);
				selectedQ.write("\n");
				selectedQ.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return 0;
		}
		try {
			  usedQueries =	getQueries(qPath1);
			  System.out.println("q:"+usedQueries.size());
			  ArrayList<String> selectedQueries =  selectQueryByDistance(queryNum);
			 logging(selectedQueries);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} 
		exit(webId);
		return usedQueries.size();
	}
	public static void selectQuery(int queryNum){
		
	//	ArrayList<String>  usedQueries=new ArrayList<String>();
		String qPath=workFile+"/estimate_structed/"+webId+"/ParamValuelist.txt";
		ArrayList<String[]> paramValuelist=new ArrayList<String[]>();
			try {
				paramValuelist=	QueryLink.getParamValuelist(qPath);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try {
			 
			  ArrayList<String> selectedQueries = new ArrayList<String>();
			  int n=paramValuelist.get(0).length;
			  int index=0;
			  for(int i=1;i<paramValuelist.size();i++){
				  if(paramValuelist.get(i).length<n)
					  index=paramValuelist.get(i).length;
			  }
			  for(int i=0;i<index;i++)
				  selectedQueries.add(i+"\n");
			 logging(selectedQueries);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} 
		exit(webId);
	}
	private static ArrayList<String> getQueries(String queriesPath) throws IOException {
		ArrayList<String> Querieslist = new ArrayList<String>();
		InputStreamReader read = new InputStreamReader(new FileInputStream(new File(queriesPath)));
		BufferedReader br = new BufferedReader(read);
		String lineTxt;
		while ((lineTxt = br.readLine()) != null) {
			Querieslist.add(lineTxt);	
			
		}
		br.close();
		return Querieslist;
	}
	private static ArrayList<String> selectQueryByDistance(int queryNum){
		Map<String, Integer> map = new HashMap<String, Integer>();
		ArrayList<String> selectedQueries=new ArrayList<String>();
		// System.out.println("usedQueries:"+usedQueries.size());
		if(usedQueries.size()==0){
			String q=unusedQueries.get(new Random().nextInt(unusedQueries.size()));
			usedQueries.add(q);
			selectedQueries.add(q);
			unusedQueries.remove(q);
		}
	//	 System.out.println("dis");
		for(String unusedquery:unusedQueries){
			int dis=0;
			for(String usedquery:usedQueries){
				String[] unquery=unusedquery.split(",");
				String[] query=usedquery.split(",");
				for(int i=0;i<query.length;i++){
					if(!unquery[i].equals(query[i]))
						dis++;
				}
			}
			map.put(unusedquery, dis);
		}

		 List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
		Collections.sort(entryList, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return (o2.getValue() - o1.getValue());
            }
});  
		
		//閫夎瘝 鍖哄垎宸茬敤 鏈敤 鐜扮敤 
	
		int num=0;
		//getKeyList(entryList,entryList.get(num).getValue()+"").size();
		while(selectedQueries.size()<queryNum&&selectedQueries.size()!=queryNum){
			int qNum=getKeyList(entryList,entryList.get(num).getValue()+"").size();
			if(qNum>queryNum){
				int c=queryNum-selectedQueries.size();
				for(int i=0;i<c;i++){
				//	System.out.println(i);
					int r=new Random().nextInt(qNum)+num;
					//System.out.println("R:"+r+"  "+" qNum:"+qNum+" num:"+num);
					 String q=entryList.get(r).getKey();
					 Integer v=entryList.get(r).getValue();
					 if(!selectedQueries.contains(q)){
						 selectedQueries.add(q);
						 usedQueries.add(q);
						 unusedQueries.remove(q);
					// System.out.println(q+":"+v);
					 }
				 }
			}
			else {
				for(int i=num;i<num+qNum&&selectedQueries.size()<queryNum;i++){
					//System.out.println("qNum:"+qNum);
					 selectedQueries.add(entryList.get(i).getKey());
					 usedQueries.add(entryList.get(i).getKey());
					 unusedQueries.remove(entryList.get(i).getKey());
					// System.out.println(entryList.get(i).getKey()+":"+entryList.get(i).getValue()); 
				}
				num+=qNum;
			} 
		}

		System.out.println(selectedQueries.size());
		return selectedQueries;
		
	}
	  public static List<String> getKeyList(List<Map.Entry<String, Integer>> map, String value){
		           List<String> keyList = new ArrayList<String>();
		          for(int i=0;i<map.size();i++){
		        //	  System.out.println(map.get(i).getValue().equals(value)+map.get(i).getValue()+value);
		               if(map.get(i).getValue()== Integer.parseInt(value)){
		                  keyList.add(map.get(i).getKey()); // System.out.println(map.get(i).getValue().equals(value));
		              }
		           }
		           return keyList;
		      }
	private static void logging( ArrayList<String> selectedQueries){
		 System.out.println("usedQueries:"+usedQueries.size());
		File f1 = new File(workFile+"/estimate_structed/"+webId+"/query/unusedQueries.txt");
		File f2 = new File(workFile+"/estimate_structed/"+webId+"/query/usedQueries.txt");
		File f3 = new File(	workFile+"/estimate_structed/"+webId+"/query/queries.txt");
		File f4 = new File(	workFile+"/estimate_structed/"+webId+"/query/queries"+nowRound+".txt");
		if(!f1.exists()){
			try {
				f1.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if(!f2.exists()){
			try {
				f2.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if(!f3.exists()){
			try {
				f3.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		try {
			FileWriter unusedQ=new FileWriter(f1);
			FileWriter usedQ=new FileWriter(f2);
			FileWriter selectedQ=new FileWriter(f3);
			FileWriter selected=new FileWriter(f4);
			for(String sq:selectedQueries){
				selectedQ.write(sq+"\n");
				selected.write(sq+"\n");
				selectedQ.flush();
				selected.flush();
			}
			for(String sq:usedQueries){
				usedQ.write(sq+"\n");			
				usedQ.flush();			
			}
			for(String uq:unusedQueries){
				unusedQ.append(uq+"\n");
				unusedQ.flush();
			}
			unusedQ.close();usedQ.close();selectedQ.close();selected.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void exit(String webId){
	}

	
	//4.1.3

	
	public static ArrayList<String> getQueriesList(ArrayList<String> num, int p){
		int m=0;
		ArrayList<String> result=new ArrayList<String>();//String[num.length*p];
	for(int i=0;i<num.size();i++){
	 	for(int n=0;n<p;n++){
	 		result.add(num.get(i)+n+",");	
			}
		
	}
		return result;
	}
	public static ArrayList<String> getAllQueries(String qPath){
		ArrayList<String[]> paramValuelist =new ArrayList<String[]>();
		try {
			paramValuelist=	QueryLink.getParamValuelist(qPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ArrayList<String> num =new ArrayList<String>();
			ArrayList<Integer> paemNum =new ArrayList<Integer>();
			num.add("");
			for(int i=0;i<paramValuelist.size();i++){
				paemNum.add(paramValuelist.get(i).length);
			}
			
			for(int n=0;n<paemNum.size();n++){
				num=getQueriesList(num,paemNum.get(n));
			}
		//	System.out.print(num.get(1));
			return num;
	}


    public static double getN(String webId){
        String[] NPara = {"N"};
        System.out.println("webId"+webId);
        double N = Integer.parseInt(DBUtil.select("queryParam", NPara, Integer.parseInt(webId))[0][0]);
        System.out.println("N is "+N);
        return N;
    }
    public static double getC(String webId){
        String[] NPara = {"C"};
        double C = Integer.parseInt(DBUtil.select("queryParam", NPara, Integer.parseInt(webId))[0][0]);
        return C;
    }
	public static void main(String[] args){
		//Queries q = new Queries("32");
		//q.createDFTable("1");	//q.random("32");
		unusedQueries=	getAllQueries(workFile+"19/ParamValuelist.txt");
		
	}

}
