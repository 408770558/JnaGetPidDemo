package com.shelltest.demo.getPid;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * @author Bowen.Li@onerway.com
 * @date 2019/8/21
 * 说明：
 */
@Log4j2
@RestController
public class getPid {

    /**
     * 获取Linux进程的PID
     *
     * @param commandOrPid
     * @return
     */
    @GetMapping("getPid")
    public boolean getPid(String commandOrPid) {

        log.info("commandOrPid ------> " + commandOrPid);

        BufferedReader reader = null;
        try {
            //显示所有进程
            Process process = Runtime.getRuntime().exec("ps -ef");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String[] strs = null;
            String line;
            while ((line = reader.readLine()) != null) {

                log.info("进程信息 -----> " + line);

                if (line.contains(commandOrPid)) {
                    log.info("相关信息 ---------------> " + commandOrPid);
                    strs = line.split("\\s+");
                }
            }

            log.info("strs ---------------> " + Arrays.toString(strs));

            return strs != null;

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
