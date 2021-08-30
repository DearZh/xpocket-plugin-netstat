package com.perfma.xlab.xpocket.plugin.netstat;

import com.perfma.xlab.xpocket.spi.XPocketPlugin;
import com.perfma.xlab.xpocket.spi.command.AbstractXPocketCommand;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;


/**
 * 用于实现每个命令的核心逻辑，一个或者多个命令指向一个类。
 *
 * @author Arnold.zhao <arnold_zhao@126.com>
 * @version NetstatXPocketCommand.java, v 0.1 2021-08-23 9:58 Arnold.zhao Exp $$
 */
@CommandInfo(name = "netstat", usage = "netstat [-vWeenNcCF] [<Af>] -r         netstat {-V|--version|-h|--help}\n" +
        "       netstat [-vWnNcaeol] [<Socket> ...]\n" +
        "       netstat { [-vWeenNac] -I[<Iface>] | [-veenNac] -i | [-cnNe] -M | -s [-6tuw] } [delay]", index = 0)
public class NetstatXPocketCommand extends AbstractXPocketCommand {

    private NetstatXPocketPlugin plugin;

    @Override
    public void invoke(XPocketProcess process) throws Throwable {
        plugin.invoke(process);
    }

    @Override
    public void init(XPocketPlugin plugin) {
        this.plugin = (NetstatXPocketPlugin) plugin;
    }
}
