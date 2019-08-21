package com.shelltest.demo.runkillprocess;

import com.shelltest.demo.utils.RunUtils;

import java.util.Arrays;

/**
 * 使用runtime调用第三方程序
 *
 * @author 18671
 */
public class RunProcess {
    public static void main(String[] args) {
        // 执行无阻塞调用
        long pid = runJavaProcess();

        System.out.println(pid);

        //十秒后杀掉进程
        try {
            Thread.sleep(1000 * 60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopProcess(pid);
    }

    private static long runJavaProcess() {
        //String cmd = "java -cp D:\\example.JavaProcess1";
        String cmd = "\"D:\\notepad\\Notepad++\\notepad++.exe\" D:\\1.txt";

        RunUtils.run(cmd);

        // 获取pid
        return (long) RunUtils.messageMap.get("pid");
    }

    private static void stopProcess(long pid) {
        // 拼接命令
        String cmd = "taskkill /PID " + pid + " /F";
        // 运行命令
        String[] returnContent = RunUtils.run2(cmd);

        System.out.println(Arrays.toString(returnContent));
    }
}
