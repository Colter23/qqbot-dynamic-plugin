@file:Suppress("unused")
package top.colter.myplugin

import com.google.auto.service.AutoService
import kotlinx.coroutines.launch
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.registerEvents


@AutoService(JvmPlugin::class)
object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        "top.colter.dynamic-plugin",
        "1.0"
    )
) {

    var goodWorkCount = 0
    var tempTime : Long = 0

    override fun onEnable() {
        //加载插件配置数据
        PluginConfig.reload()
        //加载插件数据
        PluginData.reload()
        //注册指令
        DemoCommand.register()
        //注册监听器
        PluginMain.registerEvents(GroupListener)

        //设置运行路径
        PluginData.runPath = System.getProperty("user.dir")

        PluginMain.launch {
            logger.info("run。。。。。")
            //检测动态更新 并发送给群
            forward()
        }

    }

    override fun onDisable() {
        DemoCommand.unregister()
    }

}


