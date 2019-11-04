package crawler_S;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.WebSite;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class DBUtil  {
	private static String dbDriver = "com.mysql.jdbc.Driver";
	private static String dbUrl = "jdbc:mysql://localhost:3306/webcrawler?characterEncoding=UTF-8&useSSL=false&useAffectedRows=true&allowPublicKeyRetrieval=true&serverTimezone=GMT";
	private static String dbUser = "root";
	private static String dbPass = "2333";
	public static void config(String mysqlURL, String mysqlUserName, String mysqlPassword){
		dbUrl=mysqlURL;
		dbUser=mysqlUserName;
		dbPass=mysqlPassword;
	}

	public static Connection getConn() throws ClassNotFoundException, SQLException {
		Connection conn = null;
		 Properties props= new Properties();

		 if(dbUrl==null||dbUser==null||dbPass==null){
				Properties prop=new Properties();
				try {
					prop.load(DBUtil.class.getResourceAsStream("/application.properties"));
				}catch (IOException e){
					System.err.println(e);
					return null;
				}
				dbUrl=prop.getProperty("mysqlURL");
				dbUser=prop.getProperty("mysqlUserName");
				dbPass=prop.getProperty("mysqlPassword");
			}
		if(!dbUrl.contains("rewriteBatchedStatements"))
			dbUrl=dbUrl+"&rewriteBatchedStatements=true";
		//Class.forName(dbDriver);
		conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
		
		return conn;
	}

	public static void release(Connection conn, Statement st, ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
		}
		if (st != null) {
			st.close();			
		}
		if (conn != null) {
			conn.close();			
		}
	}
	public static boolean createTable(String table, List<String> params){
		boolean flag = true;
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
	
		String sql = "CREATE TABLE "  + " if not exists "+table+" (`dataId` int(11) unsigned NOT NULL AUTO_INCREMENT,";
		for (int i = 0; i < params.size() ; i++) {
			
			sql +="`"+ params.get(i)+"` text DEFAULT NULL" + ",";
		}
		
		sql += " PRIMARY KEY (`dataId`)) Engine=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;";
		//System.out.println(sql);
		
		try {
				conn = getConn();
			
			try {
				st = (PreparedStatement) conn.prepareStatement(sql);
			} catch (SQLException e) {
				flag = false;
				System.out.println("prepareStatement");
				e.printStackTrace();		
			}
			try {
				st.executeUpdate();
			} catch (SQLException e) {
				flag = false;
				System.out.println("create table error");
				System.out.println(sql);
			//	e.printStackTrace();
				
			}
			} catch (ClassNotFoundException e1) {
				flag = false;
			} catch (SQLException e) {
				e.printStackTrace();
			}
	
		finally {
			try {
				release(conn, st, rs);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				flag = false;
				e.printStackTrace();
			}
		}
		return flag;
	}
	public static boolean createTable(String table, String[] params){
		boolean flag = true;
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
	
		String sql = "CREATE TABLE "  + " if not exists "+table+" (`dataId` int(11) unsigned NOT NULL AUTO_INCREMENT,";
		for (int i = 0; i < params.length; i++) {
			
			sql +="`"+ params[i]+"` text DEFAULT NULL" + ",";
		}
		
		sql += " PRIMARY KEY (`dataId`)) Engine=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;";
		//System.out.println(sql);
		
		try {
				conn = getConn();
			
			try {
				st = (PreparedStatement) conn.prepareStatement(sql);
			} catch (SQLException e) {
				flag = false;
				System.out.println("prepareStatement");
				e.printStackTrace();		
			}
			try {
				st.executeUpdate();
			} catch (SQLException e) {
				flag = false;
				System.out.println("create table error");
				System.out.println(sql);
			//	e.printStackTrace();
				
			}
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				flag = false;
		//		e1.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		finally {
			try {
				release(conn, st, rs);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				flag = false;
				e.printStackTrace();
			}
		}
		return flag;
	}


	public static  boolean insertIgnore(String table, String[] params, String[] params_value) {
		boolean flag = true;
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String sql = "replace into " + table + " (";
		for (int i = 0; i < params.length - 1; i++) {
			sql += params[i] + ",";
		}
		sql += params[params.length - 1] + ") values (";
		for (int j = 0; j < params_value.length - 1; j++) {
			sql += "'" + params_value[j] + "' ,";
		}
		sql += "'" + params_value[params_value.length - 1] + "');";
		//System.out.println(sql);

		try {
			conn = getConn();

			try {
				st = (PreparedStatement) conn.prepareStatement(sql);
			} catch (SQLException e) {
				flag = false;
				System.out.println("插入数据失败");
			}
			try {
				st.executeUpdate();
			} catch (SQLException e) {
				flag = false;
				System.out.println(sql);
			}
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			flag = false;
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			flag = false;
			e1.printStackTrace();
		}

		finally {
			try {
				release(conn, st, rs);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				flag = false;
				e.printStackTrace();
			}
		}
		return flag;
	}
	public static boolean insert(String table, String[] params, String[] params_value) {
		boolean flag = true;
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String sql = "insert into " + table + " (";
		for (int i = 0; i < params.length - 1; i++) {
			sql += params[i] + ",";
		}
		sql += params[params.length - 1] + ") values (";
		for (int j = 0; j < params_value.length - 1; j++) {
			sql += "'" + params_value[j] + "' ,";
		}
		sql += "'" + params_value[params_value.length - 1] + "');";
		//System.out.println(sql);
		
		try {
				conn = getConn();
			
			try {
				st = (PreparedStatement) conn.prepareStatement(sql);
			} catch (SQLException e) {
				flag = false;
				System.out.println("插入数据失败");
			}
			try {
				st.executeUpdate();
			} catch (SQLException e) {
				flag = false;
				System.out.println(sql);
			}
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				flag = false;
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				flag = false;
				e1.printStackTrace();
			}
	
		finally {
			try {
				release(conn, st, rs);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				flag = false;
				e.printStackTrace();
			}
		}
		return flag;
	}
	public static boolean insertBatch(String table, String[] params, List<String> newData){
		boolean flag = true;
		Connection conn = null;
		ResultSet rs = null;
		
        PreparedStatement stmt  = null;
        String[] data=null;
        StringBuffer sb = new StringBuffer();
        String sql = "insert into " + table + " (";
		for (int i = 0; i < params.length - 1; i++) {
			sql += params[i] + ",";
			 sb.append("?").append(",");
		}
		sql += params[params.length - 1] + ") values ("+sb+"?)";
		
        try {
        	conn = getConn();
            stmt = conn.prepareStatement(sql);
            for(int i=0;i<newData.size();i++){
            	data=newData.get(i).replace(",,", ",").split(",");
            	for(int n=0;n<data.length;n++){
            		  stmt.setString(n+1, data[n]);                  
            	}
            	stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
        	flag=false;
        	e.printStackTrace();
        	System.out.println(sql);
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            try {
				release(conn, stmt, rs);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println(sql);
			}
        }
        return flag;
    }
	public static boolean insertBatch(String table, List<String> params, JsonArray jsonArray){
		boolean flag = true;
		Connection conn = null;
		ResultSet rs = null;
		
        PreparedStatement stmt  = null;
       
        StringBuffer sb = new StringBuffer();
        String sql = "insert into " + table + " (";
		for (int i = 0; i < params.size() - 1; i++) {
			sql += params.get(i) + ",";
			 sb.append("?").append(",");
		}
		sql += params.get(params.size() - 1) + ") values ("+sb+"?)";
		
        try {
        	conn = getConn();
            stmt = conn.prepareStatement(sql);
           
            
            for(int i=0;i<jsonArray.size();i++){
            	int n=1;

            	JsonObject jsonObject = (JsonObject) jsonArray.get(i);
            	Set<Entry<String, JsonElement>> j = jsonObject.entrySet();
            	//Iterator<Entry<String, JsonElement>> j= ((JsonObject) jsonArray.get(i)).entrySet().iterator();
        		try {
        			 for (Entry<String, JsonElement> en:j) {
        				// System.out.println(params.indexOf(en.getKey().toString()));
	        			stmt.setString(params.indexOf(en.getKey().toString())+1,en.getValue().toString());
	        			if(n!=params.indexOf(en.getKey().toString())+1) {
	        				stmt.setString(n,"");
	        				n++;
	        			}	
	        			n++;
	        		}
	            	stmt.addBatch();
        		}catch(Exception e) {
        			e.printStackTrace();	
        			continue;
        		}
            }
            stmt.executeBatch();
        } catch (SQLException e) {
        	flag=false;
        	e.printStackTrace();
        	
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            try {
				release(conn, stmt, rs);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println(sql);
			}
        }
        return flag;
    }
	public static boolean insertNotExist(String table, String[] params, String[] params_value) {
		boolean flag = true;
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String sql = "insert into " + table + " (";
		for (int i = 0; i < params.length - 1; i++) {
			sql += params[i] + ",";
		}
		sql += params[params.length - 1] + ") SELECT ";
		for (int j = 0; j < params_value.length - 1; j++) {
			sql += "'" + params_value[j] + "' ,";
		}
		sql += "'" + params_value[params_value.length - 1] + "' FROM DUAL WHERE NOT EXISTS (SELECT * FROM "+table+" WHERE ";
		for(int j=0;j<params.length-1;j++){
			sql+=params[j]+"="+ "'"+params_value[j]+"' and ";
		}
		sql+=params[params.length-1]+"=\'"+params_value[params_value.length-1]+"\');";
	
	//System.out.println(sql);
		
		try {
				conn = getConn();
			
			try {
				st = (PreparedStatement) conn.prepareStatement(sql);
			} catch (SQLException e) {
				flag = false;
				System.out.println(sql);
				e.printStackTrace();
				
				
			}
			try {
				st.executeUpdate();
			} catch (SQLException e) {
				flag = false;
				System.out.println(sql);
				e.printStackTrace();
				
			}
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				flag = false;
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				flag = false;
				e1.printStackTrace();
			}
	
		finally {
			try {
				release(conn, st, rs);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				flag = false;
				e.printStackTrace();
			}
		}
		return flag;
	}
	
	// 锟斤拷锟斤拷锟斤拷锟絫able--锟斤拷锟斤拷锟狡ｏ拷params--锟斤拷锟斤拷锟斤拷疲锟絧arams_value--锟斤拷锟斤拷值锟斤拷锟斤拷锟斤拷某锟斤拷webId锟侥硷拷录
	public static boolean update(String table, String[] params, String[] params_value, int webId) {
		boolean flag = true;
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String sql = "update " + table + " SET ";
		for (int i = 0; i < params.length - 1; i++) {
			sql += params[i] + "=\'" + params_value[i] + "\',";
		}
		sql += params[params.length - 1] + "=\'" + params_value[params_value.length - 1] + "\' where webId=\'" + webId + "\';";
		//System.out.println(sql);
		try {
			conn = getConn();
			try {
				st = (PreparedStatement) conn.prepareStatement(sql);
			} catch (SQLException e) {
				System.out.println("预锟斤拷锟斤拷锟斤拷锟�");
				e.printStackTrace();
				flag = false;
			}
			try {
				st.executeUpdate();
			} catch (SQLException e) {
				System.out.println("执锟斤拷update锟斤拷锟斤拷");
				e.printStackTrace();
				flag = false;
			}
		
	} catch (ClassNotFoundException e1) {
		// TODO Auto-generated catch block
		flag = false;
		e1.printStackTrace();
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		flag = false;
		e1.printStackTrace();
	}
		finally {
			try {
				release(conn, st, rs);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				flag = false;
				e.printStackTrace();
			}
			
		}
		return flag;
	}
	
	//锟斤拷锟斤拷锟斤拷锟絬pdate
		public static boolean update(String table, String params[], String params_value[], String cond_par[], String cond_par_val[]) {
			Connection conn = null;
			PreparedStatement st = null;
			ResultSet rs = null;
			boolean flag = true;
			String sql = "update " + table + " SET ";
			for (int i = 0; i < params.length - 1; i++) {
				sql += params[i] + "=" +"'"+ params_value[i] +"'"+ ",";
			}
			sql += params[params.length - 1] + "=" + "'"+params_value[params_value.length - 1] +"'"+ " where ";
			for (int j = 0; j < cond_par.length - 1; j++) {
				sql += cond_par[j] + "=" + "'"+cond_par_val[j] +"'"+ " and ";
			}
			sql +=cond_par[cond_par.length-1]+"="+"'"+cond_par_val[cond_par.length-1]+"'"+";";			
			//System.out.println(sql);
			try {
					conn = getConn();
				
				try {
					st = (PreparedStatement) conn.prepareStatement(sql);
				} catch (SQLException e) {
					flag = false;
					
					e.printStackTrace();
				}
				try {
					st.executeUpdate();
				} catch (SQLException e) {
					flag = false;
					System.out.println(sql);
					e.printStackTrace();
				} 
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				flag = false;
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				flag = false;
				e1.printStackTrace();
			}
			finally {
				try {
					release(conn, st, rs);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					flag = false;
					e.printStackTrace();
				}
			}
			return flag;
		}

	
	public static String[][] select(String table, String[] params, String[] cond_params, String[] cond_par_val) {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String[][] result = null;
		String sql = "select ";
		for (int i = 0; i < params.length - 1; i++) {
			sql += params[i] + ",";
		}
		sql += params[params.length - 1] + " from " + table + " where ";
		for(int j=0;j<cond_params.length-1;j++){
			sql+=cond_params[j]+"=\'"+cond_par_val[j]+"\' and ";
		}
		sql+=cond_params[cond_params.length-1]+"=\'"+cond_par_val[cond_params.length-1]+"\'";
	//	System.out.println(sql);
		try {
			conn = getConn();
			try {
				st = (PreparedStatement) conn.prepareStatement(sql);
			} catch (SQLException e) {
				System.out.println("预锟斤拷锟斤拷锟斤拷锟�");
				e.printStackTrace();
			}
			try {
				rs = st.executeQuery();
			} catch (SQLException e) {
				System.out.println("select锟斤拷锟斤拷");
				e.printStackTrace();
			}
			ResultSetMetaData mm = null;
			try {
				mm = rs.getMetaData();
			} catch (SQLException e1) {
				System.out.println("rs锟结构锟斤拷取锟斤拷锟斤拷");
				e1.printStackTrace();
			}
			int columns = 0;
			try {
				columns = mm.getColumnCount();
			} catch (SQLException e1) {
				System.out.println("锟斤拷取锟斤拷锟斤拷锟斤拷锟�");
				e1.printStackTrace();
			}
			// System.out.println("columns+"+columns);
			int rowCount = 0;
			try {
				rs.last();
				rowCount = rs.getRow();
			} catch (Exception e) {
				System.out.println("锟斤拷取锟斤拷锟斤拷锟斤拷锟�");
				e.printStackTrace();
			}
			// System.out.println("rowCount+"+rowCount);

			
			try {
				rs.beforeFirst();
			} catch (SQLException e1) {
				System.out.println("指锟斤拷锟斤拷转锟斤拷锟斤拷");
				e1.printStackTrace();
			}
			int k = 0;
			try {
				result = new String[rowCount][columns];
				while (rs.next()) {
					for (int i = 1; i <= columns; i++) {
						try {
							//System.out.print(mm.getColumnName(i));
							//System.out.print(rs.getString(i));
							result[k][i - 1] = rs.getString(i);
							//System.out.print("\t\t");
							//System.out.println();
						} catch (SQLException e) {
							System.out.println("锟斤拷取锟斤拷锟斤拷锟斤拷锟�");
							e.printStackTrace();
						}
						// System.out.print("\t\t");
					}
					//System.out.println();
					k++;
				}
			} catch (SQLException e) {
				System.out.println("rs循锟斤拷锟斤拷锟斤拷");
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			try {
				release(conn, st, rs);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;

	}
	//锟斤拷锟斤拷取值
	public static String[][] select(String table, String[] params) {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String[][] result = null;
		String sql = "select ";
		for (int i = 0; i < params.length - 1; i++) {
			sql += params[i] + ",";
		}
		sql += params[params.length - 1] + " from " + table ;
		//System.out.println(sql);
		try {
			conn = getConn();
			try {
				st = (PreparedStatement) conn.prepareStatement(sql);
			} catch (SQLException e) {
				System.out.println("预锟斤拷锟斤拷锟斤拷锟�");
				e.printStackTrace();
			}
			try {
				rs = st.executeQuery();
			} catch (SQLException e) {
				System.out.println("select锟斤拷锟斤拷");
				e.printStackTrace();
			}
			ResultSetMetaData mm = null;
			try {
				mm = rs.getMetaData();
			} catch (SQLException e1) {
				System.out.println("rs锟结构锟斤拷取锟斤拷锟斤拷");
				e1.printStackTrace();
			}
			int columns = 0;
			try {
				columns = mm.getColumnCount();
			} catch (SQLException e1) {
				System.out.println("锟斤拷取锟斤拷锟斤拷锟斤拷锟�");
				e1.printStackTrace();
			}
			// System.out.println("columns+"+columns);
			int rowCount = 0;
			try {
				rs.last();
				rowCount = rs.getRow();
			} catch (Exception e) {
				System.out.println("锟斤拷取锟斤拷锟斤拷锟斤拷锟�");
				e.printStackTrace();
			}
			// System.out.println("rowCount+"+rowCount);

			
			try {
				rs.beforeFirst();
			} catch (SQLException e1) {
				System.out.println("指锟斤拷锟斤拷转锟斤拷锟斤拷");
				e1.printStackTrace();
			}
			int k = 0;
			try {
				result = new String[rowCount][columns];
				while (rs.next()) {
					for (int i = 1; i <= columns; i++) {
						try {
							//System.out.print(mm.getColumnName(i));
							//System.out.print(rs.getString(i));
							result[k][i - 1] = rs.getString(i);
							//System.out.print("\t\t");
							//System.out.println();
						} catch (SQLException e) {
							System.out.println("锟斤拷取锟斤拷锟斤拷锟斤拷锟�");
							e.printStackTrace();
						}
						// System.out.print("\t\t");
					}
					//System.out.println();
					k++;
				}
			} catch (SQLException e) {
				System.out.println("rs循锟斤拷锟斤拷锟斤拷");
				e.printStackTrace();
			}
			return result;

		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			try {
				release(conn, st, rs);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;

	}
	//锟斤拷锟絯ebId取值
	public static String[][] select(String table, String[] params, int webId){
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String sql="select ";
		String[][] result = null;
		for(int i=0;i<params.length-1;i++){
			sql+=params[i]+",";
		}
		sql+=params[params.length-1]+" from "+table+" where webId="+webId+";";
		//System.out.println(sql);	
		try {
			conn = getConn();
			try {
				st = (PreparedStatement) conn.prepareStatement(sql);
			} catch (SQLException e) {
				System.out.println("预锟斤拷锟斤拷锟斤拷锟�");
				e.printStackTrace();
			}
			try {
				rs = st.executeQuery();
			} catch (SQLException e) {
				System.out.println("select锟斤拷锟斤拷");
				e.printStackTrace();
			}
			ResultSetMetaData mm = null;
			try {
				mm = rs.getMetaData();
			} catch (SQLException e1) {
				System.out.println("rs锟结构锟斤拷取锟斤拷锟斤拷");
				e1.printStackTrace();
			}
			int columns = 0;
			try {
				columns = mm.getColumnCount();
			} catch (SQLException e1) {
				System.out.println("锟斤拷取锟斤拷锟斤拷锟斤拷锟�");
				e1.printStackTrace();
			}
			// System.out.println("columns+"+columns);
			int rowCount = 0;
			try {
				rs.last();
				rowCount = rs.getRow();
			} catch (Exception e) {
				System.out.println("锟斤拷取锟斤拷锟斤拷锟斤拷锟�");
				e.printStackTrace();
			}
			// System.out.println("rowCount+"+rowCount);

			
			try {
				rs.beforeFirst();
			} catch (SQLException e1) {
				System.out.println("指锟斤拷锟斤拷转锟斤拷锟斤拷");
				e1.printStackTrace();
			}
			int k = 0;
			try {
				result = new String[rowCount][columns];
				while (rs.next()) {
					for (int i = 1; i <= columns; i++) {
						try {
							//System.out.print(mm.getColumnName(i));
							//System.out.print(rs.getString(i));
							result[k][i - 1] = rs.getString(i);
							//System.out.print("\t\t");
							//System.out.println();
						} catch (SQLException e) {
							System.out.println("锟斤拷取锟斤拷锟斤拷锟斤拷锟�");
							e.printStackTrace();
						}
						// System.out.print("\t\t");
					}
					//System.out.println();
					k++;
				}
			} catch (SQLException e) {
				System.out.println("rs循锟斤拷锟斤拷锟斤拷");
				e.printStackTrace();
			}
			

		} 
		catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			try {
				release(conn, st, rs);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	public static List selectMutilpara(String table, String[] params, int webId, Class c){
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String sql="select ";
		List result = null;
		for(int i=0;i<params.length-1;i++){
			sql+=params[i]+",";
		}
		sql+=params[params.length-1]+" from "+table+" where webId="+webId+";";
		//System.out.println(sql);	
		try {
			conn = getConn();
			try {
				st = (PreparedStatement) conn.prepareStatement(sql);
			} catch (SQLException e) {
				System.out.println("预锟斤拷锟斤拷锟斤拷锟�");
				e.printStackTrace();
			}
			try {
				rs = st.executeQuery();
			} catch (SQLException e) {
				System.out.println("select锟斤拷锟斤拷");
				e.printStackTrace();
			}
			try {
				result=populate(rs,c);
			} catch (SQLException | InstantiationException | IllegalAccessException e) {
				System.out.println("to object fail");
				e.printStackTrace();
			}
			

		} 
		catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			try {
				release(conn, st, rs);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	// 锟斤拷table锟揭碉拷锟斤拷锟斤拷webId
	public static int getLastWebId() {
		int max = 0;
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String sql = "select * from website";
		try {
			conn = getConn();
			try {
				st = (PreparedStatement) conn.prepareStatement(sql);
			} catch (SQLException e) {
				System.out.println("预锟斤拷锟斤拷锟斤拷锟�");
				e.printStackTrace();
			}
			try {
				rs = st.executeQuery();
			} catch (SQLException e) {
				System.out.println("执锟斤拷select锟斤拷锟斤拷");
				e.printStackTrace();
			}
			try {
				rs.last();
				max = Integer.parseInt( rs.getString("webId"));
			} catch (Exception e) {
				System.out.println("锟斤拷取锟斤拷锟斤拷袛锟斤拷锟斤拷锟�");
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			try {
				release(conn, st, rs);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return max;
	}
	//锟斤拷status锟斤拷锟叫讹拷取锟斤拷锟�10锟斤拷锟斤拷录
	public static ArrayList<String> getLastInfoLinks(String webId){
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String[] param = {"round"};
		String round = DBUtil.select("current", param, Integer.parseInt(webId))[0][0];
		String r = round.split("-")[0]+"-%";
		String sql = "select * from status where webId=\'"+webId+"\' and type=\'info\' and round LIKE \'"+r+"\';" ;
		 //System.out.println(sql);
		ArrayList<String> links=new ArrayList<String>();
		try {
			conn = getConn();
			try {
				st = (PreparedStatement) conn.prepareStatement(sql);
			} catch (SQLException e) {
				System.out.println("预锟斤拷锟斤拷锟斤拷锟�");
				e.printStackTrace();
			}
			try {
				rs = st.executeQuery();
			} catch (SQLException e) {
				System.out.println("执锟斤拷select锟斤拷锟斤拷");
				e.printStackTrace();
			}
	      try {
			rs.last();
	      } catch (SQLException e) {
			System.out.println("指锟斤拷锟斤拷转锟斤拷锟斤拷");
			e.printStackTrace();
	      }
	     
	      try {
	    	//System.out.print(rs.getString("sLinkNum"));
			String rs1=rs.getString("sLinkNum");
			links.add(rs1);
			System.out.println(rs1);
	      } catch (SQLException e) {
			System.out.println("锟斤拷锟铰硷拷录取锟斤拷锟斤拷锟斤拷");
			e.printStackTrace();
	      }
	      int i=0;
	    i++;
		try {
			while(rs.previous()&&i<10){
				//System.out.println(rs.getString("sLinkNum"));
				links.add(rs.getString("sLinkNum"));
				i++;
			}
		} catch (SQLException e) {
			System.out.println("指锟斤拷锟斤拷转锟斤拷锟斤拷");
			e.printStackTrace();
		}
		
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			try {
				release(conn, st, rs);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return links;
	}
	
	//get total number of a kind of links in current round
	public static int getLinksNum(String webId, int type){
		//type:
		//1 sinfo successful info links in total
		//2 finfo failed info links in total
		//3 squery successful query links in total
		//4 fquery successful query links in total
		String sf = "";
		String iq = "";
		if(type==1){
			sf = "sLinkNum";
			iq = "info";
		}
		else if(type == 2){
			sf = "fLinkNum";
			iq = "info";
		}
		else if(type == 3){
			sf = "sLinkNum";
			iq = "query";
		}
		else if(type == 4){
			sf = "fLinkNum";
			iq = "query";
		}
		else{
			return 0;
		}
		int sum = 0;
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String[] param = {"round"};
		String round = DBUtil.select("current", param, Integer.parseInt(webId))[0][0];
		String r = round.split("-")[0]+"-%";
		String sql = "select * from status where webId=\'"+webId+"\' and type=\'"+iq+"\' and round LIKE \'"+r+"\';" ;
		 //System.out.println(sql);
		ArrayList<String> links=new ArrayList<String>();
		try {
			conn = getConn();
			try {
				st = (PreparedStatement) conn.prepareStatement(sql);
			} catch (SQLException e) {
				System.out.println("预锟斤拷锟斤拷锟斤拷锟�");
				e.printStackTrace();
			}
			try {
				rs = st.executeQuery();
			} catch (SQLException e) {
				System.out.println("执锟斤拷select锟斤拷锟斤拷");
				e.printStackTrace();
			}
			
		try {
			while(rs.next()){
				//System.out.println(rs.getString("sLinkNum"));
				String sn = rs.getString(sf);
				sum += Integer.parseInt(sn);
				
			}
		} catch (SQLException e) {
			System.out.println("指锟斤拷锟斤拷转锟斤拷锟斤拷");
			e.printStackTrace();
		}
		
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			try {
				release(conn, st, rs);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return sum;
	
	}
	
	public static int getTotalSinfoLinks(String webId){
		int sum = 0;

		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String[] param = {"round"};
		String round = DBUtil.select("current", param, Integer.parseInt(webId))[0][0];
		String r = round.split("-")[0]+"-%";
		String sql = "select * from status where webId=\'"+webId+"\' and type=\'info\' and round LIKE \'"+r+"\';" ;
		 //System.out.println(sql);
		ArrayList<String> links=new ArrayList<String>();
		try {
			conn = getConn();
			try {
				st = (PreparedStatement) conn.prepareStatement(sql);
			} catch (SQLException e) {
				System.out.println("预锟斤拷锟斤拷锟斤拷锟�");
				e.printStackTrace();
			}
			try {
				rs = st.executeQuery();
			} catch (SQLException e) {
				System.out.println("执锟斤拷select锟斤拷锟斤拷");
				e.printStackTrace();
			}
			
		try {
			while(rs.next()){
				//System.out.println(rs.getString("sLinkNum"));
				String sn = rs.getString("sLinkNum");
				sum += Integer.parseInt(sn);
				
			}
		} catch (SQLException e) {
			System.out.println("指锟斤拷锟斤拷转锟斤拷锟斤拷");
			e.printStackTrace();
		}
		
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			try {
				release(conn, st, rs);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return sum;
	}
	public static int delete(String table, String[] pn, String[] pv)
	{
		Connection conn = null;
		PreparedStatement st = null;
		int rs = 0;
		String sql="delete from "+table;
		String cond=" where ";
		for(int i=0;i< pn.length;i++)
		{
			cond+=pn[i]+" = "+pv[i]+" ,";
		}
		if(pn!=null&&pn.length!=0)
		{
			sql+=cond.substring(0, cond.length()-2)+" ;";
		}
		else
			sql+=" ;";
		try
		{
			conn=getConn();
			st = (PreparedStatement) conn.prepareStatement(sql);
			rs=st.executeUpdate();
		}
		catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally 
		{
			try
			{
				release(conn, st, null);
			} catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rs;
	}
	 public static List populate(ResultSet rs , Class clazz) throws SQLException, InstantiationException, IllegalAccessException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int colCount = rsmd.getColumnCount();
         List list = new ArrayList();
         Field[] fields = clazz.getDeclaredFields();
         while(rs.next()){
             Object obj = clazz.newInstance();
             for(int i = 1;i<=colCount;i++){
                 Object value = rs.getObject(i);
                 for(int j=0;j<fields.length;j++){
                     Field f = fields[j];
                     if(f.getName().equalsIgnoreCase(rsmd.getColumnName(i))){
                         boolean flag = f.isAccessible();
                         f.setAccessible(true);
                         f.set(obj, value);
                         f.setAccessible(flag);
                     }
                 }
             }
             list.add(obj);
         }
        return list;
	}
	public static void main(String[] args) throws FileNotFoundException {
		String[] websiteParam={"*"};
		//Struct struct=DBUtil.select("structedparam", structparams, webId);
		WebSite website=(WebSite) DBUtil.selectMutilpara("website", websiteParam, 138,WebSite.class).get(0);
		System.out.println(website.getWebId());
	}

}
