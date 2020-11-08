package top.colter.myplugin

import kotlinx.coroutines.SupervisorJob
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.ListenerHost
import net.mamoe.mirai.message.MessageEvent
import java.io.File
import javax.imageio.ImageIO

/**
 * 监听群消息
 */
object GroupListener : ListenerHost {
    val coroutineContext = SupervisorJob()

    @EventHandler
    suspend fun MessageEvent.onMessage() {
        // 截取消息内容
        var msg = message.toString().substring(message.toString().indexOf(']') + 1)

        // 私聊bot转发到群
        if (subject !is Group){
            bot.getGroup(PluginConfig.adminGroup).sendMessage("$senderName(${sender.id})-> $msg")
        }

        // @bot 回复
        if (msg.contains("[mirai:at:${bot.id}")){
            var ll = listOf<String>("( •̀ ω •́ )✧","φ(゜▽゜*)♪","(oﾟvﾟ)ノ","(¬‿¬)","(o゜▽゜)o☆",
                    "(っ °Д °;)っ","ヽ(*。>Д<)o゜","￣へ￣","(￣▽￣)\"")
            reply(ll[(ll.indices).random()])
        }

        // 开启/关闭群的动态转发
        if (msg.contains("#开启动态转发")){
            PluginData.groupList.add(subject.id)
            reply("开始向本群转发动态( •̀ ω •́ )✧")
        }
        if (msg.contains("#关闭动态转发")){
            PluginData.groupList.remove(subject.id)
            reply("关闭向本群转发动态(っ °Д °;)っ")
        }

        // 回复辛苦了
        if(msg.contains("辛苦了")||msg.contains("辛苦啦")||msg.contains("苦了")){
            var time = System.currentTimeMillis()
            if(PluginMain.goodWorkCount==0){
                PluginMain.goodWorkCount++
                PluginMain.tempTime = time
            }else if((time-PluginMain.tempTime)<60000){
                PluginMain.goodWorkCount++
                if(PluginMain.goodWorkCount>=3){
                    PluginMain.goodWorkCount = 0
                    reply("辛苦了(p≧▽≦)p")
                }
            }
        }
    }

}