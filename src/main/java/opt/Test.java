package opt;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import crawler_S.DBUtil;
import crawler_S.JsonProcess;
import crawler_S.Login;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import model.ExtraConf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Test {
	 static Gson g = new Gson();
	 String webId="118";
	private static RAMMD5Dedutor maindedu;
	private static RAMMD5Dedutor subdedu;
	public Test (String webId) {
		this.webId=webId;
	}
	 static CookieStore cookieStore = null;

	/*目的是测试能否利用配置信息进行正常的登陆*/
	public static void main(String[] args) throws Exception {
		// check( webId);
		//Runtime.getRuntime().addShutdownHook(new Exitor());
		Test test =new Test("119");
//		System.setProperty("webdriver.firefox.marionette", "D:\\\\workspace\\\\crawler_Structured\\\\geckodriver.exe");
	/*	System.setProperty("webdriver.firefox.bin", "D:\\program\\firefox\\firefox.exe");
		
		System.setProperty("webdriver.gecko.driver", "D:\\workspace\\crawler_Structured\\geckodriver.exe");
		FirefoxOptions option = new FirefoxOptions();
		
		option.addArguments("--headless");
		option.addArguments("--disable-gpu");
		option.addArguments("window-size=1024,768");
		option.addArguments("--no-sandbox");
		option.addArguments("–-disable-plugins");
		option.addArguments("–-disk-cache-size=80");
		 ExtraConf extraConf=(ExtraConf) DBUtil.selectMutilpara("extraconf", new String[] {"*"}, 123,ExtraConf.class).get(0);
		 getCookie( extraConf);*/
//		WebDriver d=new FirefoxDriver(option);
//		d.get("https://www.baidu.com");
//		 d.quit();
		
		
         //		String s=test.read_file("D:\\table\\provty\\124\\html\\0\\1553503148185.html");
//		String s1=test.read_file("D:\\table\\provty\\124\\html\\0\\1553503145050.html");
//		Map<String, String> headerMap=new HashMap<String, String> ();
//		headerMap.put("Cookie", "pgv_pvi=1966529536; _qddaz=QD.wb1kbo.v9hs8l.jq987s8j; OZ_1U_2043=vid=vc273522f62105.0&ctime=1550126914&ltime=1550126913; Hm_lvt_161777e018286efa0b721665190fb589=1550126841; userid=5068; username_zh=%e6%b5%8e%e5%8d%97%e5%b8%82; username_en=431200000000; phone=12345678901; areaIds=431200000000; useroid=4312000000000001; user_type=0; pwd=0B4E7A0E5FE84AD35FB5F95B9CEEAC79; projuseroid=4312000000000001; projareaIds=431200000000; ASP.NET_SessionId=42cxb2dykuvyhz4elflxuztd");
//		JsonObject json= g.fromJson("{\"pagenumber\":1,\"pagesize\":2,\"poorproperty\":\"\",\"poorcause\":\"\",\"planOutPoor\":\"\",\"realname\":\"\",\"name6\":\"\",\"basicArea\":\"\",\"txtYear\":\"\",\"Aad105\":\"\",\"isHelp\":\"\",\"isHelpPeople\":\"\",\"isImmigrant\":\"\",\"isPlan\":\"\",\"isNull\":\"0\",\"AreaType\":\"\",\"Aah006\":\"\",\"Aad003\":\"\",\"condition\":\"\",\"membercondition\":\"\",\"orders\":\"\",\"sorts\":\"\",\"poorFamilyType\":\"\"}", JsonObject.class);
//		ExtraConf extraConf=(ExtraConf) DBUtil.selectMutilpara("extraconf", new String[] {"*"}, 124,ExtraConf.class).get(0);
//	
			////System.out.println(post("http://ai.inspur.com/Archive/PoorFamilyList-GetPoorFamilyData", json, headerMap).toString());
	//	post("http://ai.inspur.com/Archive/PoorFamilyList-GetPoorFamilyData", json.toString(),cookie);
		
		
		
//	
//		Iterator<Entry<String, JsonElement>> j= json.entrySet().iterator();
//		while(j.hasNext()) {
//			System.out.println(j.next().getKey());
//		}
		
		
		//test.test();
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		//+20
		cm.setMaxTotal(20);
		cm.setDefaultMaxPerRoute(20);
		JsonProcess.client = HttpClients
				.custom()
				.setRetryHandler(new StandardHttpRequestRetryHandler())
				.setConnectionManager(cm)
				.build();
		ExtraConf extraConf=(ExtraConf) DBUtil.selectMutilpara("extraConf", new String[] {"*"}, 126,ExtraConf.class).get(0);
        System.out.println(extraConf.getLoginUrl());

		try {
			JsonProcess.cookies= Login.getCookie(extraConf);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(JsonProcess.cookies);
	//	getpic(JsonProcess.client,"1556526602685",JsonProcess.cookies);
//		Set<String> picid=Downloader.getPicId(JsonProcess.client,"1556526527415",JsonProcess.cookies);
//		for(String i:picid)
//			System.out.println(i);
	}
	public static void getpic(CloseableHttpClient httpClient, String txt, String cookie) throws Exception, IOException {
	    HttpGet get=new HttpGet("http://ai.inspur.com/Download/1556526602685/arc");//"http://ai.inspur.com/Download/"+txt+"arc");
	    HttpEntity entity=null;
	    get.setHeader("Cookie",cookie);	
	     
    	CloseableHttpResponse response=null;
    	 //InputStream is=null;
         response = httpClient.execute(get);
       
        int status=response.getStatusLine().getStatusCode();
        
        entity = response.getEntity();
        
       
        if (entity != null)
        {
		    InputStream in = entity.getContent();
//		    String html=EntityUtils.toString(entity, "utf-8");
//		    System.out.print(html);
			  FileUtils.copyInputStreamToFile(in, new File("D:/table/5d6034a85edf8db1ffb1218e0b23dd54574e74b8.jpg"));
			in.close();//savePicToDisk(in, filePath, id+"_"+picid.get(key));
        }

	}
	public static String getCookie(ExtraConf extraConf) throws Exception {
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
			String rootPath = System.getProperty("java.class.path");
			int lastIndex = rootPath.lastIndexOf(File.separator);
			rootPath = rootPath.substring(0, lastIndex);
			System.setProperty("webdriver.firefox.bin", "D:\\program\\firefox\\firefox.exe");
			
			System.setProperty("webdriver.gecko.driver", "D:\\workspace\\crawler_Structured\\geckodriver.exe");
		
		}
		FirefoxOptions option = new FirefoxOptions();
		
		option.addArguments("--headless");
		option.addArguments("--disable-gpu");
		option.addArguments("window-size=1024,768");
		option.addArguments("--no-sandbox");
		option.addArguments("–-disable-plugins");
		option.addArguments("–-disk-cache-size=80");
		
		option.addArguments("--disable-cache");
		WebDriver driver=new FirefoxDriver(option);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
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
				} else if (num == 6) {
					flag=true;
					break;
				}
			}
		}
		Set<org.openqa.selenium.Cookie> cookies;
		if(flag){
			System.out.println("LOGIN error");
			throw new Exception();
			
		}
		else cookies=driver.manage().getCookies();
		String cookie="";
		//System.out.println(cookies.toString());
		for(org.openqa.selenium.Cookie s:cookies) {	
			cookie+=s.toString().substring(0,s.toString().indexOf(";")+1);
		}
		
		driver.quit();
		return cookie;
	}
	
	public static JsonObject post(String url, String json, String cookie){
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        JsonObject response = null;  
        try {  
            StringEntity s = new StringEntity(json);
            s.setContentEncoding("UTF-8");  
            s.setContentType("application/json");  
            post.setEntity(s);  
            post.setHeader("Cookie",cookie);
            HttpResponse res = client.execute(post);
            System.out.print(res.getStatusLine().getStatusCode() );
            if(res.getStatusLine().getStatusCode() == HttpStatus.OK_200){
                HttpEntity entity = res.getEntity();
                
                byte[] bytes = EntityUtils.toByteArray(entity);
            	String html ="";
            	html=new String(bytes,"utf-8");
                System.out.print(html);
                response = g.fromJson(html, JsonObject.class);
                JsonObject   data=response.getAsJsonObject("d");
                JsonArray jsonArray = data.getAsJsonArray("rows");
                //System.out.println(jsonArray.toString());
               
                for (int i = 0; i < jsonArray.size(); i++) {
                	JsonObject innerJsonObject = (JsonObject) jsonArray.get(i);
                	Iterator<Entry<String, JsonElement>> j= innerJsonObject.entrySet().iterator();
//            		while(j.hasNext()) {
//            			System.out.println(j.next().getKey());
//            		}
                 }
              //  response = new JsonObject(new JsonTreeReader(new InputStreamReader(entity.getContent(),charset)));  
            }  
        } catch (Exception e) {
            throw new RuntimeException(e);
        }  
        return response;  
    }  
	public static void setCookieStore(HttpResponse httpResponse) {
	    System.out.println("----setCookieStore");
	    cookieStore = new BasicCookieStore();
	    // JSESSIONID
	    String setCookie = httpResponse.getFirstHeader("Set-Cookie")
	        .getValue();
	    String JSESSIONID = setCookie.substring("JSESSIONID=".length(),
	        setCookie.indexOf(";"));
	    System.out.println("JSESSIONID:" + JSESSIONID);
	    // 新建一个Cookie
	    BasicClientCookie cookie = new BasicClientCookie("JSESSIONID",
	        JSESSIONID);
	    cookie.setVersion(0);
	    cookie.setDomain("127.0.0.1");
	    cookie.setPath("/CwlProClient");
	    // cookie.setAttribute(ClientCookie.VERSION_ATTR, "0");
	    // cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "127.0.0.1");
	    // cookie.setAttribute(ClientCookie.PORT_ATTR, "8080");
	    // cookie.setAttribute(ClientCookie.PATH_ATTR, "/CwlProWeb");
	    cookieStore.addCookie(cookie);
	  }
    private static void addHeaders(HttpEntityEnclosingRequestBase httpRequest, Map<String, String> headerMap){
        for(Map.Entry<String, String> entry : headerMap.entrySet()){
            httpRequest.addHeader(entry.getKey(), entry.getValue());
        }
    }
	public String read_file(String path) throws Exception {
		File file = new File(path);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];  
     
        FileInputStream in = new FileInputStream(file);
        in.read(filecontent);  
        in.close();  
        
		return new String(filecontent, "utf-8");
	}
	 void print(String s){
		try {
			System.out.println("0: "+webId);
			int i=8;
			print1("");
			i=i/0;
		}catch(Exception e) {
			
			RetryUtil.setRetryTimes(5).retry(this,"");
		}	
	}
	 void print1(String s){
		 int i=8;
			try {
				System.out.println("1: "+webId);
				
				i=i/0;
			}catch(Exception e) {
				try {
					
					i=i/0;
					
				}catch(Exception e1) {
					RetryUtil.setRetryTimes(3).retry(this,"");
				}
			}
		
	 }

	void test() throws Exception, InterruptedException, IOException {
		 webId = "124";
		

			
			String[] con = { "webId", "type" };
			String[] conV = { webId + "", "formula" };
			
			maindedu=new RAMMD5Dedutor(Paths.get("D:/provty"+"/"+webId+"/"));
			Selenium_opt selenium = new Selenium_opt(Integer.parseInt(webId),con,conV,maindedu,1);
		
			//Selenium_opt selenium_subpage = new Selenium_opt(Integer.parseInt(webId),con1,conV1,subdedu,2);
			System.out.println("start");
			try {
				selenium.login();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.setOut(new PrintStream(new File("E:/output.txt"), "utf-8"));
	}
	
}
