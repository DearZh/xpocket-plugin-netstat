package com.perfma.xlab.xpocket.plugin.netstat;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

/**
 * @author Arnold.zhao
 * @version RuntimeUtils.java, v 0.1 2021-08-23 10:58 Arnold.zhao Exp $$
 */
public class RuntimeUtils {


    public static String execCmdWithResult(String command) {
        try {
            //接收正常结果流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //接收异常结果流
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            CommandLine commandline = CommandLine.parse(command);
            DefaultExecutor exec = new DefaultExecutor();
            exec.setExitValues(null);
            //设置10s超时
            ExecuteWatchdog watchdog = new ExecuteWatchdog(10000);
            exec.setWatchdog(watchdog);
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
            exec.setStreamHandler(streamHandler);
            int result = exec.execute(commandline);
            //不同操作系统注意编码，否则结果乱码
            String out = outputStream.toString(Charset.defaultCharset().toString());
            String error = errorStream.toString(Charset.defaultCharset().toString());
            return out + error;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}
