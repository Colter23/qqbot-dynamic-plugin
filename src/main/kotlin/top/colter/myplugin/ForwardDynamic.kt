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
import java.awt.image.BufferedImage
import top.colter.myplugin.PluginMain.logger

/**
 * 检测动态更新 并发给群
 */
suspend fun forward (){

    delay(5000)

    //获取登陆bot
    val bot = Bot.getInstance(PluginConfig.loginQQId)

    var followNum : Int = 0
    var liveStatus : Int = 0
    var roomInfo : JSONObject
    var roomCover : String = ""
    var liveStartTime : Long = 0

    var res : JSONObject
    var desc : JSONObject
    var dynamicId : String = ""
    var dynamicType : Int = 0
    var timestamp : Long = 0
    var card : JSONObject
    var display : JSONObject
    var content : String = ""

    var pictures = mutableListOf<String>()
    var emojiList = mutableMapOf<String,java.awt.Image>()

    var emojiJson : JSONArray
    var resImg : BufferedImage

    while(true){
        delay(10000)

        logger.info {"->Start testing.开始检测"}

        for(member in PluginData.followList){
            try{
                delay(2000)

                res = httpGET(PluginData.dynamicApi + member["uid"]).getJSONObject("data").getJSONArray("cards").getJSONObject(0)
                dynamicId = res.getJSONObject("desc").getBigInteger("dynamic_id").toString()

                if (dynamicId != member["dynamicId"]){
                    logger.info {"-->${member["name"]} update dynamic"}
                    delay(2000)
                    followNum = httpGET(PluginData.followNumApi + member["uid"]).getJSONObject("data").getInteger("follower").toInt()
                    //delay(2000)

                    //var liveStatus = httpGET(liveApi + ren[0]).getJSONObject("data").getInteger("liveStatus").toInt()

                    desc = res.getJSONObject("desc")
                    dynamicType = desc.getInteger("type")

                    timestamp = desc.getBigInteger("timestamp").toLong()

                    card = JSON.parseObject(res.getJSONObject("card").toJSONString())
                    display = res.getJSONObject("display")

                    // 判断动态类型 解析数据
                    when (dynamicType){
                        //带图片的动态
                        2 -> {
                            content = card.getJSONObject("item").getString("description")
                            for (pic in card.getJSONObject("item").getJSONArray("pictures")){
                                pictures.add((pic as JSONObject).getString("img_src"))
                            }
                            try{
                                emojiJson = display.getJSONObject("emoji_info").getJSONArray("emoji_details")
                                getEmoji(emojiJson,emojiList)
                            }catch (e:Exception){

                            }
                        }

                        //带表情的文字动态
                        4 -> {
                            content = card.getJSONObject("item").getString("content")
                            try{
                                emojiJson = display.getJSONObject("emoji_info").getJSONArray("emoji_details")
                                getEmoji(emojiJson,emojiList)
                            }catch (e:Exception){

                            }
                        }

                        //视频更新动态
                        8 -> {
                            content = "视频: ${card.getString("title")}"
                            pictures.add(card.getString("pic"))
                        }
                    }

                    // 构建回复消息
                    resImg = getMsg(content,timestamp,"${member["name"]}",followNum,dynamicId,emojiList,pictures)
                    var resMsg  = MessageChainBuilder(1)
                    resMsg.add(bot.getGroup(PluginConfig.adminGroup).uploadImage(resImg))
                    resMsg.add("https://t.bilibili.com/$dynamicId")
                    sendGroups(bot,resMsg.asMessageChain())


                    //无效  接口没有此数据
                    if (liveStatus==1 && member["live"].toBoolean()){
                        var msg  = MessageChainBuilder(1)
                        msg.add("${member["name"]}")
                        msg.add(" 开播了!")
                        sendGroups(bot,msg.asMessageChain())
                    }

                    // 更新动态ID
                    member["dynamicId"] = dynamicId

                }


                delay(2000)
                roomInfo = httpGET(PluginData.liveStatusApi+member["liveRoom"] ).getJSONObject("data").getJSONObject("room_info")
                liveStatus = roomInfo.getInteger("live_status")
                if (liveStatus == 1 && !member["live"].toBoolean()){
                    pictures.clear()
                    emojiList.clear()
                    if (followNum==0){
                        delay(2000)
                        followNum = httpGET(PluginData.followNumApi + member["uid"]).getJSONObject("data").getInteger("follower").toInt()
                    }

                    liveStartTime = roomInfo.getBigInteger("live_start_time").toLong()
                    pictures.add(roomInfo.getString("cover"))

                    // 构建回复消息
                    content = "直播: ${roomInfo.getString("title")}"
                    resImg = getMsg(content,liveStartTime,"${member["name"]}",followNum,"${member["liveRoom"]}",emojiList,pictures)
                    var resMsg  = MessageChainBuilder(1)
                    resMsg.add(bot.getGroup(PluginConfig.adminGroup).uploadImage(resImg))
                    resMsg.add("https://live.bilibili.com/${member["liveRoom"]}")
                    sendGroups(bot,resMsg.asMessageChain())

                    member["live"] = true.toString()
                }


            }catch (e:Exception){
                logger.error(e.message)
                bot.getGroup(PluginConfig.adminGroup).sendMessage("ERROR: 请求数据失败！！！")
            }

            followNum = 0
            pictures.clear()
            emojiList.clear()

        }
        delay(55000)
    }
}

//群发消息
suspend fun sendGroups(bot:Bot,resMsg:MessageChain) {
    for(groupId in PluginData.groupList){
        bot.getGroup(groupId.toLong()).sendMessage(resMsg)
    }
}
