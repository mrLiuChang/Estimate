package opt;

import crawler_S.DBUtil;
import crawler_S.Pattern;
import crawler_S.Queries;
import crawler_S.Scheduler;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import model.ExtraConf;
import model.Struct;
import model.WebSite;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Selenium_opt_Backup implements Runnable {
	private RAMMD5Dedutor dedu=null;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private  int newDataQueriesNum = 0;
	PrintStream out=null;
	PrintStream err=null;
	private  int N = 1;
	private  int C = 0;
	private  Struct struct=null;
	private  WebSite website=null;
	private  ExtraConf extraConf=null;
	private WebDriver driver = null;
	private List<String> paramValueList = new ArrayList<>();
	private  int sum = 0;
	Boolean thread_flag=false;
	private  int webId = 0;
	private  int round = 0;
	private  int type = 0;
	private Set<String> subpageId=new HashSet<String>();
	private  int SampleData_sum = 0;
	private  TableProcess tableObject=null;
	private DataProcess dp =null;
	Set<Cookie> cookies=null;
	AtomicInteger pageNum = new AtomicInteger(0);
	
	static ChromeDriverService service = null;
	public Selenium_opt_Backup(int webId, String[] con, String[] conV, RAMMD5Dedutor dedu, int pagetype) throws InterruptedException, IOException {
		// ParamSetter.initialByWebId(webId+"");
		init(webId);
		this.webId = webId;
		this.type=pagetype;
		this.dp = new DataProcess(webId + "");
		String[] fPara = {"formula"};
		if(DBUtil.select("pattern_structed",fPara,con,conV).length>0)
			this.tableObject=new  TableProcess(con,conV);
		this.dedu=dedu;
		paramValueList= Arrays.asList((struct.getparamValueList().trim().split(";")));
		String path=website.getWorkFile() + "/" + webId+ "/";
		
		if(pagetype==1){
			out=new PrintStream(new File( path + "output.txt"), "utf-8");
			err=new PrintStream(new File( path + "err.txt"), "utf-8");
		}else{
			out=new PrintStream(new File(path+ "subpage/output.txt"), "utf-8");;
			err=new PrintStream(new File( path + "subpage/err.txt"), "utf-8");
		}
		
//	    System.setOut(out);
//		System.setErr(err);
//		if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
//			String rootPath = System.getProperty("java.class.path");
//			int lastIndex = rootPath.lastIndexOf(File.separator);
//			rootPath = rootPath.substring(0, lastIndex);
//			System.setProperty("webdriver.chrome.driver", rootPath + File.separator+"chromedriver.exe");
//		   // out.println(rootPath);
//		}
	}
	private  void init(Integer webId){
		String[] params = {"*"};
		String[] roundParam = { "round" };
		 struct=(Struct) DBUtil.selectMutilpara("structedparam", params, webId,Struct.class).get(0);
		 website=(WebSite) DBUtil.selectMutilpara("website", params, webId,WebSite.class).get(0);
		 round = Integer.parseInt(DBUtil.select("estimate", new String[]{"walkTimes"}, new String[]{"estiId"}, new String[]{webId+""})[0][0]);

		extraConf=(ExtraConf) DBUtil.selectMutilpara("extraconf", params, webId,ExtraConf.class).get(0);
		 
	}
	private  void webdriverInit() throws IOException {
	
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--headless");
			chromeOptions.addArguments("--disable-gpu");
			chromeOptions.addArguments("window-size=1024,768");
			chromeOptions.addArguments("--no-sandbox");
			chromeOptions.addArguments("–-disable-plugins");
			chromeOptions.addArguments("–-disk-cache-size=80");
			
			chromeOptions.addArguments("--disable-cache");
			Map<String, Object> prefs = new HashMap<String, Object>();
			
			prefs.put("profile.managed_default_content_settings.images", 2); // 2就是代表禁止加载的意思
			chromeOptions.setExperimentalOption("prefs", prefs);

			driver = new ChromeDriver(chromeOptions);
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
		}
	public  WebDriver login() throws InterruptedException, IOException {
		if (driver == null)
			 webdriverInit();
		int num = 0;
		boolean flag=false;
		if (extraConf.getLoginUrl() != null && extraConf.getLoginUrl().length() > 0) {
			while (true) {
				driver.get(extraConf.getLoginUrl());
				
				WebElement txtUserName = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id(extraConf.getUserNameXpath())));
				// out.println(txtUserName);
				txtUserName.sendKeys(extraConf.getUserName());

				WebElement txtPassword = (new WebDriverWait(driver,10))
						.until(ExpectedConditions.presenceOfElementLocated(By.id(extraConf.getPasswordXpath())));
				txtPassword.sendKeys(extraConf.getPassword());
				WebElement submit = (new WebDriverWait(driver, 10))
						.until(ExpectedConditions.presenceOfElementLocated(By.id(extraConf.getSubmitXpath())));
				submit.click();
				Thread.sleep(3000);
				num++;
				if (!driver.getCurrentUrl().equals(extraConf.getLoginUrl())) {
					Thread.sleep(2000);
					break;
				} else if (num == 5) {
					out.println("请确认用户名、密码，然后重试");
					flag=true;
					break;
				}
			}
		}
		if(flag){
//			exit();
			System.exit(1);
		}
		else cookies=driver.manage().getCookies();
		return driver;
	}
	
	private  void switchToSearchpage() throws Exception {
		if (struct.getiframeNav() != null && !"".equals(struct.getiframeNav())) {
			driver.switchTo().frame(struct.getiframeNav());
			if (struct.getnavValue() != null && !"".equals(struct.getnavValue())) {
				WebElement pkh = (new WebDriverWait(driver, 10))
						.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(struct.getnavValue())));
				pkh.click();
				Thread.sleep(extraConf.getTimeout());
				driver.switchTo().defaultContent();
			}
		}
		if (struct.getiframeCon() != null && !"".equals(struct.getiframeCon()))
			driver.switchTo().frame(struct.getiframeCon());
		Thread.sleep(extraConf.getTimeout());
		 (new WebDriverWait(driver, 5)).until(ExpectedConditions.elementToBeClickable(By.id(struct.getsearchButton())));
			
	}
	private  int Download_mainPage(String query)throws Exception {
		String filepath = website.getWorkFile() +"/"+ webId + "/html/"+round+"/";
		File pagePath = new File(filepath);
		if (!pagePath.exists())
			pagePath.mkdirs();
		ExecutorService service = Executors.newFixedThreadPool(extraConf.getThreadNum());
		BlockingDeque<String> queue = new LinkedBlockingDeque<>(extraConf.getThreadNum() * extraConf.getThreadNum());
		AtomicInteger downloadNum = new AtomicInteger(0);
		
		String MD5Path = website.getWorkFile()+"/" + webId + "/dataMD5.txt";
		Runnable produceTask = () -> {
			WebElement nextPage = driver.findElement(By.xpath(struct.getnextPageXPath()));
			int pageNum = 1;
			try {
				queue.put(driver.getPageSource());
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();err.println("queue put error "+pageNum);
			}
			while ((!nextPage.getAttribute("class").contains("disabled"))) {
				Actions actions = new Actions(driver);
				actions.moveToElement(nextPage).click().perform();
				pageNum++;
				try {
					Thread.sleep(extraConf.getTimeout());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.getStackTrace();
				}
				actions.release();
				try {	
					queue.put(driver.getPageSource());
					if(pageNum%100==0){
						try{
							ReleaseAndTopageNum(pageNum+"",query);
						}catch(Exception e){
							retry_method(5,pageNum+"",query,1);
						}
					}
					nextPage = (new WebDriverWait(driver, 10))
							.until(ExpectedConditions.presenceOfElementLocated(By.xpath(struct.getnextPageXPath())));									
				} catch (Exception ex) {
					 ex.printStackTrace();
					 err.println("interrupted in put into queue "+pageNum);
					 continue;
				 }
			} 
			thread_flag=true;
		}; 


		Runnable consumeTask = () -> {
		  while (true) {
			  try { 				  
				String txt = queue.poll(extraConf.getThreadNum(), TimeUnit.SECONDS);
			    if (txt == null||"".equals(txt)) {
                      if (thread_flag) break;
                      continue;
                 }          
				PrintWriter pw = new PrintWriter(new File(pagePath, System.currentTimeMillis()+ ".html"), "utf-8");
				pw.print(txt);
				pw.flush();
				pw.close();
				
				int changeSize=Table_process(webId,txt,MD5Path);
				for(int i=0;i<changeSize;i++){
					downloadNum.incrementAndGet();
				}
			  } catch (Exception ex) {
				  ex.printStackTrace();
				  err.println("consumeTask process error");
			  }
		  }
		};
		service.execute(produceTask);
		for (int i = 0 ; i < extraConf.getThreadNum() - 1 ; i ++) {
		  service.execute(consumeTask);
		}
		service.shutdown();
		while (true) {
		  try {
			  if (service.awaitTermination(extraConf.getThreadNum(), TimeUnit.SECONDS)) {
				  break;
			  }
			  //out.println("have download "+downloadNum.get()+" data");
		  } catch (InterruptedException ex) {
			  ex.printStackTrace();
			  err.println("interrupted when wait for thread pool");
		  }
		}
		out.println("\t\t"+" Download "+downloadNum.get()+" data");
		return  downloadNum.get();
	}
	private  void Download_Default(int webId) throws Exception {
		driver = login();
		driver.get(website.getIndexUrl());
		switchToSearchpage();
		
		WebElement btnSearch = driver.findElement(By.id(struct.getsearchButton()));
		btnSearch.click();
		try{
			Thread.sleep(extraConf.getTimeout());
			 (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.name(struct.getresultRow()))); 
				
			}catch(Exception e){
				retry_method(5,struct.getresultRow(),"",2);
		}	
		if(type==1)
			Download_mainPage_Default(webId);
		else{
			try {
				Download_subpage("");
			}catch(Exception e) {
				RetryUtil.setRetryTimes(3).retry(this,webId);;//Download_subpage("",pageNum);
			}
			Pattern_process(dedu);
			round++;
		}
		
		out.println("\t"+df.format(new Date())+" Data_process end");
	}
//	void Download_subpage(String query,AtomicInteger pageNum2){
//		
//	}
	private  void Download_mainPage_Default(int webId) throws Exception {
		// ��ʼ���أ�M2status active
		String[] para_status2 = { "M1status", "M2status" };
		String[] value_status2 = { "done", "active" };
//		DBUtil.update("current", para_status2, value_status2, webId);
		out.println("\t"+df.format(new Date())+" Data_download begin");
		Download_mainPage("");
	  
		out.println("\t"+df.format(new Date())+" Data_download end");
		String[] para_status3 = { "M2status", "M3status" };
		String[] value_status3 = { "done", "active" };

		out.println("\t"+df.format(new Date())+" Data_process begin");
		String[] p = { "webId", "round", "type", "sLinkNum", "fLinkNum" };
		String[] v1 = { webId + "", "1", "info", "0", "0" };
		String[] v2 = { webId + "", "1", "query", "0", "0" };
		DBUtil.insert("status", p, v1);
		DBUtil.insert("status", p, v2);
		
//		DBUtil.update("current", para_status3, value_status3, webId);
		round++;
		//Data(webId, filepath, workFile+"/"+webId + "/subpage/" + round);
		String[] pv = { round+"" };
		String[] param = { "round" };
//		DBUtil.update("current", param, pv, webId);
	}
	private  void ReleaseAndTopageNum(String pageNum, String query ) throws Exception {
		//out.println("ReleaseAndTopageNum");
		Iterator<Cookie> cookiesSet=cookies.iterator();
		driver.quit();
		driver=null;
		webdriverInit();
		driver.get(extraConf.getLoginUrl());
		while(cookiesSet.hasNext()){
			driver.manage().addCookie(cookiesSet.next());
		}
		driver.get(website.getIndexUrl());
		switchToSearchpage();
		Thread.sleep(extraConf.getTimeout());
		Actions action = new Actions(driver);
		if(round!=0)
			getSearchPage(query);
		else if(round==0){
		   WebElement btnSearch = (new WebDriverWait(driver, 5)).until(ExpectedConditions.presenceOfElementLocated(By.id(struct.getsearchButton()))); 		 
		   btnSearch.click();
		}
		try{ 
			Thread.sleep(extraConf.getTimeout());
			 (new WebDriverWait(driver, 5)).until(ExpectedConditions.presenceOfElementLocated(By.name(struct.getresultRow()))); 
			 WebElement pageNo = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(struct.getpageNumXPath()))); 

			pageNo.clear();
			pageNo.sendKeys(pageNum);
			action.sendKeys(Keys.ENTER).perform();

		    (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(struct.getpageNumXPath()))); 
			Thread.sleep(extraConf.getTimeout());
			action.release();	
		}catch(Exception e){
				e.printStackTrace();
				err.println("ReleaseAndTopageNum: WebDriverWait error");
				try{
					driver.switchTo().defaultContent();
					switchToSearchpage();
					if(round!=0)
						getSearchPage(query);
					else if(round==0){
						WebElement btnSearch = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id(struct.getsearchButton()))); 
					//	 (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By.id(struct.getsearchButton())));
							
						btnSearch.click();
					}	
					Thread.sleep(extraConf.getTimeout());
					 (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.name(struct.getresultRow()))); 	
				}catch(Exception e1){
					retry_method(5,pageNum,query,1);
				}	
		}	
		
	}
	 private void retry_method(int retryTimes, String param, String query, int type) throws Exception {
		boolean isException =false;
        try{
        	if(type==1)
        		ReleaseAndTopageNum(param,query);
        	else if(type==2){
        		out.println("retry:"+retryTimes);
        		WebElement btnSearch = driver.findElement(By.id(struct.getsearchButton()));
        		btnSearch.click();
        		 (new WebDriverWait(driver, 20)).until(ExpectedConditions.presenceOfElementLocated(By.name(param))); 
        			
        	}   		
        }catch(Exception e){
        	 isException = true;
        }
        if(isException && retryTimes > 0){
        	retry_method(--retryTimes, param,query,type);
        }     
	 }
	 /*private  void ReleaseAndTopageNum(String pageNum,String query ) throws Exception{
		//out.println("ReleaseAndTopageNum");
		try{
			Iterator<Cookie> cookiesSet=cookies.iterator();		
			driver.quit();
			driver=null;
			webdriverInit();
			driver.get(extraConf.getLoginUrl());
			while(cookiesSet.hasNext()){
				driver.manage().addCookie(cookiesSet.next());
			}
			driver.get(website.getIndexUrl());
			Thread.sleep(extraConf.getTimeout());
			switchToSearchpage();
			Thread.sleep(extraConf.getTimeout());
			Actions action = new Actions(driver);
			if(round!=0)
				getSearchPage(query);
			else if(round==0){
			   WebElement btnSearch = (new WebDriverWait(driver, 5)).until(ExpectedConditions.presenceOfElementLocated(By.id(struct.getsearchButton()))); 		 
			   btnSearch.click();
			}
			try{ 
				Thread.sleep(extraConf.getTimeout());
				 (new WebDriverWait(driver, 5)).until(ExpectedConditions.presenceOfElementLocated(By.name(struct.getresultRow()))); 
				 WebElement pageNo = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(struct.getpageNumXPath()))); 
	
				pageNo.clear();
				pageNo.sendKeys(pageNum);
				action.sendKeys(Keys.ENTER).perform();
	
			    (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(struct.getpageNumXPath()))); 
				Thread.sleep(extraConf.getTimeout());
				action.release();	
			}catch(Exception e){
					e.printStackTrace();
					err.println("ReleaseAndTopageNum: WebDriverWait error");
						driver.switchTo().defaultContent();
						switchToSearchpage();
						if(round!=0)
							getSearchPage(query);
						else if(round==0){
							WebElement btnSearch = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id(struct.getsearchButton()))); 
						//	 (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By.id(struct.getsearchButton())));
								
							btnSearch.click();
						}	
						Thread.sleep(extraConf.getTimeout());
						 (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.name(struct.getresultRow()))); 					
				}	
		}catch(Exception e1){
			e1.printStackTrace();
			RetryUtil.setRetryTimes(5).retry(this,pageNum,query);
		}	
	}
	 private void retry_method(int retryTimes,String param,String query) throws Exception {
		boolean isException =false;
        try{	
    		out.println("retry:"+retryTimes);
    		WebElement btnSearch = driver.findElement(By.id(struct.getsearchButton()));
    		btnSearch.click();
    		(new WebDriverWait(driver, 20)).until(ExpectedConditions.presenceOfElementLocated(By.name(param)));    			       		
        }catch(Exception e){
        	 isException = true;
        }
        if(isException && retryTimes > 0){
        	retry_method(--retryTimes, param,query);
        }     
	 }*/
//	 private void retry_wait(String name){
//		 (new WebDriverWait(driver, 20)).until(new ExpectedCondition<Boolean>() {
//				private Boolean apply(WebDriver d) {
//					WebElement element = driver.findElement(By.name(name));
//					return element != null;
//				}
//			});
//	 }
	private  void Download_subpage(String query) throws Exception {
		if(pageNum.get()!=0) {
			ReleaseAndTopageNum(pageNum+"",query);
		}
//		DBUtil.update("current", new String[]{ "M4status" }, new String[]{ "active" }, webId);
		out.println("\t"+df.format(new Date())+" Data_download begin");
		String MD5Path = website.getWorkFile() +"/"+ webId + "/subpage/dataMD5.txt";
		File subpagePath = new File(website.getWorkFile() +"/"+ webId + "/subpage/" + round + "/");
		if (!subpagePath.exists())
			subpagePath.mkdirs();
		ExecutorService service = Executors.newFixedThreadPool(extraConf.getThreadNum());
		BlockingDeque<String> queue = new LinkedBlockingDeque<>(extraConf.getThreadNum() * extraConf.getThreadNum());
		AtomicInteger downloadNum = new AtomicInteger(0);
		
		Runnable produceTask = () -> {
			boolean first=true;
			WebElement nextPage = driver.findElement(By.xpath(struct.getnextPageXPath()));
			
			do {			
				Actions action = new Actions(driver);
				if(!first){
					action.moveToElement(nextPage).click().perform();
					pageNum.incrementAndGet();
					try {
						Thread.sleep(extraConf.getTimeout());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();err.println("Thread.sleep ");
						
					}
				}
				if (struct.getiframeSubParam() != null && !"".equals(struct.getiframeSubParam()) && struct.getresultRow() != null && !"".equals(struct.getresultRow())) {
					List<WebElement> poorList = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(struct.getresultRow())));
					for (int j = 0; j < poorList.size(); j++) {
						String u = driver.findElements(By.name(struct.getresultRow())).get(j).getAttribute("value").trim();
						if (u != null && !"".equals(u)) {
							//out.println(u);
							if(subpageId.contains(u))
								continue;
							else subpageId.add(u);
							try {
								action.doubleClick(driver.findElements(By.name(struct.getresultRow())).get(j)).perform();
								Thread.sleep(extraConf.getTimeout());
							
								driver.switchTo().defaultContent();
							
								driver.switchTo().frame(struct.getiframeSubParam());
								queue.put(driver.getPageSource());
								action.sendKeys(Keys.ESCAPE).perform();	
								driver.switchTo().defaultContent();
								driver.switchTo().frame(struct.getiframeCon());
								Thread.sleep(extraConf.getTimeout());
							}catch(Exception e){
								e.printStackTrace();err.println("double click error");
								try{
									pageNum.decrementAndGet();;
									e.printStackTrace();err.println("put queue error "+pageNum);
									ReleaseAndTopageNum(pageNum+"",query);
									break;
								}catch(Exception e1){
									e.printStackTrace();err.println("put queue error "+pageNum);
									try {
										retry_method(3,pageNum.get()+"",query,1);
									} catch (Exception e2) {
										// TODO Auto-generated catch block
										break;
									}
									
								}
							}				
						}
					}
				}	
				if(pageNum.get()%50==0) {
					try{
						ReleaseAndTopageNum(pageNum+"",query);
					}catch(Exception e){
						e.printStackTrace();
						err.println("ReleaseAndTopageNum error");
						try {
							retry_method(5,pageNum.get()+"",query,1);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							break;
						}
					}	
				}
				nextPage = (new WebDriverWait(driver, 20)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(struct.getnextPageXPath()))); 
				action.release();
				first=false;
			} while ((!nextPage.getAttribute("class").contains("disabled")));
			thread_flag=true;
		}; 
		Runnable consumeTask = () -> {
		  while (true) {
			  try { 
				String txt = queue.poll(extraConf.getThreadNum(), TimeUnit.SECONDS);
			    if (txt == null||"".equals(txt)) {
					  if (thread_flag) break;
					  continue;
				 } 			  
				PrintWriter pw = new PrintWriter(new File(subpagePath,  System.currentTimeMillis()+ ".html"), "utf-8");
				pw.print(txt);
				pw.flush();
				pw.close();
				Table_process(webId,txt,MD5Path);
				
				downloadNum.incrementAndGet();
			  } catch (InterruptedException ex) {
				  ex.printStackTrace();
				 err.print("interrupted in load document and write it to file");
			  } catch (Exception ex) {
				  ex.printStackTrace();
				  err.print("interrupted in load document and write it to file");
			  }
		  }
		};
		service.execute(produceTask);
		for (int i = 0 ; i < extraConf.getThreadNum() - 1 ; i ++) {
		  service.execute(consumeTask);
		}
		service.shutdown();

		while (true) {
		  try {
			  if (service.awaitTermination(extraConf.getThreadNum(), TimeUnit.SECONDS)) {
				  break;
			  }	  
		  } catch (InterruptedException ex) {
			  ex.printStackTrace();
			  //logger.error("interrupted when wait for thread pool");
		  }
		}
		
		
		out.println("\t\tDownload "+downloadNum.get()+" documents");
		out.println("\t"+df.format(new Date())+" Data_download end");
	}
	
	private  void getSearchPage(String query) throws Exception {
	//	out.println("getSearchPage");
		driver.navigate().refresh();
		try{
			switchToSearchpage() ;
			List<WebElement> arrows = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(struct.getarrow())));
			String[] index = query.split(",");
			Actions actions=new Actions(driver);
			for (int n = 0; n < index.length; n++) {
				String[] value = paramValueList.get(n).split(",");
			//	 out.println(arrows.get(n));
				try {
					actions.moveToElement(arrows.get(n)).click().perform();
					driver.findElement(By.id(value[Integer.parseInt(index[n])])).click();
					actions.moveToElement(arrows.get(n)).click().perform();
				} catch (Exception e) {
					continue;
				}
			}
			actions.release();
			WebElement btnSearch = driver.findElement(By.id(struct.getsearchButton()));
			btnSearch.click();	
		}catch(Exception e){
			e.printStackTrace();err.println("getSearchPage error");
		}
	}
	private ArrayList<String> getQueries(String path) throws IOException {
			ArrayList<String> queries = new ArrayList<>();
			InputStreamReader read = new InputStreamReader(new FileInputStream(new File(path)));
			BufferedReader br = new BufferedReader(read);
			String line = null;
			while ((line = br.readLine()) != null)
				queries.add(line);
			br.close();
			return queries;
	}
	private  void DowndloadMainPage_NDefault(int webId) throws Exception {
		
		Long totalSize=extraConf.getDatabaseSize();
		
			
//		String[] roundParam = { "round", "SampleData_sum" };
//		String[] current_params= DBUtil.select("current", roundParam, webId)[0];
		SampleData_sum = Scheduler.ItemSize;
		
		int Oi = SampleData_sum;
		if (extraConf.getDatabaseSize() != 0 && SampleData_sum / totalSize > 0.9) {
			return;
		}
//		round = Integer.parseInt(current_params[0].trim());
//		out.println("Round-"+round +" begin:");
		PrintWriter pw = new PrintWriter(website.getWorkFile() +"/"+ webId + "/ParamValuelist.txt", "utf-8");
		for (String value : paramValueList) {
			pw.println(value);
			int num = value.split(",").length;
			if (num > C)
				C = num;
			N *= num;
		}
		pw.flush();
		pw.close();
		C = Math.max(C, (int) (Math.log(N) / Math.log(2)));
		String[] params = { "N", "C" };
		String[] values = { String.valueOf(N), String.valueOf(C) };
		DBUtil.update("queryParam", params, values, webId);
		int changeSize=0;
		Queries query = new Queries(webId + "");
		if (driver == null){
			driver = login();
			driver.get(website.getIndexUrl());
			switchToSearchpage();
		}
	while (true) {
//			DBUtil.update("current", para_status4, value_status4, webId);
			query.selectQueries(C);
			ArrayList<String> queries=getQueries(website.getWorkFile()+"/" + webId + "/query/queries.txt");
		
			for (int m = 0; m < queries.size(); m++) {
				getSearchPage(queries.get(m));
				try{
					 (new WebDriverWait(driver, 20)).until(ExpectedConditions.presenceOfElementLocated(By.name(struct.getresultRow()))); 
						
				}catch(Exception e){
					continue;
				}
				List<WebElement> poorList = driver.findElements(By.name(struct.getresultRow()));
				if(poorList.size()<2)
					continue;
				
				out.println("\t"+df.format(new Date())+" Data_download begin");
				
//				DBUtil.update("current", para_status2, value_status2, webId);
				changeSize=Download_mainPage(queries.get(m));
				//download(pagePath, subpagePath);
				// M2status done, M3status active
				out.println("\t"+df.format(new Date())+" Data_download end");
				
//				DBUtil.update("current", para_status3, value_status3, webId);
				//int changeSize = Data(webId, filepath, website.getWorkFile() +"/"+ webId + "/subpage/" + round);

				Oi += changeSize;
				if (changeSize > 0)
					newDataQueriesNum++;
			}
			out.println("\t"+df.format(new Date())+" Data_process begin");
			//Pattern_process();
			round++;
			String[] pv = { round + "" };
//			DBUtil.update("current", new String[] {"round"}, pv, webId);
			DBUtil.update("estimate", new String[]{"walkTimes"}, new String[]{round+""}, new String[]{"estiId"}, new String[]{webId+""});

			
			String[] p = { "webId", "round", "type", "sLinkNum", "fLinkNum" };
			String[] v1 = { webId + "", round + "", "info", "0", "0" };
			String[] v2 = { webId + "", round + "", "query", "0", "0" };
			DBUtil.insert("status", p, v1);
			DBUtil.insert("status", p, v2);
			if(check(Oi,newDataQueriesNum,totalSize)||changeSize==0)
				break;
			out.println("\t"+df.format(new Date())+" Data_process end");

		}
		String[] para_M4status = { "M3status" };
		String[] value_M4status = { "done" };
//		DBUtil.update("current", para_M4status, value_M4status, webId);
		driver.quit();
	}
	private  void DowndloadsubPage_NDefault() throws Exception {
		
		String[] roundParam = { "round", "SampleData_sum" };
		int currentRound=Integer.parseInt(DBUtil.select("estimate", new String[]{"walkTimes"}, new String[]{"estiId"}, new String[]{webId+""})[0][0]);

		String path="";
		if (driver == null){
			driver = login();
			driver.get(website.getIndexUrl());
			switchToSearchpage();
		}
		if(!(currentRound>round)){
//			DBUtil.update("current",  new String[]{ "M4status" }, new String[]{"done"}, webId);
			return;
		}
			
		//List<WebElement> arrows = driver.findElements(By.className(struct.getarrow()));
		while(true){
			out.println("Round-"+round +" begin:");
			path=website.getWorkFile() +"/"+ webId + "/query/queries"+round+".txt";
			
			ArrayList<String> queries=getQueries(path);
			out.println("\t"+df.format(new Date())+" Data_download begin");
			for(int m=0;m<queries.size();m++){
				getSearchPage(queries.get(m));
				try{
					 (new WebDriverWait(driver, 20)).until(ExpectedConditions.presenceOfElementLocated(By.name(struct.getresultRow()))); 
						
				}catch(Exception e){
					continue;
				}
				List<WebElement> poorList = driver.findElements(By.name(struct.getresultRow()));
				if(poorList.size()<2)
					continue;
				Download_subpage(queries.get(m));			
			}
			Pattern_process(dedu);
			if(round==currentRound){
				 currentRound= Integer.parseInt(DBUtil.select("estimate", new String[]{"walkTimes"}, new String[]{"estiId"}, new String[]{webId+""})[0][0]);
				 if(round==currentRound)
					break;
			}
			out.println("\t"+df.format(new Date())+" Data_download begin");
			round++;
			
		}
		
//		DBUtil.update("current",  new String[]{ "M4status" }, new String[]{"done"}, webId);
	}
	private  boolean check(int Oi, int newDataQueriesNum, Long totalSize){
		int i = Math.min(round * C, N);
		if (i == N)
			return true;
		double pi = newDataQueriesNum / i;
		double dataSize = 0;
		if (totalSize == 0)
			dataSize = (1 + (N - i) / i * pi) * Oi;
		else
			dataSize = totalSize;
		if (Oi / dataSize > 0.90)
			return true;
		else return false;
	}


	private  void select(WebDriver driver, String xpath, String query) {
		Select s = new Select(driver.findElement(By.xpath(xpath)));
		s.selectByValue(query);
		try {
			Thread.sleep(extraConf.getTimeout());
		} catch (InterruptedException e) {
			e.printStackTrace();
			err.println("Thread.sleep");
		}
	}
	private   int Table_process(int webId, String txt, String MD5Path) throws Exception {
		int changeSize =0;
		//String MD5Path = website.getWorkFile()+"/" + webId + "/dataMD5.txt";
    	ArrayList<String> data = new ArrayList<String>();
		if(tableObject!=null){
			data=tableObject.getAllTable(txt,website.getIndexUrl().substring(0,website.getIndexUrl().indexOf(":")+3));
			changeSize += dp.Data_process(data, MD5Path,dedu);
        }

		return changeSize;
		
	}
	private   void Pattern_process(RAMMD5Dedutor dedu){
		Pattern p = new Pattern();
		String patternPath="";
        if(type==2)			
        	patternPath= website.getWorkFile() +"/"+ webId + "/subpage/" + round;
        else if(type==1){
        	patternPath= website.getWorkFile() +"/"+ webId + "/html/" + round;
        }
        p.buildPatterns(webId,patternPath,dedu);
	}
	private List<String> getNewDatas(List<String> datas) {
        if (datas.size() == 0) {
            return Collections.emptyList();
        }
        return datas;
    }
	
	@Override
	public void run() {
		try {
			String[] roundParam = { "round" };
			round = Integer.parseInt(DBUtil.select("estimate", new String[]{"walkTimes"}, new String[]{"estiId"}, new String[]{webId+""})[0][0]);

			out.println("Round-"+round +" begin:");
			if (round == 0)
				Download_Default(webId);
			if(type==1){
				DowndloadMainPage_NDefault(webId);
			}
			else if(type==2){
				DowndloadsubPage_NDefault();
			}
//			String pn[] = { "M1status" };
//			String pv[] = { "stop" };// stop terminate
//			DBUtil.update("current", pn, pv, webId);
		} catch (Exception e1) {
			e1.printStackTrace();
			err.println(e1);
			
		}finally{			
		}
	}

}