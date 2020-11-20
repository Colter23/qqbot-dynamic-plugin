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
        val msg = message.toString().substring(message.toString().indexOf(']') + 1)

        val emoji = listOf<String>("( •̀ ω •́ )✧","φ(゜▽゜*)♪","(oﾟvﾟ)ノ","(o゜▽゜)o☆",
                "(っ °Д °;)っ","ヽ(*。>Д<)o゜","￣へ￣","(￣▽￣)\"","(。・ω・)ノ","(´-ω-)",
                "(੭ˊ꒳ˋ)੭✧","（っ ' ᵕ ' ｃ）","(੭ ᐕ))？","ฅ^•ω•^ฅ","(  `꒳´ )","(っ ॑꒳ ॑c)",
                "⸜(* ॑꒳ ॑*  )⸝✩°｡⋆","(´･ω･`)?","`(*>﹏<*)′","(●'◡'●)","( •̀ ω •́ )y","(づ￣ 3￣)づ",
                "=￣ω￣=","＞﹏＜","(￣3￣)","＞︿＜","≧ ﹏ ≦","o((>ω< ))o","ヽ(゜▽゜　)","(￣﹏￣；)",
                "つ﹏⊂","(☆-ｖ-)","（〃｀ 3′〃）","(ง •_•)ง","o(〃＾▽＾〃)o","(。・ω・。)","╰(￣ω￣ｏ)",
                "（○｀ 3′○）","(°ー°〃)","o(≧口≦)o","✧(≖ ◡ ≖✿)","(｡･∀･)ﾉﾞ","ヾ(≧∇≦*)ゝ","(๑•̀ㅂ•́)و✧",
                "ヽ(✿ﾟ▽ﾟ)ノ","(๑´ㅂ`๑)","(/≧▽≦)/","（´v｀）","ε = = (づ′▽`)づ","")

        if (msg=="#?"||msg=="#？"||msg=="#help"||msg=="#帮助"){
            reply("#? 或 #help 或 #帮助 : 功能列表\n" +
                    "#r : 从0-10随机一个数\n" +
                    "@机器人 : 随机回复表情\n" +
                    "bell粉丝数 或 贝尔粉丝数\n" +
                    "memory粉丝数 或 泡沫粉丝数\n" +
                    "lily粉丝数 或 白百合粉丝数\n" )
        }else
            if (msg == "#r"){
                val resMsg  = MessageChainBuilder(1)
                resMsg.add(At(sender as Member))
                resMsg.add("\n")
                resMsg.add("你抽到的数字为: ${(0..10).random()}\n")
                resMsg.add(emoji[(emoji.indices).random()])
                reply(resMsg.asMessageChain())
        }else
            if (msg=="#bell"||msg=="bell粉丝数"||msg=="贝尔粉丝数"||msg=="猫猫粉丝数"){
                val resImg = getFanImg(487550002,"bell")
                val resMsg = MessageChainBuilder(1)
                resMsg.add(resImg.upload())
                reply(resMsg.asMessageChain())
        }else
            if (msg=="#memory"||msg=="memory粉丝数"||msg=="泡沫粉丝数"){
                val resImg = getFanImg(487551829,"memory")
                val resMsg = MessageChainBuilder(1)
                resMsg.add(resImg.upload())
                reply(resMsg.asMessageChain())
        }else
            if (msg=="#lily"||msg=="lily粉丝数"||msg=="白百合粉丝数"||msg=="派派粉丝数"){
                val resImg = getFanImg(421347849,"lily")
                val resMsg = MessageChainBuilder(1)
                resMsg.add(resImg.upload())
                reply(resMsg.asMessageChain())
        }else // 回复辛苦了
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
        }else // @全体成员 回复收到
            if (msg.contains("[mirai:atall]")){
                reply("收到( •̀ ω •́ )y")
        }else // @bot 回复
            if (msg.contains("[mirai:at:${bot.id}")||subject !is Group){
                if ((0..1).random()==0){
                    reply(emoji[(emoji.indices).random()])
                }else{
                    val emoji = ImageIO.read(File("${PluginData.runPath}/emoji/${(1..PluginData.emojiNum).random()}.png"))
                    reply(emoji.upload())
                }
        }

        // 私聊bot转发到群
//        if (subject !is Group){
//            reply(emoji[(emoji.indices).random()])
////            bot.getGroup(PluginConfig.adminGroup).sendMessage("$senderName(${sender.id})-> $msg")
//        }


//        val pattern = """\d{1,2}.\d{1,2}日总结"""
//        if (Regex(pattern).find(msg)){
//
//        }

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