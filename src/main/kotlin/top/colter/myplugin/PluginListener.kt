package top.colter.myplugin

import kotlinx.coroutines.SupervisorJob
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.ListenerHost
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.uploadImage
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

        if (msg=="#?"||msg=="#help"||msg=="#帮助"){
            reply("#help 或 #帮助 : 指令列表"+
                    "\n" +
                    "#r : 从0-10随机一个数"+
                    "\n" +
                    "@机器人 : 随机回复表情"+ "\n" +
                    "bell粉丝数 或 贝尔粉丝数"+ "\n" +
                    "memory粉丝数 或 泡沫粉丝数"+ "\n" +
                    "lily粉丝数 或 白百合粉丝数"+ "\n" )
        }

        var emoji = listOf<String>("( •̀ ω •́ )✧","φ(゜▽゜*)♪","(oﾟvﾟ)ノ","(o゜▽゜)o☆",
                "(っ °Д °;)っ","ヽ(*。>Д<)o゜","￣へ￣","(￣▽￣)\"","(。・ω・)ノ","(´-ω-)",
                "(੭ˊ꒳ˋ)੭✧","（っ ' ᵕ ' ｃ）","(੭ ᐕ))？","ฅ^•ω•^ฅ","(  `꒳´ )","(っ ॑꒳ ॑c)",
                "⸜(* ॑꒳ ॑*  )⸝✩°｡⋆","(´･ω･`)?","`(*>﹏<*)′","(●'◡'●)","( •̀ ω •́ )y","(づ￣ 3￣)づ",
                "=￣ω￣=","＞﹏＜","＞︿＜","≧ ﹏ ≦","o((>ω< ))o","ヽ(゜▽゜　)","(￣﹏￣；)","つ﹏⊂",
                "(☆-ｖ-)")


        // @bot 回复
        if (msg.contains("[mirai:at:${bot.id}")){
            reply(emoji[(emoji.indices).random()])
        }

        if (msg == "#r"){
            var resMsg  = MessageChainBuilder(1)
            resMsg.add(At(sender as Member))
            resMsg.add("\n")
            resMsg.add("你抽到的数字为: ${(0..10).random()}\n")
            resMsg.add(emoji[(emoji.indices).random()])
            reply(resMsg.asMessageChain())
        }

        // @全体成员 回复收到
        if (msg.contains("[mirai:atall]")){
            reply("收到( •̀ ω •́ )y")
        }

        // 回复辛苦了
        if(msg.contains("辛苦了")||msg.contains("辛苦啦")||msg.contains("苦了")||msg.contains("苦啦")){
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
            }else{
                PluginMain.tempTime = time
                PluginMain.goodWorkCount = 1
            }
        }

        if (msg=="bell粉丝数"||msg=="贝尔粉丝数"){
            val resImg = getFanImg(487550002,"bell")
            val resMsg = MessageChainBuilder(1)
            resMsg.add(resImg.upload())
            reply(resMsg.asMessageChain())
        }else if (msg=="memory粉丝数"||msg=="泡沫粉丝数"){
            val resImg = getFanImg(487551829,"memory")
            val resMsg = MessageChainBuilder(1)
            resMsg.add(resImg.upload())
            reply(resMsg.asMessageChain())
        }else if (msg=="lily粉丝数"||msg=="白百合粉丝数"){
            val resImg = getFanImg(421347849,"lily")
            val resMsg = MessageChainBuilder(1)
            resMsg.add(resImg.upload())
            reply(resMsg.asMessageChain())
        }

//        if (msg == "#测试"){
//            val info = mutableListOf(
//                    mutableMapOf<String,Int>(
//                            Pair<String,Int>("fan",126096),
//                            Pair<String,Int>("riseFan",1000),
//                            Pair<String,Int>("guard",230),
//                            Pair<String,Int>("riseGuard",22)),
//                    mutableMapOf<String,Int>(
//                            Pair<String,Int>("fan",99384),
//                            Pair<String,Int>("riseFan",10000),
//                            Pair<String,Int>("guard",100),
//                            Pair<String,Int>("riseGuard",11)),
//                    mutableMapOf<String,Int>(
//                            Pair<String,Int>("fan",31687),
//                            Pair<String,Int>("riseFan",22),
//                            Pair<String,Int>("guard",11111),
//                            Pair<String,Int>("riseGuard",-10)))
//
//            val timestamp = System.currentTimeMillis()
//            val resImg = getSummaryImg(timestamp,info)
//            val resMsg = MessageChainBuilder(1)
//            resMsg.add(resImg.upload())
//            reply(resMsg.asMessageChain())
//
//        }




    }
}