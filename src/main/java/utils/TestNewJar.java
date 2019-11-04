package utils;

import crawler_S.DBUtil;
import java.io.IOException;

public class TestNewJar {


    public static void main(String[] args) throws IOException {

        String estiId = "149";
        String mysqlURL = "jdbc:mysql://localhost:3306/webcrawler?characterEncoding=UTF-8&useSSL=false&useAffectedRows=true&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String mysqlUserName = "root";
        String mysqlPassword = "2333";

        String jarPath = "E:\\0\\dc-web-1\\TestJar1\\target\\import_tool-jar-with-dependencies.jar";
        System.out.println("jarPath is " + jarPath);
        ProcessBuilder builder = new ProcessBuilder("java", "-jar", jarPath, estiId, mysqlURL, mysqlUserName, mysqlPassword);
        Process p = builder.start();
        try {
            Thread.sleep(5000l);
        } catch (InterruptedException e) {
        }
        if (p.isAlive()) {
            System.out.println("估测任务成功启动");

        } else {
            System.out.println("估测任务启动失败，请重新检查参数配置是正确，或查看输出日志进行问题定位");
        }

    }
    private static  String stop(String estiId) {
        String[][] ans = DBUtil.select("estimate", new String[]{"status"},new String[]{"estiId"}, new String[]{estiId} );
        if (ans.length == 0) {
            return "estimate id 所对应的网站不存在";
        }
        if (ans[0][0].equals("stop")) {
            return "该估测任务正处于未启动状态，无权进行暂停操作";
        }
        if (ans[0][0].equals("")||ans[0][0].isEmpty()){
            return "估测任务状态不明，无法进行停止操作";
        }

        String pidStr= DBUtil.select("estimate", new String[]{"pid"},new String[]{"estiId"}, new String[]{estiId})[0][0];
        System.out.println("pidStr is "+pidStr);
        long pid = Long.parseLong(pidStr);
        System.out.println("try to kill "+pid);

        Runtime rt = Runtime.getRuntime();
        if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
            try {
                rt.exec("taskkill /pid " + pid+" -f");
                setStatus("stop",estiId);
            } catch (IOException ex) {
            }
        } else {
            try {
                rt.exec("kill " + pid);
                setStatus("stop",estiId);
            } catch (IOException ex) {
            }
        }
        try {
            Thread.sleep(2000l);
        } catch (InterruptedException ex) {
        }

        ans = DBUtil.select("estimate", new String[]{"status"},new String[]{"estiId"}, new String[]{estiId} );
        if (ans[0][0].equals("stop")) {
            return "估测任务成功暂停";
        } else {
            return "估测任务暂停异常，请稍后重试，或查看输出日志进行问题定位";
        }
    }
    private static boolean setStatus(String status,String estiId){
        boolean flag=DBUtil.update("estimate", new String[]{"status"}, new String[]{status}, new String[]{"estiId"}, new String[]{estiId});
        return flag;
    }
}