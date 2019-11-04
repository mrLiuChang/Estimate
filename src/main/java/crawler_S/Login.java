package crawler_S;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import model.ExtraConf;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import model.ExtraConf;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static java.lang.System.out;

public class Login {

    public static CloseableHttpClient jnoaMultiThread(String webId, int threadNum, String username, String password, String url) {
        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will be using the HttpClient.
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(threadNum);
        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(20);
        // Increase max connections for localhost:80 to 50
        HttpHost localhost = new HttpHost("locahost", 80);
        cm.setMaxPerRoute(new HttpRoute(localhost), 50);
        // StandardHttpRequestRetryHandler
        CloseableHttpClient httpclient = HttpClients
                .custom()
                .setRetryHandler(new StandardHttpRequestRetryHandler())
                .setConnectionManager(cm)
                .build();

        HttpResponse response;
        HttpEntity entity = null;
        //   List<Cookie> cookies = null ;
        try {
            HttpPost httpPost;
            //  HttpClient httpclient = new DefaultHttpClient();
            httpPost = new HttpPost(url); // 閻€劍鍩涢惂璇茬秿
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            String[] userPa = {"userParam"};
            String[] pwdPa = {"pwdParam"};
            nvps.add(new BasicNameValuePair(DBUtil.select("website", userPa, Integer.parseInt(webId))[0][0], username));
            nvps.add(new BasicNameValuePair(DBUtil.select("website", pwdPa, Integer.parseInt(webId))[0][0], password));

            httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
            response = httpclient.execute(httpPost);

            entity = response.getEntity();
            System.out.println("Login form get: " + response.getStatusLine());
            EntityUtils.consume(entity);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return httpclient;
    }

    public static CloseableHttpClient jnoaMultiThread_post(String webId, int threadNum) {
        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will be using the HttpClient.
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(threadNum);
        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(20);
        // Increase max connections for localhost:80 to 50
        HttpHost localhost = new HttpHost("locahost", 80);
        cm.setMaxPerRoute(new HttpRoute(localhost), 50);
        // StandardHttpRequestRetryHandler
        CloseableHttpClient httpclient = HttpClients
                .custom()
                .setRetryHandler(new StandardHttpRequestRetryHandler())
                .setConnectionManager(cm)
                .build();
        return httpclient;
    }

    // this method needs changing
    public static WebClient jnoa(String webId) {//(String[] paramName, String[] paramValue,String  url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        //config

        WebClient client = new WebClient(BrowserVersion.FIREFOX_52);
        ;
        org.apache.commons.logging.impl.Jdk14Logger logger = (org.apache.commons.logging.impl.Jdk14Logger) LogFactory.getLog("com.gargoylesoftware.htmlunit");
        logger.getLogger().setLevel(Level.OFF);
        client.getOptions().setJavaScriptEnabled(true);    //榛樿鎵цjs锛屽鏋滀笉鎵цjs锛屽垯鍙兘浼氱櫥褰曞け璐ワ紝鍥犱负鐢ㄦ埛鍚嶅瘑鐮佹闇?瑕乯s鏉ョ粯鍒躲??
        client.getOptions().setCssEnabled(false);
        client.getOptions().setThrowExceptionOnScriptError(false); //js杩愯閿欒鏃讹紝鏄惁鎶涘嚭寮傚父
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getOptions().setTimeout(20000);
        client.setAjaxController(new NicelyResynchronizingAjaxController());
        client.waitForBackgroundJavaScript(50000);
        client.setJavaScriptTimeout(50000);
        String[] userParam = {"userName"};
        String[] pwdParam = {"password"};
        String[] loginUrlParam = {"loginUrl"};
        String url = DBUtil.select("extraConf", loginUrlParam, Integer.parseInt(webId))[0][0];

        if (url != (null) && url.length() > 0) {
            System.out.print("loginurl:" + url);
            String username = DBUtil.select("website", userParam, Integer.parseInt(webId))[0][0];
            String password = DBUtil.select("website", pwdParam, Integer.parseInt(webId))[0][0];
            HtmlPage page = null;
            try {
                page = client.getPage(url);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }//
            //   System.out.println(page.asXml());//http://oa.cecic.corp/names.nsf?Login
            //鐧诲綍http://portal.cecic.corp/
            HtmlInput ln = page.getElementByName("Username");
            HtmlInput pwd = page.getElementByName("Password");
            //HtmlInput red = page.getElementByName("RedirectTo");
            List<DomElement> inputsList = page.getElementsByTagName("td");
            DomElement btn = null;
            for (DomElement n : inputsList) {
                if (n.hasAttribute("onclick")) {
                    btn = n;
                    break;
                }
            }
            ln.setAttribute("value", username);
            pwd.setAttribute("value", password);
            try {
                btn.click();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                //		HtmlPage page1 = client.getPage("http://jtzb.cecic.corp/jtzb/dzbx.nsf/vwpublicedbylx?Query=%D2%E2%CF%F2&searchview=&count=20&view=vwpublicedbylx&start=1");
                //| IOException System.out.println(page1.asText());//http://jtzb.cecic.corp/jtzb/dzbx.nsf/vwpublicedbylx?searchview&count=20&Query=%E5%85%B3%E4%BA%8E&view=vwpublicedbylx
            } catch (FailingHttpStatusCodeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }//
            client.waitForBackgroundJavaScript(10000);

            return client;
        } else return client;


    }

    /*public static WebClient joinoa(String webId, String username, String password,String  url) {
           // boolean loginResult = false;

        WebClient client = new WebClient();


        CloseableHttpClient httpclient = HttpClients.createDefault();

            HttpResponse response;
            HttpEntity entity = null;

           // List<Cookie> cookies = null ;

            //缂佸嫬缂撻惂璇茬秿閻ㄥ埦ost閸栵拷
            try {
                 HttpPost httpPost;
               //  HttpClient httpclient = new DefaultHttpClient();
                 httpPost = new HttpPost(url); // 閻€劍鍩涢惂璇茬秿
                 List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                 String[] userPa = {"userParam"};
                 String[] pwdPa = {"pwdParam"};
                 nvps.add(new BasicNameValuePair(DBUtil.select("website", userPa, Integer.parseInt(webId))[0][0],username));
                 nvps.add(new BasicNameValuePair(DBUtil.select("website", userPa, Integer.parseInt(webId))[0][0], password));
                 System.out.println(DBUtil.select("website", userPa, Integer.parseInt(webId))[0][0]);
                 httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
                response = httpclient.execute(httpPost);

                entity = response.getEntity();
                System.out.println("Login form get: " + response.getStatusLine());
              //  EntityUtils.consume(entity);

//	            cookies =  httpclient.getConnectionManager()..getCookieStore().getCookies();
//	            if (cookies.isEmpty()) {
//	                System.out.println("None");
//	            } else {
//	                for (int i = 0; i < cookies.size(); i++) {
//	                    System.out.println("- " + cookies.get(i).toString());
//	                }
//	            }
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (entity != null) {
                byte[] bytes = null;
                String html = null;
                try {
                    bytes = EntityUtils.toByteArray(entity);
                    html = new String(bytes,"GB2312");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //System.out.println(id + " - " + bytes.length + " bytes read");
                System.out.println( html);}

            return client;
        }
    */
    public static WebClient loginOA(String username, String password, String url) {//(String[] paramName, String[] paramValue,String  url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        //config
        WebClient client = new WebClient(BrowserVersion.FIREFOX_52);
        client.getOptions().setJavaScriptEnabled(true);    //榛樿鎵цjs锛屽鏋滀笉鎵цjs锛屽垯鍙兘浼氱櫥褰曞け璐ワ紝鍥犱负鐢ㄦ埛鍚嶅瘑鐮佹闇?瑕乯s鏉ョ粯鍒躲??
        client.getOptions().setCssEnabled(false);
        client.getOptions().setThrowExceptionOnScriptError(false); //js杩愯閿欒鏃讹紝鏄惁鎶涘嚭寮傚父
        client.getOptions().setTimeout(20000);
        client.setAjaxController(new NicelyResynchronizingAjaxController());
        client.waitForBackgroundJavaScript(50000);
        client.setJavaScriptTimeout(50000);
        // client.setIgnoreOutsideContent(true);
        //  client.getOptions().setThrowExceptionOnScriptError(false);
        /*   */
        org.apache.commons.logging.impl.Jdk14Logger logger = (org.apache.commons.logging.impl.Jdk14Logger) LogFactory.getLog("com.gargoylesoftware.htmlunit");
        logger.getLogger().setLevel(Level.OFF);
        HtmlPage page = null;
        try {
            page = client.getPage(url);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }//
        //   System.out.println(page.asXml());//http://oa.cecic.corp/names.nsf?Login
        //鐧诲綍http://portal.cecic.corp/
        HtmlInput ln = page.getElementByName("Username");
        HtmlInput pwd = page.getElementByName("Password");
        //HtmlInput red = page.getElementByName("RedirectTo");
        List<DomElement> inputsList = page.getElementsByTagName("td");
        DomElement btn = null;
        for (DomElement n : inputsList) {
            if (n.hasAttribute("onclick")) {
                //System.out.println(n.asXml());
                //System.out.println(n.getOnClickAttribute());
                btn = n;
                break;
            }
        }


        ln.setAttribute("value", username);
        pwd.setAttribute("value", password);
        try {
            btn.click();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            Thread.sleep(5000);
            ///System.out.println( client.waitForBackgroundJavaScript(30000));
            try {
//				HtmlPage page1 = client.getPage("http://jtzb.cecic.corp/jtzb/dzbx.nsf/myview?OpenForm&view=vwpublicedold2014");
//				//
//				System.out.println(page1.asText());
//				| IOException
            } catch (FailingHttpStatusCodeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }//
            client.waitForBackgroundJavaScript(10000);

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // client.getCookieManager();
        return client;
    }

    public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
        new Login();
        WebClient client = new WebClient(BrowserVersion.CHROME);
        client.getOptions().setJavaScriptEnabled(true);    //榛樿鎵цjs锛屽鏋滀笉鎵цjs锛屽垯鍙兘浼氱櫥褰曞け璐ワ紝鍥犱负鐢ㄦ埛鍚嶅瘑鐮佹闇?瑕乯s鏉ョ粯鍒躲??
        client.getOptions().setCssEnabled(false);
        client.getOptions().setThrowExceptionOnScriptError(false); //js杩愯閿欒鏃讹紝鏄惁鎶涘嚭寮傚父
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getOptions().setTimeout(20000);
        client.setAjaxController(new NicelyResynchronizingAjaxController());
        client.waitForBackgroundJavaScript(50000);
        client.setJavaScriptTimeout(50000);

        /**/
        org.apache.commons.logging.impl.Jdk14Logger logger = (org.apache.commons.logging.impl.Jdk14Logger) LogFactory.getLog("com.gargoylesoftware.htmlunit");
        logger.getLogger().setLevel(Level.OFF);
        HtmlPage p = client.getPage("http://10.13.56.36:8090/Search?curpage=2&keyword=%E8%89%B2%E6%83%85");
        System.out.println(p.asText());
//   	CloseableHttpClient httpclient = Login.jnoaMultiThread("22",10, DBUtil.select("website", userParam, Integer.parseInt("22"))[0][0], 
//			DBUtil.select("website", passwordParam, Integer.parseInt("22"))[0][0], 
//			DBUtil.select("website", loginUrlParam, Integer.parseInt("22"))[0][0]);
        //CloseableHttpClient httpclien = Login.jnoaMultiThread("22",10, "lichenyu", "1984215", "http://jtzb.cecic.corp/names.nsf?Login");
        //jnoa("23", "zhangsan04", "password", "http://oa.cecic.corp/names.nsf?Login");
        //  HttpContext context=HttpClientContext.create();
        // HttpPost httpget = new HttpPost("http://jtzb.cecic.corp/jtzb/dzbx.nsf/3c33479f138922674825788e003be426/9e2dc6217d63d465482580e9002d90c7?opendocument");

//		System.out.println(DBUtil.select("website", pwdParam, Integer.parseInt("20"))[0][0]+DBUtil.select("website", loginUrlParam, Integer.parseInt("20"))[0][0]);
//    	loginOA(DBUtil.select("website", userParam, Integer.parseInt("20"))[0][0], 
//				DBUtil.select("website", pwdParam, Integer.parseInt("20"))[0][0], 
//				DBUtil.select("website", loginUrlParam, Integer.parseInt("20"))[0][0]);
//    	//Login.jnoa("5","zhangsan02", "password","http://oa.cecic.corp/names.nsf?Login");
    }

    public static String getCookie_chrome(ExtraConf extraConf) throws Exception {
        if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
            String rootPath = System.getProperty("java.class.path");
            int lastIndex = rootPath.lastIndexOf(File.separator);
            rootPath = rootPath.substring(0, lastIndex);
            System.setProperty("webdriver.chrome.driver", rootPath + File.separator + "chromedriver.exe");
            // out.println(rootPath);
        }
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

        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);
        int num = 0;
        boolean flag = false;
        if (extraConf.getLoginUrl() != null && extraConf.getLoginUrl().length() > 0) {
            while (true) {
                driver.get(extraConf.getLoginUrl());

                WebElement txtUserName = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id(extraConf.getUserNameXpath())));
                // out.println(txtUserName);
                txtUserName.sendKeys(extraConf.getUserName());

                WebElement txtPassword = (new WebDriverWait(driver, 10))
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
                    flag = true;
                    break;
                }
            }
        }
        Set<org.openqa.selenium.Cookie> cookies;
        if (flag) {
            System.out.println("LOGIN error");
            throw new Exception();

        } else cookies = driver.manage().getCookies();
        String cookie = "";
        //System.out.println(cookies.toString());
        for (org.openqa.selenium.Cookie s : cookies) {
            cookie += s.toString().substring(0, s.toString().indexOf(";") + 1);
        }

        driver.quit();
        return cookie;
    }
    public static String getCookie(ExtraConf extraConf) throws Exception {
        if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {

            /*获取所有的类的路径，也包括所有的jar包的路径*/
            /*下面的rootPath在jar包运行时使用*/
            String rootPath = System.getProperty("java.class.path");
            int lastIndex = rootPath.lastIndexOf(File.separator);
            rootPath = rootPath.substring(0, lastIndex);
            /*打包之前注释掉此句。*/
//            rootPath = "D:\\360MoveData\\Users\\Thinkpad\\Documents\\WeChat Files\\achang658\\FileStorage\\File\\2019-05\\crawler_Structured";
            System.out.println("rootPath is "+rootPath);

            //System.setProperty("webdriver.firefox.bin", rootPath+"\\firefox.exe");//"D:\\program\\firefox\\firefox.exe");
            System.setProperty("webdriver.gecko.driver", rootPath + "\\geckodriver.exe");//D:\\workspace\\crawler_Structured
            //	System.setProperty("webdriver.gecko.driver", "D:\\workspace\\crawler_Structured\\geckodriver.exe");
            //	System.setProperty("webdriver.firefox.bin", "D:\\program\\firefox\\firefox.exe");
//			
        }

        /*对火狐浏览器进行配置*/
        FirefoxOptions option = new FirefoxOptions();
        option.addArguments("--headless");
        option.addArguments("--disable-gpu");
        option.addArguments("window-size=1024,768");
        option.addArguments("--no-sandbox");
        option.addArguments("–-disable-plugins");
        option.addArguments("–-disk-cache-size=80");
        option.addArguments("--disable-cache");
        WebDriver driver = new FirefoxDriver(option);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(7, TimeUnit.SECONDS);


        /*进行模拟登陆操作，并拿到登陆之后的cookie。*/
        int num = 0;
        boolean flag = false;
        if (extraConf.getLoginUrl() != null && extraConf.getLoginUrl().length() > 0) {
            while (true) {
                driver.get(extraConf.getLoginUrl());
                WebElement txtUserName = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id(extraConf.getUserNameXpath())));
//                out.println(txtUserName);
                txtUserName.sendKeys(extraConf.getUserName());

                WebElement txtPassword = (new WebDriverWait(driver, 10))
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
                    flag = true;
                    break;
                }
            }
        }
        Set<org.openqa.selenium.Cookie> cookies;
        if (flag) {
            System.out.println("LOGIN error");
            throw new Exception();

        } else cookies = driver.manage().getCookies();
        String cookie = "";

        for (org.openqa.selenium.Cookie s : cookies) {
            cookie += s.toString().substring(0, s.toString().indexOf(";") + 1);
        }
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
//		Runtime rt = Runtime.getRuntime();
//		 String[] cmd = { "sh", "-c",  "ps aux | grep \"Xvfb :"+port+"\" |  awk '{print $2}' | xargs kill -9" };
//		 try {
//			rt.exec(cmd);
//		} catch (IOException e) {
//			
//			System.err.append("kill xvfb err");
//		}
        return cookie;
    }

}
