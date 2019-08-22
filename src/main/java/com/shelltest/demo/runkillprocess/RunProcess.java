package com.shelltest.demo.runkillprocess;

import com.shelltest.demo.utils.Kernel32;
import com.sun.jna.Platform;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

/**
 * 使用runtime调用第三方程序
 *
 * @author 18671
 */
@RestController
public class RunProcess {

    /**
     * 执行脚本并返回pid值
     *
     * @param cmd 执行脚本
     * @return -1执行失败 <0 为pid值
     */
    @GetMapping("getPid")
    public long getPid(String cmd) {

        //执行失败返回-1
        long pid = -1;
        try {

            Process child = Runtime.getRuntime().exec(cmd);

            pid = getProcessPID(child);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pid;
    }

    /**
     * 获取执行脚本的pid
     *
     * @param child jna返回参数
     * @return -1执行失败 <0 为pid值
     */
    private static long getProcessPID(Process child) {
        long pid = -1;
        Field field;
        try {
            if (Platform.isWindows()) {
                try {
                    field = child.getClass().getDeclaredField("handle");
                    field.setAccessible(true);
                    pid = Kernel32.INSTANCE.GetProcessId((Long) field.get(child));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (Platform.isLinux()) {
                try {
                    Class<?> clazz = Class.forName("java.lang.UNIXProcess");
                    field = clazz.getDeclaredField("pid");
                    field.setAccessible(true);
                    pid = (Integer) field.get(child);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pid;
    }

    /**
     * 根据cmd或pid查询进程是否存在
     *
     * @param commandOrPid cmd或pid值
     * @return true 运行中， false 运行失败
     */
    @GetMapping("isExistPid")
    public boolean isExistPid(String commandOrPid) {

        BufferedReader reader = null;
        try {
            //显示所有进程
            Process process = Runtime.getRuntime().exec("ps -ef");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.contains(commandOrPid)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
