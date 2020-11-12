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

/**
 * 检测动态更新 并发给群
 */
suspend fun forward() {

    delay(5000)
    //获取登陆bot
    val bot = Bot.getInstance(PluginConfig.loginQQId)

    while(true) {
        logger.info { "->Start testing.开始检测" }

        var fileMsg = ""
        val followList = File("${PluginData.runPath}/followList.ini")

        for (follow in followList.readLines()) {
            if (follow[0]=='#'){
                fileMsg += follow+"\n"
                continue
            }
            val member = follow.split(' ')
            var dynamicId = ""
            var liveStatus = 0
            try {
                delay(2000)
                var followNum : Int = 0
                val res = httpGET(PluginData.dynamicApi + member[0]).getJSONObject("data").getJSONArray("cards").getJSONObject(0)
                dynamicId = res.getJSONObject("desc").getBigInteger("dynamic_id").toString()

                if (dynamicId != member[4]) {
//                    logger.info { "-->${member["name"]} update dynamic" }
                    delay(2000)
                    followNum = httpGET(PluginData.followNumApi + member[0]).getJSONObject("data").getInteger("follower").toInt()

                    val desc = res.getJSONObject("desc")
                    val dynamicType = desc.getInteger("type")

                    val timestamp = desc.getBigInteger("timestamp").toLong()

                    val card = JSON.parseObject(res.getJSONObject("card").toJSONString())
                    val display = res.getJSONObject("display")

                    var content = ""

                    val pictures = mutableListOf<String>()
                    val emojiList = mutableMapOf<String,java.awt.Image>()

                    var emojiJson : JSONArray = JSONArray()

                    // 判断动态类型 解析数据
                    when (dynamicType) {
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
                    val resImg = getMsg(content, timestamp, member[1], followNum, dynamicId, emojiList, pictures)
                    val resMsg = MessageChainBuilder(1)
                    resMsg.add(bot.getGroup(PluginConfig.adminGroup).uploadImage(resImg))
                    resMsg.add("https://t.bilibili.com/$dynamicId")
                    sendGroups(bot, resMsg.asMessageChain())

                    //更新动态ID
//                    PluginData.followList[index]["dynamicId"] = dynamicId

                }

                delay(2000)
                val roomInfo = httpGET(PluginData.liveStatusApi + member[3]).getJSONObject("data").getJSONObject("room_info")
                liveStatus = roomInfo.getInteger("live_status")

                if (liveStatus == 1 && (member[2]=="0"||member[2]=="2")) {

                    val pictures = mutableListOf<String>()
                    val emojiList = mutableMapOf<String,java.awt.Image>()
                    if (followNum == 0) {
                        delay(2000)
                        followNum = httpGET(PluginData.followNumApi + member[0]).getJSONObject("data").getInteger("follower").toInt()
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
                    val resImg = getMsg(content, liveStartTime, member[1], followNum, member[3], emojiList, pictures)
                    val resMsg = MessageChainBuilder(1)
                    resMsg.add(bot.getGroup(PluginConfig.adminGroup).uploadImage(resImg))
                    resMsg.add("https://live.bilibili.com/${member[3]}")
                    sendGroups(bot, resMsg.asMessageChain())

//                    PluginData.followList[index]["live"] = true.toString()
                }
//                if (liveStatus == 0 || liveStatus == 2) {
//                    PluginData.followList[index]["live"] = false.toString()
//                }
                followNum = 0
            } catch (e: Exception) {
                logger.error(e.message)

                bot.getGroup(PluginConfig.adminGroup).sendMessage("ERROR: 请求数据失败！！！")
            }

            fileMsg += member[0]+" "+member[1]+" "+liveStatus+" "+member[3]+" "+dynamicId+"\n"
        }
        followList.writeText(fileMsg)
        delay(55000)///////////55000
    }
}

//群发消息
suspend fun sendGroups(bot:Bot,resMsg:MessageChain) {
    for(groupId in PluginData.groupList){
        bot.getGroup(groupId.toLong()).sendMessage(resMsg)
    }
}





