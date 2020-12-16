package top.colter.myplugin

import kotlinx.coroutines.delay


suspend fun init(){
    PluginMain.logger.info("Start Init Data... 开始初始化数据...")

    PluginMain.followList = PluginConfig.followList
    PluginMain.groupList = PluginConfig.groupList
    PluginMain.friendList = PluginConfig.friendList
    PluginMain.followMemberGroup = PluginConfig.followMemberGroup

    var i = 0
    while (PluginConfig.subData.size/7>i){
        val map = mutableMapOf<String,String>()
        map["uid"] = PluginConfig.subData[i*7+0]
        map["name"] = PluginConfig.subData[i*7+1]
        map["dynamicId"] = PluginConfig.subData[i*7+2]
        map["liveStatus"] = PluginConfig.subData[i*7+3]
        map["liveRoom"] = PluginConfig.subData[i*7+4]
        map["fan"] = PluginConfig.subData[i*7+5]
        map["guard"] = PluginConfig.subData[i*7+6]
        try {
            PluginMain.subData.add(map)
        }catch (e:Exception){
            PluginMain.subData = mutableListOf(map)
        }
//        println(i)
        i++
    }
//    println(PluginMain.subData)

    // 更新订阅信息
//    println(PluginMain.subData)
    PluginMain.subData.forEach { u ->
        updateInfo(u)
    }
//    println(PluginMain.subData)

//    PluginConfig.followList.forEach { uid ->
//        val map = getFollowInfo(uid)
//        try {
//            PluginMain.subData.add(map)
//        }catch (e:Exception){
//            PluginMain.subData = mutableListOf(map)
//        }
////        println(PluginMain.subData.toString())
//    }

    PluginMain.logger.info("Init Data End... 初始化数据结束...")
}

suspend fun updateInfo(oldMap : MutableMap<String, String>) : MutableMap<String, String>{
    delay(2000)
    val res = httpGetWithCookie(PluginConfig.dynamicApi+oldMap["uid"]).getJSONObject("data").getJSONArray("cards").getJSONObject(0)
    oldMap["dynamicId"] = res.getJSONObject("desc").getBigInteger("dynamic_id").toString()
    try {
        oldMap["liveStatus"] = res.getJSONObject("display").getJSONObject("live_info").getInteger("live_status").toString()
    }catch (e:Exception){
        oldMap["liveStatus"] = "0"
    }
    return oldMap
}

suspend fun getFollowInfo(uid:String): String {
    delay(2000)
    val res = httpGetWithCookie(PluginConfig.dynamicApi+uid).getJSONObject("data").getJSONArray("cards").getJSONObject(0)
    val map = mutableMapOf<String,String>()
    val userProfile = res.getJSONObject("desc").getJSONObject("user_profile")
    val name = userProfile.getJSONObject("info").getString("uname")
    map["uid"] = uid
    map["name"] = name
    map["dynamicId"] = res.getJSONObject("desc").getBigInteger("dynamic_id").toString()
    try {
        map["liveStatus"] = res.getJSONObject("display").getJSONObject("live_info").getInteger("live_status").toString()
    }catch (e:Exception){
        map["liveStatus"] = "0"
    }

    val face = userProfile.getJSONObject("info").getString("face")
    val pendant = userProfile.getJSONObject("pendant").getString("image")

    delay(2000)
    val liveRoom = httpGetWithCookie(PluginConfig.liveRoomApi+uid).getJSONObject("data").getBigInteger("roomid").toString()
    map["liveRoom"] = liveRoom

    map["fan"] = ""
    map["guard"] = ""
    generateImg(uid,name,face,pendant)


//    delay(2000)
//    map["fan"] = httpGET(PluginConfig.followNumApi + uid).getJSONObject("data").getInteger("follower").toString()
//
//    delay(2000)
//    map["guard"] = httpGET(PluginConfig.guardApi +"ruid="+uid+"&roomid="+liveRoom).getJSONObject("data").getJSONObject("info").getInteger("num").toString()

    return name
}