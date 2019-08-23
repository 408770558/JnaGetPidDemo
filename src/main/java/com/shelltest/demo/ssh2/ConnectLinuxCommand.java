package com.shelltest.demo.ssh2;

import com.shelltest.demo.utils.PidUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Bowen.Li@onerway.com
 * @date 2019/8/22
 * 说明：
 */
@Log4j2
@RestController
public class ConnectLinuxCommand {


    /**
     * localhost:8080/ssh2?ip=192.168.91.3&userName=root&password=admin123&cmd=vi /opt/1.txt
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
    public boolean query(String ip, String userName, String password, int pid) {

        PidUtils pidUtils = new PidUtils();

        return pidUtils.isExistPid(ip, userName, password, pid);
    }
}
