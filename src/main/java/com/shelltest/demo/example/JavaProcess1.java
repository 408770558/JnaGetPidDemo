package com.shelltest.demo.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class JavaProcess1 {

    // public static void main(String[] args) {
    //     for (int i = 0; i < 100; i++) {
    //
    //         System.out.println(i);
    //         appendFile("test:  " + i);
    //
    //         try {
    //             Thread.sleep(1000);
    //         } catch (InterruptedException e) {
    //             e.printStackTrace();
    //         }
    //     }
    // }

    /**
     * 追加文件
     */
    private static void appendFile(String content) {
        FileWriter fw;
        try {
            // 如果文件存在，则追加内容
            // 如果文件不存在，则创建文件
            File f = new File("/opt/1.txt");
            fw = new FileWriter(f, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(content);
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
