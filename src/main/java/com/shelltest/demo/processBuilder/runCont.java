package com.shelltest.demo.processBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Bowen.Li@onerway.com
 * @date 2019/8/22
 * 说明：
 */
public class runCont {
    public void run() {
        try {
            String cmd = "D:\\Java\\jdk1.7.0_79\\bin\\java.exe";
            String version = "-version";
            ProcessBuilder pb = new ProcessBuilder();
            Process ps = pb.start();
            InputStream is = ps.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));


            String line;
            while ((line = br.readLine()) != null) {
                System.err.println(line);
            }


            InputStream is1 = ps.getInputStream();
            BufferedReader br1 = new BufferedReader(new InputStreamReader(is1));


            String line1;
            while ((line1 = br1.readLine()) != null) {
                System.out.println(line1);
            }


            int exitCode = ps.waitFor();
            System.out.println(exitCode);
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }
}
