package com.shelltest.demo.utils;

import com.shelltest.demo.constant.SpiderConstant;
import com.sun.jna.Platform;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 18671
 */
public class RunUtils {
    public static Map<String, Object> messageMap = new HashMap<>();

    /**
     * 运行代码有两种方式，一种是通过反射动态运行，一种是另起进程，这里选择另起进程
     */
    public static void run(String cmd) {
        //执行失败返回-1
        long pid = -1;
        try {

            Process child = Runtime.getRuntime().exec(cmd);

            messageMap.put(SpiderConstant.SPIDER_KEYWORD_PROCESSMAP_PROCESS, child);

            pid = getProcessPID(child);

            messageMap.put(SpiderConstant.SPIDER_KEYWORD_PROCESSMAP_PID, pid);

            // 正在运行
            messageMap.put(SpiderConstant.SPIDER_KEYWORD_PROCESSMAP_RUNSTATUS, 0);
        } catch (Exception e) {
            e.printStackTrace();

            messageMap.put(SpiderConstant.SPIDER_KEYWORD_PROCESSMAP_PID, pid);
        }
    }

    /**
     * 阻塞
     */
    public static String[] run2(String cmd) {
        String returnPrintContent;
        String returnErrorContent;
        String[] returnContent = new String[2];
        try {

            Process child = Runtime.getRuntime().exec(cmd);

            // 正常输出流和异常输出流
            InputStream stdin = child.getInputStream();
            InputStream stderr = child.getErrorStream();

            // 启动线程
            ConsoleSimulator cs1 = new ConsoleSimulator(stdin, 0);
            ConsoleSimulator cs2 = new ConsoleSimulator(stderr, 1);

            Thread tIn = new Thread(cs1);
            Thread tErr = new Thread(cs2);

            tIn.start();
            tErr.start();

            tIn.join();
            tErr.join();

            returnPrintContent = cs1.getReturnPrintContent();
            returnErrorContent = cs2.getReturnErrorContent();
            // 处理中文乱码，需更改服务器端编码
            // 0是全部信息
            returnContent[0] = returnPrintContent;
            // 1是错误信息
            returnContent[1] = returnErrorContent;
            return returnContent;
        } catch (Exception e) {
            e.printStackTrace();
            return returnContent;
        }
    }

    /**
     * 获取pid
     *
     * @param child
     * @return
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
}
