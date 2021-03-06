package top.colter.myplugin

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.delay
import net.mamoe.mirai.Bot
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.uploadImage
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.toExternalImage
import top.colter.myplugin.PluginMain.logger
import java.awt.image.BufferedImage
import java.text.SimpleDateFormat

/**
 * 检测动态更新 并发给群
 */
suspend fun forward() {

    //获取登陆bot
    val bot = PluginMain.bot

    while(true) {
        logger.info { "->Start testing...开始检测..." }

        //每日总结 每天23:58生成
        val timestamp = System.currentTimeMillis()
        val time = SimpleDateFormat("HHmm").format(timestamp)
        val currDate = SimpleDateFormat("MMdd").format(timestamp)
        val info : MutableList<MutableMap<String, Int>> = mutableListOf()

        val summary = (time=="2357"||time=="2358")&&currDate!=PluginConfig.summaryDate

        val delay = (8000L..13000L)

        var index = 0

        try {
            for (member in PluginMain.subData){

                // 获取动态
                delay(delay.random())
                val res = httpGetWithCookie(PluginConfig.dynamicApi + member["uid"]).getJSONObject("data").getJSONArray("cards").getJSONObject(0)
                val dynamicId = res.getJSONObject("desc").getBigInteger("dynamic_id").toString()
                // 检测动态是否更新
                if (dynamicId != member["dynamicId"]) {

                    val desc = res.getJSONObject("desc")
                    val dynamicType = desc.getInteger("type")

                    val timestamp = desc.getBigInteger("timestamp").toLong()

                    val card = JSON.parseObject(res.getString("card"))
                    val display = res.getJSONObject("display")

                    var content = ""

                    val pictures = mutableListOf<String>()
                    val emojiList = mutableMapOf<String,java.awt.Image>()

                    var emojiJson : JSONArray = JSONArray()

                    // 判断动态类型 解析数据
                    when (dynamicType) {
                        //转发动态
                        1 -> {
                            content = "转发动态 : \n"+card.getJSONObject("item").getString("content")+"\n\n"
                            val origType = card.getJSONObject("item").getInteger("orig_type")
                            val origin = JSON.parseObject(card.getString("origin"))
                            val originUser = card.getJSONObject("origin_user").getJSONObject("info").getString("uname")
                            when (origType){
                                //直播动态
                                1 -> {
                                }
                                //带图片的动态
                                2 -> {
                                    content += "原动态 $originUser : \n"
                                    content += origin.getJSONObject("item").getString("description")
                                    for (pic in origin.getJSONObject("item").getJSONArray("pictures")) {
                                        pictures.add((pic as JSONObject).getString("img_src"))
                                    }
                                }
                                //带表情的文字动态
                                4 -> {
                                    content += "原动态 $originUser : \n"
                                    content += origin.getJSONObject("item").getString("content")
                                }
                                //视频动态
                                8 -> {
                                    content += "来自 $originUser 的视频 : ${origin.getString("title")}"
                                    pictures.add(origin.getString("pic"))
                                }
                            }
                            try {
                                emojiJson = display.getJSONObject("emoji_info").getJSONArray("emoji_details")
                                getEmoji(emojiJson, emojiList)
                            } catch (e: Exception) { }
                            try {
                                emojiJson = display.getJSONObject("origin").getJSONObject("emoji_info").getJSONArray("emoji_details")
                                getEmoji(emojiJson, emojiList)
                            } catch (e: Exception) { }
                        }
                        //带图片的动态
                        2 -> {
                            content = card.getJSONObject("item").getString("description")
                            for (pic in card.getJSONObject("item").getJSONArray("pictures")) {
                                pictures.add((pic as JSONObject).getString("img_src"))
                            }
                            try {
                                emojiJson = display.getJSONObject("emoji_info").getJSONArray("emoji_details")
                                getEmoji(emojiJson, emojiList)
                            } catch (e: Exception) {

                            }
                        }
                        //带表情的文字动态
                        4 -> {
                            content = card.getJSONObject("item").getString("content")
                            try {
                                emojiJson = display.getJSONObject("emoji_info").getJSONArray("emoji_details")
                                getEmoji(emojiJson, emojiList)
                            } catch (e: Exception) {

                            }
                        }
                        //视频更新动态
                        8 -> {
                            content = "视频: ${card.getString("title")}"
                            pictures.add(card.getString("pic"))
                        }
                    }

                    // 构建回复消息
                    val resImg = getMsgImg(content, timestamp, "${member["uid"]}", dynamicId, emojiList, pictures)
                    sendGroups(bot,"${member["uid"]}", resImg, "https://t.bilibili.com/$dynamicId")

                    //更新动态ID
                    PluginMain.subData[index]["dynamicId"] = dynamicId

                }

                val liveStatus =
                    try {
                        res.getJSONObject("display").getJSONObject("live_info").getInteger("live_status")
                    }catch (e:Exception){
                        0
                    }

                if (liveStatus == 1 && (member["liveStatus"]=="0"||member["liveStatus"]=="2")) {

                    val pictures = mutableListOf<String>()
                    val emojiList = mutableMapOf<String,java.awt.Image>()

                    delay(delay.random())
                    val roomInfo = httpGET(PluginConfig.liveStatusApi + member["liveRoom"]).getJSONObject("data").getJSONObject("room_info")

                    val liveStartTime = roomInfo.getBigInteger("live_start_time").toLong()
                    val cover = roomInfo.getString("cover")
                    val keyframe = roomInfo.getString("keyframe")
                    if (cover!=""){
                        pictures.add(cover)
                    }else if(keyframe!=""){
                        pictures.add(keyframe)
                    }

                    // 构建回复消息
                    val resImg = getMsgImg("直播: ${roomInfo.getString("title")}", liveStartTime, "${member["uid"]}", "${member["liveRoom"]}", emojiList, pictures)
                    sendGroups(bot,"${member["uid"]}", resImg, "https://live.bilibili.com/${member["liveRoom"]}")

                }
                PluginMain.subData[index]["liveStatus"] = liveStatus.toString()

                if (summary && PluginConfig.summaryList.contains(member["uid"])){

                    val infoMap = mutableMapOf<String,Int>()
                    delay(delay.random())
                    val followNum = httpGET(PluginConfig.followNumApi + member["uid"]).getJSONObject("data").getInteger("follower")
                    infoMap["fan"] = followNum
                    infoMap["riseFan"] = followNum - "${member["fan"]}".toInt()
                    delay(delay.random())
                    val guardNum = httpGET(PluginConfig.guardApi +"ruid="+member["uid"]+"&roomid="+member["liveRoom"]).getJSONObject("data").getJSONObject("info").getInteger("num")
                    infoMap["guard"] = guardNum
                    infoMap["riseGuard"] = guardNum - "${member["guard"]}".toInt()
                    info.add(infoMap)

                    PluginMain.subData[index]["fan"] = followNum.toString()
                    PluginMain.subData[index]["guard"] = guardNum.toString()
                }
                index++
            }
        } catch (e: Exception) {
            bot.getGroup(PluginConfig.adminGroup).sendMessage("ERROR: 请求处理数据失败！！！五分钟后重试\n"+e.message)
            delay(300000)
        }

        if (summary){
            val resImg = getSummaryImg(timestamp,info)
            sendGroups(bot, "0",resImg,"")
            PluginConfig.summaryDate = currDate
        }
        delay(10000)
    }
}

//群发消息
suspend fun sendGroups(bot:Bot, uid:String, resImg: BufferedImage, resStr: String) {
    if (uid=="0"){
        PluginMain.groupList.forEach { id ->
            val resMsg = MessageChainBuilder(1)
            resMsg.add(bot.getGroup(id).uploadImage(resImg.toExternalImage()))
            resMsg.add(resStr)
            bot.getGroup(id).sendMessage(resMsg.asMessageChain())
            delay(500)
        }
    }else{
        PluginMain.followMemberGroup[uid]?.forEach { id ->
            if (PluginMain.groupList.contains(id)){
                val resMsg = MessageChainBuilder(1)
                resMsg.add(bot.getGroup(id).uploadImage(resImg.toExternalImage()))
                resMsg.add(resStr)
                bot.getGroup(id).sendMessage(resMsg.asMessageChain())
            }else if (PluginMain.friendList.contains(id)){
                val resMsg = MessageChainBuilder(1)
                resMsg.add(bot.getFriend(id).uploadImage(resImg.toExternalImage()))
                resMsg.add(resStr)
                bot.getFriend(id).sendMessage(resMsg.asMessageChain())
            }
            delay(500)
        }
    }
}





