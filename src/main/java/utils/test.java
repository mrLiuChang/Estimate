package utils;

import basic.Walker;
import crawler_S.DBUtil;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import crawler_S.DBUtil;


public class test {

    public static void processTest(String estiIdStr) throws IOException {

        Walker walker = Walker.initWalker(estiIdStr);
        /*
         * 使用walker.startWalk()函数开始进行随机游走。将先前的主函数的逻辑写进startWalk函数中
         * */
        walker.startWalk(estiIdStr);

        walker.PrintEstiRes();

    }

    //估测模块jar包的主运行逻辑。
    public static void main(String[] args) throws IOException {
        System.out.println(args[0] + " " + args[1] + " " + args[2]);
        String dbUrl = args[0];
        String dbUser = args[1];
        String dbPass = args[2];
        DBUtil.config(dbUrl, dbUser, dbPass);

        // 估测进程运行之前，向数据库写入进程号 pid。
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        System.out.println("pid is "+pid);
        String estiId = args[3];
        DBUtil.update("estimate", new String[]{"pid"}, new String[]{pid}, new String[]{"estiId"}, new String[]{estiId});

        processTest(estiId);

        //估测完成之后，更新status
        DBUtil.update("estimate", new String[]{"status"}, new String[]{"stop"}, new String[]{"estiId"}, new String[]{estiId});

    }

}
