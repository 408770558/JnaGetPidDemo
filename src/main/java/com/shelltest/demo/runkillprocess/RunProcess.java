package com.shelltest.demo.runkillprocess;

import com.shelltest.demo.utils.RunUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * 使用runtime调用第三方程序
 *
 * @author 18671
 */
@RestController
@Log4j2
public class RunProcess {

    /**
     * http://localhost:8080/sleepPid?command=vi%20/opt/1.txt
     * @param command
     * @return
     */
    @GetMapping("sleepPid")
    public long sleepPid(String command) {
        // 执行无阻塞调用
        long pid = runJavaProcess(command);

        log.info("-----------" + pid);

        //30秒后杀掉进程
        try {
            Thread.sleep(1000 * 30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopProcess(pid);

        return pid;
    }

    private static long runJavaProcess(String command) {
        //String cmd = "java -cp D:\\example.JavaProcess1";
        //String cmd = "\"D:\\notepad\\Notepad++\\notepad++.exe\" D:\\1.txt";
        //String cmd = "vim /opt/sssss/xx.xx";

        RunUtils.run(command);

        // 获取pid
        return (long) RunUtils.messageMap.get("pid");
    }

    private static void stopProcess(long pid) {
        // 拼接命令
        //String cmd = "taskkill /PID " + pid + " /F";
        String cmd = "kiil -9" + pid;
        // 运行命令
        String[] returnContent = RunUtils.run2(cmd);

        System.out.println(Arrays.toString(returnContent));
    }
}
