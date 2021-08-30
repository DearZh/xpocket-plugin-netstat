import com.perfma.xlab.xpocket.plugin.netstat.RuntimeUtils;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Arnold.zhao
 * @version Test.java, v 0.1 2021-08-23 9:58 Arnold.zhao Exp $$
 */
public class Test {
    private static final String DEFAULT_CHARSET = "UTF-8";

    public static void main(String[] args) {
        /*String cmd = "netstat";
        try {
            final String result = exeCommand(cmd);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        System.out.println(RuntimeUtils.execCmdWithResult("netstat -h"));
    }

    private static String execCmdWithResult() {
        try {
//            String command = "ping 127.0.0.1";
            String command = "netstat -h";
            //接收正常结果流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //接收异常结果流
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            CommandLine commandline = CommandLine.parse(command);
            DefaultExecutor exec = new DefaultExecutor();
            exec.setExitValues(null);
            //设置一分钟超时
            ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
            exec.setWatchdog(watchdog);
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
            exec.setStreamHandler(streamHandler);
            int result = exec.execute(commandline);
            //不同操作系统注意编码，否则结果乱码
            String out = outputStream.toString("GBK");
            String error = errorStream.toString("GBK");
            return out + error;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


    private static String exeCommand(String command) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ByteArrayOutputStream error = new ByteArrayOutputStream()) {
            int exitCode = exeCommand(command, out, error);
            if (exitCode != 0) {
//                throw new RuntimeException("命令运行失败!");
                System.out.println("命令运行失败" + exitCode);
            }
            return out.toString("GBK") + error.toString("GBK");
        }
    }

    public static int exeCommand(String command, ByteArrayOutputStream out, ByteArrayOutputStream error) throws ExecuteException, IOException {
        CommandLine commandLine = CommandLine.parse(command);
        PumpStreamHandler pumpStreamHandler = null;
        if (null == out) {
            pumpStreamHandler = new PumpStreamHandler();
        } else {
            pumpStreamHandler = new PumpStreamHandler(out, error);
        }
        // 设置超时时间为10秒
        ExecuteWatchdog watchdog = new ExecuteWatchdog(10000);
        DefaultExecutor executor = new DefaultExecutor();
        //setExitValues，具体影响是什么？不加这个null的设置会导致windows执行直接失败退出。
        //不加这个配置，在linux环境是可以执行成功的。
        executor.setExitValues(null);

        executor.setWatchdog(watchdog);
        executor.setStreamHandler(pumpStreamHandler);
//        return executor.execute(commandLine, System.getenv());
        return executor.execute(commandLine);
    }
}
