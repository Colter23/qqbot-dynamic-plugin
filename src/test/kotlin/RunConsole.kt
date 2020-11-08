import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.pure.MiraiConsolePureLoader
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import top.colter.myplugin.PluginData
import top.colter.myplugin.PluginMain
import java.net.URL

@ConsoleExperimentalApi
suspend fun main() {
    MiraiConsolePureLoader.startAsDaemon()

    PluginMain.load() // 主动加载插件, Console 会调用 MyPluginMain.onLoad
    PluginMain.enable() // 主动启用插件, Console 会调用 MyPluginMain.onEnable

    val bot = MiraiConsole.addBot(1111111111, "2222222222").alsoLogin() // 登录一个测试环境的 Bot

    MiraiConsole.job.join()

}


