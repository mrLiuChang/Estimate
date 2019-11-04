package crawler_S;

import opt.DataProcess;
import opt.RAMMD5Dedutor;
import opt.TableProcess;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import model.ExtraConf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Downloader {
    //	private static String charset = "utf-8";
    private static String charset = "utf-8";
    private static String webId = "";
    private int connectionRequestTimeout = 5000;
    private int socketTimeout = 5000;
    private int connectTimeout = 5000;
    private static DataProcess dp = null;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:sssss");
    private static RAMMD5Dedutor dedu = null;
    private static String url = null;
    static boolean threadflag = false;
    static boolean pic_downlod = false;
    private static int totalResNum=0;
    private static List<String> userAgent = new ArrayList<String>() {
        {
            add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
            add("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");
            add("Mozilla/5.0(WindowsNT6.1;rv:31.0)Gecko/20100101Firefox/31.0");
            add("Mozilla");
            add("HTTP Banner Detection(security.ipip.net)");
        }
    };

    //constructors
    public Downloader(String webId, ExtraConf extraConf, RAMMD5Dedutor dedu) {
        this.webId = webId;
        this.dedu = dedu;
        this.dp = new DataProcess(webId + "");
        charset = extraConf.getCharset();
        //Config_test.getConfigValue(webId,"website","crawler", "charset");

        connectionRequestTimeout = (int) extraConf.getTimeout() * 10;
        //Integer.parseInt(Config_test.getConfigValue(webId,"website","crawler", "timeout"));
        socketTimeout = connectionRequestTimeout;
        //Integer.parseInt(Config_test.getConfigValue(webId,"website","crawler", "timeout"));
        connectTimeout = connectionRequestTimeout;
        //Integer.parseInt(Config_test.getConfigValue(webId,"website","crawler", "timeout"));
        url = DBUtil.select("website", new String[]{"indexUrl"}, Integer.parseInt(webId))[0][0];
        url = url.substring(0, url.indexOf(":") + 3);
    }

    static String getUserAgent() {
        return userAgent.get((int) (System.currentTimeMillis() % userAgent.size()));
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        Downloader.charset = charset;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }



    public int[] htmlDownload_GET(List<String> urls, ExtraConf extraConf, String saveFolder, String subpagePath) {
        int threadNum = extraConf.getThreadNum();
        String loginUrl = extraConf.getLoginUrl();
        CloseableHttpClient httpclient = null;
        ThreadPoolExecutor exePool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNum);

        /*
        * 注意，对于登陆和不用登陆的网站
        * 有两种不同的client对象的赋值方法
        * */
        if (loginUrl != null && loginUrl.length() > 0) {
            httpclient = Login.jnoaMultiThread(webId, threadNum, extraConf.getUserName(),
                    extraConf.getPassword(),
                    loginUrl);
        } else {
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(threadNum + 10);
            cm.setDefaultMaxPerRoute(threadNum + 10);
            // Increase max connections for localhost:80 to 50
            //HttpHost localhost = new HttpHost("locahost", 80);
            //cm.setMaxPerRoute(new HttpRoute(localhost), 50);
            httpclient = HttpClients
                    .custom()
                    .setRetryHandler(new StandardHttpRequestRetryHandler())
                    .setConnectionManager(cm)
                    .build();
        }
        int[] StatusCode = new int[urls.size()];
        if (saveFolder != null && !saveFolder.isEmpty()) {
            File folder = new File(saveFolder);
            if (!folder.exists())
                folder.mkdirs();
        }
        try {
            int len = urls.size();
            int count = 0;
            while (count < len) {
                if (exePool.getActiveCount() < threadNum) {
                    String url = urls.get(count);
                    if (!url.startsWith("http://") || url.contains("<")) {
                        count++;
                        continue;
                    }
                    HttpGet httpget = null;
                    try {
                        httpget = new HttpGet(url);
                    } catch (Exception e) {
                        count++;
                        continue;
                    }
                    RequestConfig requestConfig = RequestConfig.custom()
                            .setConnectionRequestTimeout(connectionRequestTimeout)
                            .setSocketTimeout(socketTimeout)
                            .setConnectTimeout(connectTimeout)
                            .build();
                    httpget.setConfig(requestConfig);
                    httpget.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
                    Thread t;
                    String[] con = {"webId", "type"};
                    String[] conV = {webId + "", "formula"};

                    /*tableProces的功能是什么？*/
                    TableProcess tp = new TableProcess(con, conV);
                    if (
                            url.contains(".") &&
                            (url.substring(url.lastIndexOf("."), url.length()).equals(".pdf") ||
                            url.substring(url.lastIndexOf("."), url.length()).equals(".doc") ||
                            url.substring(url.lastIndexOf("."), url.length()).equals(".docx") ||
                            url.substring(url.lastIndexOf("."), url.length()).equals(".xls") ||
                            url.substring(url.lastIndexOf("."), url.length()).equals(".xlsx") ||
                            url.substring(url.lastIndexOf("."), url.length()).equals(".html"))) {
                        t = new GetHtmlThread(httpclient, httpget, count, saveFolder + "/" + count + url.substring(url.lastIndexOf("."), url.length()), subpagePath, StatusCode, tp);
                    } else {
                        t = new GetHtmlThread(httpclient, httpget, count, saveFolder + "/" + count + ".html", subpagePath, StatusCode, tp);
                    }
                    exePool.submit(t);
                    count++;
                }
            }

            /*
            * 此处定义了线程池中每个线程的最大等待时间
            * */
            exePool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return StatusCode;
    }

    public int[] htmlDownload_POST(String url, List<String> jsons, ExtraConf extraConf, String cookie, String saveFolder) {

        int threadNum = extraConf.getThreadNum();
        CloseableHttpClient httpclient = null;
        ThreadPoolExecutor exePool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNum);
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        //+20
        cm.setMaxTotal(threadNum + 20);
        cm.setDefaultMaxPerRoute(threadNum + 20);

        HttpHost localhost = new HttpHost(url, 80);
        cm.setMaxPerRoute(new HttpRoute(localhost), 50);
        httpclient = HttpClients
                .custom()
                .setRetryHandler(new StandardHttpRequestRetryHandler())
                .setConnectionManager(cm)
                .build();
        int[] StatusCode = new int[jsons.size()];
        try {
            int len = jsons.size();
            int count = 0;
            System.out.println(df.format(new Date()) + " - start ");




            while (count < len) {

                if (exePool.getActiveCount() < threadNum) {
                    String json = jsons.get(count);
                    HttpPost post = new HttpPost(url);
                    try {
                        StringEntity s = new StringEntity(json);
                        s.setContentEncoding("UTF-8");
                        s.setContentType("application/json");
                        post.setEntity(s);
                        post.setHeader("Cookie", cookie);
                        post.setHeader("user-agent", getUserAgent());
                    } catch (Exception e) {
                        count++;
                        continue;

                    }

                    /*
                    静态内部类的对象在此处构造，由于在静态方法中创建对象GetHtmlThread_Post，因此GetHtmlThread_Post类
                    声明为这个类的静态内部类。
                    * */
                    Thread t = new GetHtmlThread_Post(httpclient, post, count, StatusCode, saveFolder + "/" + System.currentTimeMillis() + ".html");
                    exePool.submit(t);
                    count++;
                }
            }
            exePool.shutdown();
            threadflag = true;
            int num = 0;
            while (true) {
                try {
                    if (exePool.awaitTermination(extraConf.getThreadNum(), TimeUnit.SECONDS))
                        break;
                    num++;
                    if (num > 6)
                        break;
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println(df.format(new Date()) + " - end ");

        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return StatusCode;
    }

    /**
     * A thread that performs a GET.
     * save the contents from a web page
     */
    static class GetHtmlThread extends Thread {

        private final CloseableHttpClient httpClient;
        private final HttpContext context;
        private final HttpGet httpget;
        private final int id;
        private final String savePath;
        private final int[] StatusCode;
        private final String subpagePath;
        private final TableProcess tableObject;

        public GetHtmlThread(CloseableHttpClient httpClient, HttpGet httpget, int id, String savePath, String subpagePath, int[] StatusCode, TableProcess tableObject) {
            this.httpClient = httpClient;
            this.context = HttpClientContext.create();
            this.httpget = httpget;
            this.id = id;
            this.savePath = savePath;
            this.StatusCode = StatusCode;
            this.subpagePath = subpagePath;
            this.tableObject = tableObject;
        }

        /**
         * Executes the GetMethod and save the response.
         * 注意每一次的run，也就是每一个线程，用于下载一个页面。
         */
        @Override
        public synchronized void run() {
            HttpEntity entity = null;
            InputStream inputStream = null;
            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(httpget, context);
                //   System.out.println(id + " - get executed");
                // get the response body as an array of bytes

                StatusCode[id] = response.getStatusLine().getStatusCode();
                if (StatusCode[id] < 200 || StatusCode[id] >= 300) {
                    return;
                }
                entity = response.getEntity();

                if (entity != null) {
                    byte[] bytes = EntityUtils.toByteArray(entity);
                    String html = "";
                    html = new String(bytes, charset);
                    /*
                    table_process的功能是什么
                    * */
                    int sizeChange=Table_process(tableObject, html, "");
                    Scheduler.inc(sizeChange);
//                    DBUtil.update("estimate",new String[]{"result"},new String[]{Scheduler.ItemSize+""}, new String[]{"estiId"}, new String[]{webId});
//                    DBUtil.update("estimate",new String[]{"rateBar"},new String[]{"waiting"},new String[]{"estiId"}, new String[]{webId});
                    System.out.println("totalResNum is "+Scheduler.ItemSize);
                    EntityUtils.consumeQuietly(entity);
                }

            } catch (Exception e) {
                System.out.println(id + " - error: " + e);
                //e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (response != null) {
                    try {
                        response.close();

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * A thread that performs a Post.
     * save the contents from a web page
     */
    static class GetHtmlThread_Post extends Thread {

        private final CloseableHttpClient httpClient;
        private final HttpContext context;
        private final HttpPost httppost;
        private final int id;
        private final int[] StatusCode;
        private final String savePath;

        /*
         *
         * */
        public GetHtmlThread_Post(CloseableHttpClient httpClient, HttpPost httppost, int id, int[] StatusCode, String savePath) {
            this.httpClient = httpClient;
            this.context = HttpClientContext.create();
            this.httppost = httppost;
            this.id = id;
            this.StatusCode = StatusCode;
            this.savePath = savePath;

        }

        /**
         * Executes the GetMethod and save the response.
         */
        @Override
        public synchronized void run() {
            HttpEntity entity = null;

            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(httppost, context);
                StatusCode[id] = response.getStatusLine().getStatusCode();
                if (StatusCode[id] < 200 || StatusCode[id] >= 300) {
                    return;
                }
                entity = response.getEntity();
                if (entity != null) {
                    try {
                        String html = EntityUtils.toString(entity, charset);//new String(bytes,charset);
                        JsonProcess.json_process(Integer.parseInt(webId), html, dedu);
                    } catch (ConnectionClosedException e) {
                        StatusCode[id] = 500;
                        e.printStackTrace();
                    }
//                    EntityUtils.consume(entity);
                }

            } catch (Exception e) {
                System.out.println(id + " - error: " + e);
                e.printStackTrace();
            } finally {
                if (response != null) {
                    try {
                        response.close();

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }
    }



    private static int Table_process(TableProcess tableObject, String txt, String MD5Path) throws Exception {
        int changeSize = 0;

        ArrayList<String> data = new ArrayList<String>();
        if (tableObject != null) {
            data = tableObject.getAllTable(txt, url);

            changeSize += dp.Data_process(data, MD5Path, dedu);
        }

        return changeSize;

    }
}
