@file:Suppress("unused")
package top.colter.myplugin

import com.google.auto.service.AutoService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.event.registerEvents
import net.mamoe.mirai.message.MessageEvent
import java.net.HttpURLConnection
import java.net.URL

@AutoService(JvmPlugin::class)
object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        "top.colter.dynamic-plugin",
        "1.0"
    )
) {

    var goodWorkCount = 0
    var tempTime : Long = 0

    lateinit var bot : Bot

    // 全部的订阅信息
    lateinit var subData : MutableList<MutableMap<String,String>>

    // 订阅关系
//    val subId = mutableMapOf<String,MutableMap<String,List<Long>>>()
    lateinit var followList : MutableList<String>
    lateinit var groupList : MutableList<Long>
    lateinit var friendList : MutableList<Long>
    lateinit var followMemberGroup : MutableMap<String,MutableList<Long>>

    @ConsoleExperimentalApi
    override fun onEnable() {

        //加载插件配置数据
        PluginConfig.reload()
        //加载插件数据
        PluginData.reload()
        //注册指令
        AddCommand.register()
        DeleteCommand.register()
        //注册监听器
        PluginMain.registerEvents(NewFriendRequestListener)
        PluginMain.registerEvents(GroupListener)
        PluginMain.registerEvents(FriendListener)
        PluginMain.registerEvents(TempListener)
        PluginMain.registerEvents(MessageListener)

        //设置运行路径
        PluginConfig.runPath = System.getProperty("user.dir")

        PluginMain.launch {
            logger.info("forward......")
            //检测动态更新 并发送给群

            delay(10000)
//            bot = Bot.getInstance(PluginConfig.loginQQId)
            Bot.forEachInstance { b ->
                bot = b
            }
            init()
            forward()
        }
    }

    override fun onDisable() {
        save()
        AddCommand.unregister()
        DeleteCommand.unregister()
    }

}


