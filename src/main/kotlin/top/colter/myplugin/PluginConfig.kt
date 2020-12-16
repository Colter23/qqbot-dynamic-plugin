package top.colter.myplugin

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value
import top.colter.myplugin.PluginData.provideDelegate

// 定义一个配置. 所有属性都会被追踪修改, 并自动保存.
// 配置是插件与用户交互的接口, 但不能用来保存插件的数据.
object PluginConfig : AutoSavePluginConfig() {

    // 登陆的QQ号
//    var loginQQId : Long by value()
    // 管理群 私聊bot,报错都会发送此群
    var adminGroup : Long by value()

    //百度翻译api密钥
    val APP_ID by value("")
    val SECURITY_KEY by value("")

    // 运行路径 在初始化时赋值
    var runPath by value("./")

    var summaryDate by value("")

    // 动态计数
    var dynamicCount : Int by value(0)

    var emojiNum : Int by value(88)


    var followList : MutableList<String> by value()

    var groupList : MutableList<Long> by value()
    var friendList : MutableList<Long> by value()

    var followMemberGroup : MutableMap<String,MutableList<Long>> by value()

    var summaryList : MutableList<String> by value()

    var subData : MutableList<String> by value()


    // 获取b站动态API时访问的UID (通过哪个用户访问，会有一些特化信息，比如自己的关注列表谁点赞了等 好像需要登陆)
    val visitor_uid by value(111111111)
    // 获取b站动态API
    val dynamicApi by value("https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history?visitor_uid=$visitor_uid&offset_dynamic_id=0&need_top=0&host_uid=")
    // 获取b站粉丝数API
    val followNumApi by value("https://api.bilibili.com/x/relation/stat?vmid=")
    // 获取直播状态API
    val liveStatusApi by value("https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByRoom?room_id=")
    // 获取直播id API
    val liveRoomApi by value("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=")
    // 大航海数 需要参数 用户id:ruid 直播间id:roomid  eg: ruid=487550002&roomid=21811136
    val guardApi by value("https://api.live.bilibili.com/xlive/app-room/v2/guardTab/topList?page=1&page_size=1&")
    // b站cookie
    val cookie by value("")

}