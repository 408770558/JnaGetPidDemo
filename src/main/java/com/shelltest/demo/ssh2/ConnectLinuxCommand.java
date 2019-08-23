package com.shelltest.demo.ssh2;

import com.shelltest.demo.utils.PidUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bowen.Li@onerway.com
 * @date 2019/8/22
 * 说明：
 */
@Log4j2
@RestController
public class ConnectLinuxCommand {


    /**
     * http://localhost:8080/ssh2?ip=192.168.91.3&userName=root&password=admin123&cmd=cd%20/opt&cmd=java%20-jar%20demo-0.0.1-SNAPSHOT.jar
     *
     * @throws
     * @Title: login
     * @Description: 用户名密码方式  远程登录linux服务器
     * @return: Boolean
     */
    @GetMapping("ssh2")
    public int loginAndExecute(String ip, String userName, String password, String... cmd) {

        PidUtils pidUtils = new PidUtils();
        return pidUtils.getPid(ip, userName, password, cmd);
    }

    /**
     * localhost:8080/ssh2?ip=192.168.91.3&userName=root&password=admin123&cmd=vi /opt/1.txt
     *
     * @throws
     * @Title: login
     * @Description: 用户名密码方式  远程登录linux服务器
     * @return: Boolean
     */
    @GetMapping("ssh3")
    public String query(String ip, String userName, String password, int pid) {

        PidUtils pidUtils = new PidUtils();

        List<Integer> list = new ArrayList<>();
        list.add(pid);
        list.add(7070);
        list.add(8080);
        list.add(9090);
        list.add(1010);
        return pidUtils.isExistPid(ip, userName, password, list).toString();
    }
}
