package crawler_S;

import opt.RAMMD5Dedutor;
import opt.Selenium_opt;
import utils.test;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;

public class Scheduler {

	static String webId="131";
	private static RAMMD5Dedutor maindedu;
	private static RAMMD5Dedutor subdedu=null;
	static Selenium_opt selenium =null;
	static Selenium_opt selenium_subpage=null;
	public static int ItemSize=0;


	public static void main(String[] args) throws InterruptedException, NumberFormatException, IOException {

		/*如果main函数传参*/
		if (args.length >= 1) {
			webId = args[0];
			if (args.length != 1 && args.length != 4) {
				System.exit(1);
			}
			String dbURL = args[1];
			String dbUser = args[2];
			String dbPass = args[3];
			DBUtil.config(dbURL, dbUser, dbPass);
			System.out.println("webId is "+webId);
		}else {
            webId="127";
            String dbURL="jdbc:mysql://localhost:3306/webcrawler?characterEncoding=UTF-8&useSSL=false&useAffectedRows=true&allowPublicKeyRetrieval=true&serverTimezone=GMT";
            String dbUser="root";
            String dbPass="2333";
            DBUtil.config(dbURL, dbUser, dbPass);
        }

		System.out.println("start");


		ParamSetter paramSetter = new ParamSetter();

		/*获取工作路径和driver类型*/
		String[] filePara = {"workFile", "driver", "runningMode"};
		String[] params = DBUtil.select("website", filePara, Integer.parseInt(webId))[0];
		String workfile = params[0];
		String driver = params[1];
		String mode = params[2];

		/*
		 *根据两者 创建后续需要的文件
		 *注意一下创建一个文件路径中包括嵌套路径的方法
		 * */
		paramSetter.createNewfile(workfile, webId);

		/*配置main函数的参数时，需要将输出流输出到文件中。*/
		if (args.length >= 1) {
			System.setOut(new PrintStream(new File(workfile + "/estimate_structed/" + webId + "/output.txt"), "utf-8"));
			System.setErr(new PrintStream(new File(workfile + "/estimate_structed/" + webId + "/err.txt"), "utf-8"));
		}


        /*更新状态*/
		if (mode.equals("structed")) {
			paramSetter.initialByWebId(webId);//initiate database

			/*
			148
			 * 基于连接的从此处开始
			 * */
			if ("0".equals(driver)) {
				Controller_Structured c = new Controller_Structured(webId);
				new Thread(c).start();
			}


			/*基于json接口,149*/
			else if ("2".equals(driver)) {
				Controller_Jsonbase.workFile = workfile;
				Controller_Jsonbase c = new Controller_Jsonbase(webId);
				maindedu = c.dedu;
				new Thread(c).start();
			}


		}else if(mode.equals("unstructed")){

            DBUtil.update("estimate",new String[]{"status"},new String[]{"start"}, new String[]{"estiId"},new String[]{webId});
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String pid = name.split("@")[0];
            System.out.println("pid is "+pid);
            DBUtil.update("estimate", new String[]{"pid"}, new String[]{pid}, new String[]{"estiId"}, new String[]{webId});


            test.processTest(webId);
			//估测完成之后，更新status
			DBUtil.update("estimate", new String[]{"status"}, new String[]{"stop"}, new String[]{"estiId"}, new String[]{webId});

			/*在估测完成之后，更新estimate表的 进程号 pid 为0*/
			pid=0+"";
			DBUtil.update("estimate", new String[]{"pid"}, new String[]{pid}, new String[]{"estiId"}, new String[]{webId});


		}

    }
	private static class Exitor extends Thread {
        @Override
        public void run() {
            System.out.println("start the exit thread");

        	System.out.println("start to close resource");
              try {
            	  if(maindedu!=null)
            		  maindedu.close();//dedu data save
            	  if(subdedu!=null)
            		  subdedu.close();
            	  if(selenium!=null&&selenium.driver!=null) {
                	  selenium.driver.quit();
            	  }
            	  if(selenium_subpage!=null&&selenium_subpage.driver!=null) {
            		  selenium_subpage.driver.quit();
            	  }
              } catch (IOException ex) {
                  //nothing to do
              }

             System.out.println("finish to close resource");
        }
    }
	public synchronized static void inc(int num) {
		ItemSize+=num;
	}
}
