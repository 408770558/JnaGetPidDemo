package com.shelltest.demo.utils;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bowen.Li@onerway.com
 * @date 2019/8/22
 * 说明：
 */
public class PidUtils {

    /**
     * 执行失败
     */
    private final int resultError = -1;

    /**
     * 执行成功
     */
    private final int resultOk = 0;

    /**
     * 进行中
     */
    private final int resultIn = -2;

    /**
     * 获取脚本执行的pid
     *
     * @param ip       服务器ip地址
     * @param userName 服务器账号
     * @param password 服务器密码
     * @param cmd      执行脚本，分段传入
     * @return -1脚本执行失败， 0脚本执行成功，<0服务器pid值
     */
    public int getPid(String ip, String userName, String password, String... cmd) {

        if (StringUtils.isEmpty(cmd)) {
            return resultError;
        }

        String lastCmd = null;
        StringBuilder cmds = new StringBuilder();
        for (int i = 0; i < cmd.length; i++) {
            cmds.append(cmd[i].trim());
            if (i < cmd.length - 1) {
                cmds.append(" ; ");
            }

            if (i == cmd.length - 1) {
                lastCmd = cmd[i].trim();
            }
        }

        if (StringUtils.isEmpty(lastCmd)) {
            return resultError;
        }

        Connection conn = setConnection(ip, userName, password);
        if (conn != null) {

            Session session;
            try {
                session = conn.openSession();

                //第一次执行脚本 -1执行失败， 0执行完成 -2进行中
                int result = runSession(session, cmds.toString());

                if (result <= resultOk && result > resultIn) {
                    return result;
                } else {

                    //打开查询连接
                    Session querySession = conn.openSession();
                    return runQuerySession(querySession, lastCmd);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn.close();
            }
        }

        return resultError;
    }

    /**
     * 查询进程是否存活
     *
     * @param ip       服务器ip
     * @param userName 服务器账号
     * @param password 服务器密码
     * @param pids     pid进程
     * @return true进程运行中， false未查询到进程, 空集合查询失败
     */
    public List<Map<Integer, Boolean>> isExistPid(String ip, String userName, String password, List<Integer> pids) {
        Connection conn = setConnection(ip, userName, password);
        if (conn != null) {
            try {
                //打开查询连接
                Session querySession = conn.openSession();
                return runQuerySession(querySession, pids);
            } catch (Exception e) {
                System.out.println("查询失败");
            } finally {
                conn.close();
            }
        }
        return new ArrayList<>();
    }


    private Connection setConnection(String ip, String userName, String password) {
        boolean flag = false;
        Connection conn;

        try {
            conn = new Connection(ip);
            conn.connect();// 连接

            //判断身份是否已经认证
            if (!conn.isAuthenticationComplete()) {

                //加锁，防止多线程调用时线程间判断不一致，导致出现重复认证
                synchronized (this) {
                    if (!conn.isAuthenticationComplete()) {
                        //进行身份认证
                        flag = conn.authenticateWithPassword(userName, password);
                    }
                }
            }

            if (flag) {
                return conn;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("认证失败");
        }
        return null;
    }

    private int runSession(Session session, String cmds) {

        int result = resultError;

        if (session == null) {
            System.out.println("认证失败");
            return result;
        }

        try {
            session.execCommand(cmds);

            session.waitForCondition(ChannelCondition.EXIT_STATUS, 500L);
            try {
                //执行中session为null
                int ret = session.getExitStatus();
                if (ret == resultOk) {
                    return ret;
                }
            } catch (Exception e) {
                System.out.println("脚本正在进行中，请稍后查询pid");
                result = -2;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("执行脚本过程出现异常");
        } finally {
            session.close();
        }
        return result;
    }

    private int runQuerySession(Session session, String lastCmd) {
        List<String> list = getConnReturn(session);
        return initCheckPid(list, lastCmd);
    }

    private List<Map<Integer, Boolean>> runQuerySession(Session session, List<Integer> pids) {
        List<String> list = getConnReturn(session);
        return initCheckPid(list, pids);
    }

    private List<String> getConnReturn(Session session) {

        List<String> list = new ArrayList<>();
        if (session == null) {
            System.out.println("认证失败");
            return list;
        }

        InputStream stdout = null;

        try {
            session.execCommand("ps -eo pid,cmd");

            //获取返回输出
            stdout = new StreamGobbler(session.getStdout());
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));

            String line;
            while ((line = stdoutReader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (stdout != null) {
                    stdout.close();
                }
            } catch (IOException e) {
                System.out.println("返回信息关闭失败");
                e.printStackTrace();
            }
            session.close();
        }

        if (list.size() > 0) {
            list.remove(0);
        }
        return list;
    }

    private int initCheckPid(List<String> list, String lastCmd) {
        for (String s : list) {
            String str = trimInnerSpaceStr(s);
            int i = str.indexOf(" ");

            String comm = str.substring(i);
            if (comm.trim().equals(lastCmd)) {
                return Integer.valueOf(str.substring(0, i));
            }
        }
        return resultError;
    }

    private List<Map<Integer, Boolean>> initCheckPid(List<String> list, List<Integer> pids) {

        if (CollectionUtils.isEmpty(pids)) {
            return new ArrayList<>();
        }

        List<Map<Integer, Boolean>> resultList = new ArrayList<>(pids.size());
        for (Integer pid : pids) {

            Map<Integer, Boolean> resultMap = new HashMap<>(1);
            //默认不存在
            resultMap.put(pid, false);

            for (String s : list) {
                String str = trimInnerSpaceStr(s);
                int i = str.indexOf(" ");

                //系统pid
                Integer sysPid = Integer.valueOf(str.substring(0, i).trim());
                if (sysPid.equals(pid)) {
                    //查询到修改为成功
                    resultMap.put(pid, true);
                }
            }

            resultList.add(resultMap);
        }
        return resultList;
    }

    /**
     * 去掉字符串前后的空间，中间的空格保留
     *
     * @param str 字符串
     * @return 返回左右两边清除空格的字符     
     */
    private static String trimInnerSpaceStr(String str) {
        String trim = " ";
        str = str.trim();
        while (str.startsWith(trim)) {
            str = str.substring(1).trim();
        }
        while (str.endsWith(trim)) {
            str = str.substring(0, str.length() - 1).trim();
        }
        return str;
    }
}
