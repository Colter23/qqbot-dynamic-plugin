package top.colter.myplugin

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.delay
import net.mamoe.mirai.Bot
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.uploadImage
import net.mamoe.mirai.utils.info
import top.colter.myplugin.PluginMain.logger
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat

/**
 * 检测动态更新 并发给群
 */
suspend fun forward_old() {

    delay(10000)
    //获取登陆bot
//    val bot = Bot.getInstance(PluginConfig.loginQQId)
    val bot = PluginMain.bot

    while(true) {
        logger.info { "->Start testing.开始检测" }

        //每日总结 每天23:58生成
        val dateFile = File("${PluginConfig.runPath}/date.ini")
        val date = dateFile.readText()
        val timestamp = System.currentTimeMillis()
        val time = SimpleDateFormat("HHmm").format(timestamp)
        val currDate = SimpleDateFormat("MMdd").format(timestamp)
        val info : MutableList<MutableMap<String, Int>> = mutableListOf()
        val summaryTime1 = "2357"
        val summaryTime2 = "2358"

        var fileMsg = ""
        var followNum : Int = 0
        var guardNum : Int = 0
        val followList = File("${PluginConfig.runPath}/followList.ini")
        val delay = (8000..13000).random().toLong()

        var exception = false

        try {
            for (follow in followList.readLines()) {
                if (follow[0]=='#'){
                    fileMsg += follow+"\n"
                    continue
                }
                val member = follow.split(' ')
                var dynamicId = ""
                var liveStatus = 0
                val infoMap = mutableMapOf<String,Int>()

                delay(delay)
                val res = httpGET(PluginConfig.dynamicApi + member[0]).getJSONObject("data").getJSONArray("cards").getJSONObject(0)
                dynamicId = res.getJSONObject("desc").getBigInteger("dynamic_id").toString()

                if (dynamicId != member[4]) {
//                    logger.info { "-->${member["name"]} update dynamic" }
                    delay(delay)
                    followNum = httpGET(PluginConfig.followNumApi + member[0]).getJSONObject("data").getInteger("follower")

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
//                    val resImg = getMsgImg(content, timestamp, member[1], followNum, dynamicId, emojiList, pictures)
//                    val resMsg = MessageChainBuilder(1)
//                    resMsg.add(bot.getGroup(PluginConfig.adminGroup).uploadImage(resImg))
//                    resMsg.add("https://t.bilibili.com/$dynamicId")
//                    sendGroups(bot, resMsg.asMessageChain())

                    //更新动态ID
//                    PluginConfig.followList[index]["dynamicId"] = dynamicId

                }

                delay(delay)
                val roomInfo = httpGET(PluginConfig.liveStatusApi + member[3]).getJSONObject("data").getJSONObject("room_info")
                liveStatus = roomInfo.getInteger("live_status")

                if (liveStatus == 1 && (member[2]=="0"||member[2]=="2")) {

                    val pictures = mutableListOf<String>()
                    val emojiList = mutableMapOf<String,java.awt.Image>()
                    if (followNum == 0) {
                        delay(delay)
                        followNum = httpGET(PluginConfig.followNumApi + member[0]).getJSONObject("data").getInteger("follower")
                    }

                    val liveStartTime = roomInfo.getBigInteger("live_start_time").toLong()
                    var cover = roomInfo.getString("cover")
                    var keyframe = roomInfo.getString("keyframe")
                    if (cover!=""){
                        pictures.add(cover)
                    }else if(keyframe!=""){
                        pictures.add(keyframe)
                    }

                    // 构建回复消息
                    val content = "直播: ${roomInfo.getString("title")}"
//                    val resImg = getMsgImg(content, liveStartTime, member[1], followNum, member[3], emojiList, pictures)
//                    val resMsg = MessageChainBuilder(1)
//                    resMsg.add(bot.getGroup(PluginConfig.adminGroup).uploadImage(resImg))
//                    resMsg.add("https://live.bilibili.com/${member[3]}")
//                    sendGroups(bot, resMsg.asMessageChain())

//                    PluginConfig.followList[index]["live"] = true.toString()
                }
//                if (liveStatus == 0 || liveStatus == 2) {
//                    PluginConfig.followList[index]["live"] = false.toString()
//                }


                if (followNum==0 && (time==summaryTime1||time==summaryTime2) && currDate != date){
                    delay(delay)
                    followNum = httpGET(PluginConfig.followNumApi + member[0]).getJSONObject("data").getInteger("follower")
                }

                if ((time==summaryTime1||time==summaryTime2) && currDate != date){
                    infoMap["fan"] = followNum
                    infoMap["riseFan"] = followNum - member[5].toInt()
                    delay(delay)
                    guardNum = httpGET(PluginConfig.guardApi +"ruid="+member[0]+"&roomid="+member[3]).getJSONObject("data").getJSONObject("info").getInteger("num")
                    infoMap["guard"] = guardNum
                    infoMap["riseGuard"] = guardNum - member[6].toInt()
                    info.add(infoMap)
                }

                fileMsg += if ((time==summaryTime1||time==summaryTime2) && currDate != date)
                    member[0]+" "+member[1]+" "+liveStatus+" "+member[3]+" "+dynamicId+" "+followNum+" "+guardNum+"\n"
                else
                    member[0]+" "+member[1]+" "+liveStatus+" "+member[3]+" "+dynamicId+" "+member[5]+" "+member[6]+"\n"

                followNum = 0
                guardNum = 0
            }
        } catch (e: Exception) {
            bot.getGroup(PluginConfig.adminGroup).sendMessage("ERROR: 请求处理数据失败！！！五分钟后重试\n"+e.message)
//                throw IOException("请求处理数据失败")
            delay(300000)
            exception = true
        }

        if (!exception){
            if ((time==summaryTime1||time==summaryTime2) && currDate != date){
//                val resImg = getSummaryImg(timestamp,info)
//                val resMsg = MessageChainBuilder(1)
//                resMsg.add(bot.getGroup(PluginConfig.adminGroup).uploadImage(resImg))
//                sendGroups(bot, resMsg.asMessageChain())
//                dateFile.writeText(currDate)
            }

            followList.writeText(fileMsg)
            delay(15000)///////////55000
        }

    }
}

//群发消息
suspend fun sendGroups_old(bot:Bot,resMsg:MessageChain) {
    for(groupId in PluginConfig.groupList){
        bot.getGroup(groupId.toLong()).sendMessage(resMsg)
        delay(500)
    }
}





