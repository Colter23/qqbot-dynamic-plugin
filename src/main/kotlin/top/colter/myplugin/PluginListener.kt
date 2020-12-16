package top.colter.myplugin

import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.ListenerHost
import net.mamoe.mirai.event.ListeningStatus
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.message.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.PokeMessage.Types.Poke
import top.colter.myplugin.FriendListener.onMessage
import top.colter.myplugin.GroupListener.onMessage
import java.io.File
import javax.imageio.ImageIO
import kotlin.coroutines.CoroutineContext

val emoji = listOf<String>("( •̀ ω •́ )✧","φ(゜▽゜*)♪","(oﾟvﾟ)ノ","(o゜▽゜)o☆",
        "(っ °Д °;)っ","ヽ(*。>Д<)o゜","￣へ￣","(￣▽￣)\"","(。・ω・)ノ","(´-ω-)",
        "(੭ˊ꒳ˋ)੭✧","（っ ' ᵕ ' ｃ）","(੭ ᐕ))？","ฅ^•ω•^ฅ","(  `꒳´ )","(っ ॑꒳ ॑c)",
        "⸜(* ॑꒳ ॑*  )⸝✩°｡⋆","(´･ω･`)?","`(*>﹏<*)′","(●'◡'●)","( •̀ ω •́ )y","(づ￣ 3￣)づ",
        "=￣ω￣=","＞﹏＜","(￣3￣)","＞︿＜","≧ ﹏ ≦","o((>ω< ))o","ヽ(゜▽゜　)","(￣﹏￣；)",
        "つ﹏⊂","(☆-ｖ-)","（〃｀ 3′〃）","(ง •_•)ง","o(〃＾▽＾〃)o","(。・ω・。)","╰(￣ω￣ｏ)",
        "（○｀ 3′○）","(°ー°〃)","o(≧口≦)o","✧(≖ ◡ ≖✿)","(｡･∀･)ﾉﾞ","ヾ(≧∇≦*)ゝ","(๑•̀ㅂ•́)و✧",
        "ヽ(✿ﾟ▽ﾟ)ノ","(๑´ㅂ`๑)","(/≧▽≦)/","（´v｀）","ε = = (づ′▽`)づ","")

/**
 * 同意好友申请
 */
object NewFriendRequestListener : ListenerHost{
    @EventHandler
    suspend fun NewFriendRequestEvent.onMessage(){
        this.accept()
        delay(2000)
        bot.getFriend(fromId).sendMessage("( •̀ ω •́ )✧")
    }
}

/**
 * 监听群消息
 */
object GroupListener : ListenerHost {
    val coroutineContext = SupervisorJob()

    @EventHandler
    suspend fun GroupMessageEvent.onMessage() {

        // 截取消息内容
        val msg = message.toString().substring(message.toString().indexOf(']') + 1)

        if (msg=="#?"||msg=="#？"||msg=="#help"||msg=="#帮助"||msg=="#功能"){
            reply("#? 或 #help 或 #帮助 : 功能列表\n" +
                    "#r : 从0-10随机一个数\n" +
                    "@机器人 : 随机回复表情\n" +
                    "bell粉丝数 或 贝尔粉丝数\n" +
                    "memory粉丝数 或 泡沫粉丝数\n" +
                    "lily粉丝数 或 白百合粉丝数\n" +
                    "xx.xx总结 : 对应日期的每日总结")
            this.intercept()
        }else
        if (msg == "#r"){
            val resMsg  = MessageChainBuilder(1)
            resMsg.add(At(sender as Member))
            resMsg.add("\n")
            resMsg.add("你抽到的数字为: ${(0..10).random()}\n")
            resMsg.add(emoji[(emoji.indices).random()])
            reply(resMsg.asMessageChain())
            this.intercept()
        }else
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
            this.intercept()
        }else // @全体成员 回复收到
        if (msg.contains("[mirai:atall]")){
            reply("收到( •̀ ω •́ )y")
            this.intercept()
        }
//        if (msg=="#测试"){
//            val resImg = generateImg("11111","碧居结衣Official","https://i2.hdslb.com/bfs/face/99d97c653e45aa1c8ec70dfeae6efa8667861c8c.jpg","https://i2.hdslb.com/bfs/garb/c55538368be301a33f0dc891356469c2d3a44407.png")
////            val resMsg  = MessageChainBuilder(1)
////            resMsg.add(resImg.upload())
////            reply(resMsg.asMessageChain())
//            this.intercept()
//        }
        //Event: NewFriendRequestEvent(bot=Bot(2945271969), eventId=1607832143000000, message=我是ColterのBot, fromId=1527296113, fromGroupId=1030460255, fromNick=ColterのBot)


//        if (msg=="#voice"){
//            val vv = File("${PluginData.runPath}/test.mp3")
//            val vvv = vv.inputStream()
//            reply(bot.getGroup(1030460255).uploadVoice(vvv))
//            vvv.close()
//        }


        // 私聊bot转发到群
//        if (subject !is Group){
//            reply(emoji[(emoji.indices).random()])
////            bot.getGroup(PluginConfig.adminGroup).sendMessage("$senderName(${sender.id})-> $msg")
//        }

        //测试每日总结
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

/**
 * 监听好友消息
 */
object FriendListener : ListenerHost {
    @EventHandler
    suspend fun FriendMessageEvent.onMessage(){
        val msg = message.toString().substring(message.toString().indexOf(']') + 1)
        if (msg=="#?"||msg=="#？"||msg=="help"||msg=="帮助"||msg=="功能"){
            reply("如要使用b站动态推送功能请回复 ‘开启私人订阅’\n回复 ‘功能’ 或 ‘帮助’ 查看命令列表\n如果有问题或事情请加 3375582524\n玩的开心ヾ(≧∇≦*)ゝ")
            reply("开启私人订阅\n关闭私人订阅\n"+
                    "#r 或 随机数 : 从0-10随机一个数\n"+
                    "add uid 或 添加 uid : 添加订阅\n"+
                    "del uid 或 删除 uid : 删除订阅\n"+
                    "订阅列表")
            this.intercept()
        }else
        if (msg == "开启私人订阅"){
            if (PluginMain.friendList.contains(friend.id)){
                reply("你已经开启过私人订阅了\n＞﹏＜")
            }else{
                PluginMain.friendList.add(friend.id)
                reply("开启私人订阅成功\n(oﾟvﾟ)ノ")
                reply("注意事项：\n"+
                        "不要添加过多订阅，访问周期会很长\n"+
                        "不要过度依赖机器人，机器人并不稳定，随时可能爆炸\n"+
                        "玩的开心( •̀ ω •́ )y")
            }
            this.intercept()
        }else
        if (msg == "关闭私人订阅"){
            if (PluginMain.friendList.contains(friend.id)){
                PluginMain.friendList.remove(friend.id)
                reply("关闭私人订阅成功\n(°ー°〃)")
            }else{
                reply("啊这(°ー°〃)")
            }
            this.intercept()
        }else
        if (msg.contains("添加")||msg.contains("add")){
            if (!PluginMain.friendList.contains(friend.id)) {
                reply("请先开启私人订阅功能\n＞﹏＜")
                this.intercept()
                return
            }

            var uid = ""
            var name = ""
            uid = if (msg.contains("add")){
                msg.substring(4)
            }else{
                msg.substring(3)
            }
            try {
                PluginMain.followMemberGroup[uid]!!.add(friend.id)
                PluginMain.subData.forEach { item ->
                    if (item["uid"] == uid){
                        name = item["name"].toString()
                        return@forEach
                    }
                }
                reply("添加 $name 成功\n( •̀ ω •́ )y")
            }catch (e:Exception){
                reply("添加并初始化信息中，请耐心等待，大概需要10s")
                try {

                    name = getFollowInfo(uid)
                    if (!PluginMain.followList.contains(uid)){
                        PluginMain.followList.add(uid)
                    }
                    PluginMain.followMemberGroup[uid] = mutableListOf(friend.id)

//                    println(PluginMain.followMemberGroup.toString())

                    reply("添加 $name 成功\n( •̀ ω •́ )y")
                }catch (e:Exception){
                    reply("添加 $uid 失败! 内部错误 或 检查uid是否正确\n$e")
                }
            }

            this.intercept()
        }else
        if (msg.contains("删除")||msg.contains("del")){
            if (!PluginMain.friendList.contains(friend.id)) {
                reply("请先开启私人订阅功能\n＞﹏＜")
                this.intercept()
                return
            }
            var uid = ""
            var name = ""
            try {
                uid = if (msg.contains("del")){
                    msg.substring(4)
                }else{
                    msg.substring(3)
                }

                PluginMain.followMemberGroup[uid]?.remove(friend.id)
                if (PluginMain.followMemberGroup[uid]?.size==0){
                    PluginMain.followList.remove(uid)
                    PluginMain.followMemberGroup.remove(uid)
                    PluginMain.subData.forEach { u ->
                        if (u["uid"]==uid) {
                            PluginMain.subData.remove(u)
                            return@forEach
                        }
                    }
                }
                reply("删除 $uid 成功")
            }catch (e:Exception){
                reply("删除 $uid 失败! 内部错误 或 检查uid是否正确\n$e")
            }
            this.intercept()
        }else
        if (msg == "订阅列表"){
            if (!PluginMain.friendList.contains(friend.id)) {
                reply("请先开启私人订阅功能\n＞﹏＜")
                this.intercept()
                return
            }
            var res = ""
            PluginMain.followMemberGroup.forEach { (uid, list) ->
                if (list.contains(friend.id)){
                    PluginMain.subData.forEach { u ->
                        if (uid == u["uid"]){
                            res+=u["name"] +"  "+uid+"\n"
                        }
                    }
                }
            }
            reply(res)
            this.intercept()
        }else
        if (msg == "#r"||msg == "随机数"){
            val resMsg  = ""
            resMsg.plus("你抽到的数字为: ${(0..10).random()}\n")
            resMsg.plus(emoji[(emoji.indices).random()])
            reply(resMsg)
            this.intercept()
        }
    }
}

/**
 * 监听群临时会话消息
 */
object TempListener : ListenerHost {
    @EventHandler
    suspend fun TempMessageEvent.onMessage(){

    }
}

/**
 * 监听消息
 */
object MessageListener : ListenerHost {
    @EventHandler
    suspend fun MessageEvent.onMessage(){
        val msg = message.toString().substring(message.toString().indexOf(']') + 1)
        if (msg == "#保存数据"){
            save()
            reply("保存成功")
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
            if (msg=="#lily"||msg=="lily粉丝数"||msg=="白百合粉丝数"||msg=="派派粉丝数") {
                val resImg = getFanImg(421347849, "lily")
                val resMsg = MessageChainBuilder(1)
                resMsg.add(resImg.upload())
                reply(resMsg.asMessageChain())
        }else // 历史每日总结
        if (msg.contains(Regex("""\d{1,2}.\d{1,2}总结"""))){
            val d = msg.substring(0,msg.indexOf('总')).replace(".","")
            val resMsg = MessageChainBuilder(1)
            try {
                val resImg = ImageIO.read(File("${PluginConfig.runPath}/bg/summary/$d.jpg"))
                resMsg.add(resImg.upload())
            }catch (e:Exception){
                resMsg.add("没有找到此日的总结(*>﹏<*)")
            }
            reply(resMsg.asMessageChain())
        }else // @bot 回复
        if (msg.contains("[mirai:at:${bot.id}")||subject !is Group){
            if ((0..1).random()==0){
                reply(emoji[(emoji.indices).random()])
            }else{
                val emoji = ImageIO.read(File("${PluginConfig.runPath}/emoji/${(1..PluginConfig.emojiNum).random()}.png"))
                reply(emoji.upload())
            }
        }
    }
}