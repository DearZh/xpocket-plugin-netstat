package com.perfma.xlab.xpocket.plugin.netstat;

import com.perfma.xlab.xpocket.spi.AbstractXPocketPlugin;
import com.perfma.xlab.xpocket.spi.context.SessionContext;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.spi.process.XPocketProcessAction;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 用于插件整体的声明周期管理和日志输出
 *
 * @author Arnold.zhao <arnold_zhao@126.com>
 * @version NetstatXPocketCommand.java, v 0.1 2021-08-23 9:58 Arnold.zhao Exp $$
 */
public class NetstatXPocketPlugin extends AbstractXPocketPlugin {

    private static final String LOGO = " _______  __________________________________________________________\n" +
            " \\      \\ \\_   _____/\\__    ___/   _____/\\__    ___/  _  \\__    ___/\n" +
            " /   |   \\ |    __)_   |    |  \\_____  \\   |    | /  /_\\  \\|    |   \n" +
            "/    |    \\|        \\  |    |  /        \\  |    |/    |    \\    |   \n" +
            "\\____|__  /_______  /  |____| /_______  /  |____|\\____|__  /____|   \n" +
            "        \\/        \\/                  \\/                 \\/         ";

    private Process netstatProc;

    public static final String lineSeparator = System.getProperty("line.separator");

    public void invoke(XPocketProcess process) {

        List<Boolean> booleans = new ArrayList<>();

        process.register(new XPocketProcessAction() {
            @Override
            public void userInput(String input) throws Throwable {
                // enter
            }

            @Override
            public void interrupt() throws Throwable {
                //ctrl +c
                booleans.add(true);
            }
        });
        process.output(" cmd：" + process.getCmd() + "  args：" + Arrays.toString(process.getArgs()));

        try {
            if (process.getArgs() != null && process.getArgs().length > 0) {
                //执行common exc
                String result = RuntimeUtils.execCmdWithResult(handleCmdStr(process.getCmd(),
                        process.getArgs()));
                process.output(result);
                process.end();
            }

            //兼容windows环境，直接执行netstat时采用Runtime exec持续的流式获取数据
            switch (process.getCmd()) {
                case "netstat":
                    run(process, booleans);
                    break;
                default:
                    netstatProc.getOutputStream().write(handleCmd(process.getCmd(),
                            process.getArgs()));
                    netstatProc.getOutputStream().flush();
                    break;
            }
        } catch (Exception e) {
            process.output(e.getMessage());
            process.end();
        }

    }


    public void run(XPocketProcess process, List<Boolean> booleans) {
        try {
            netstatProc = Runtime.getRuntime()
                    .exec(handleCmdStr(process.getCmd(),
                            process.getArgs()));
            InputStream instr = netstatProc.getInputStream();

            int ret_read = 0, index = 0;
            byte[] line = new byte[1024];

            for (; ; ) {
                ret_read = instr.read();
                if (ret_read == -1) {
                    break;
                }
                if (booleans.size() > 0) {
                    break;
                }
                switch (ret_read) {
                    case '\r':
                    case '\n':
                        String lineStr = new String(line, 0, index);
                        if (!lineStr.trim().equalsIgnoreCase(process.getCmd())) {
                            process.output(lineStr + lineSeparator);
                        }
                        index = 0;
                        break;
                    default:
                        line[index++] = (byte) ret_read;
                }
            }
            if (process != null) {
                process.end();
            }
        } catch (IOException e) {
            process.output("Exception while reading socket:" + e.getMessage());
            process.end();
        }
    }


    private byte[] handleCmd(String cmd, String[] args) {
        return handleCmdStr(cmd, args).getBytes();
    }

    private String handleCmdStr(String cmd, String[] args) {
        StringBuilder cmdStr = new StringBuilder(cmd);

        if (args != null) {
            for (String arg : args) {
                cmdStr.append(' ').append(arg);
            }
        }

        cmdStr.append("\n");

        return cmdStr.toString();
    }


    /**
     * 用于输出自定义LOGO
     *
     * @param process
     */
    @Override
    public void printLogo(XPocketProcess process) {
        process.output(LOGO);
    }

    /**
     * 插件会话被切出时被调用
     *
     * @param context
     */
    @Override
    public void switchOff(SessionContext context) {
        super.switchOff(context);
    }

    /**
     * 插件会话被切入时被调用
     *
     * @param context
     */
    @Override
    public void switchOn(SessionContext context) {
        super.switchOn(context);
    }

    /**
     * XPocket整体退出时被调用，用于清理插件本身使用的资源
     *
     * @throws Throwable
     */
    @Override
    public void destory() throws Throwable {
        super.destory();
    }

    /**
     * 插件首次被初始化时被调用
     *
     * @param process
     */
    @Override
    public void init(XPocketProcess process) {
        super.init(process);
    }


}
