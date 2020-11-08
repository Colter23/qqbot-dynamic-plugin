package top.colter.myplugin

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

// 定义插件数据
object PluginData : AutoSavePluginData() {

    // 获取b站动态API时访问的UID (通过哪个用户访问，会有一些特化信息，比如自己的关注列表谁点赞了等 好像需要登陆)
    val visitor_uid by value(111111111)
    // 获取b站动态API
    val dynamicApi by value("https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history?visitor_uid=$visitor_uid&offset_dynamic_id=0&need_top=0&host_uid=")
    // 获取b站粉丝数API
    val followNumApi by value("https://api.bilibili.com/x/relation/stat?vmid=")
    // 获取直播状态API
    val liveStatusApi by value("https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByRoom?room_id=")

    // 运行路径 在初始化时赋值
    var runPath by value("./")

    // 动态计数
    var dynamicCount : Int by value(0)

    // 转发群列表 可通过 #开启动态转发 添加
    var groupList : MutableList<Long> by value(mutableListOf())

    // 订阅列表
    var followList by value(mutableListOf(
        mutableMapOf<String,String>(
            Pair<String,String>("uid","487550002"),
            Pair<String,String>("name","bell"),
            Pair<String,String>("live","false"),
            Pair<String,String>("liveRoom","21811136"),
            Pair<String,String>("dynamicId","454388105190912992")
        ),
        mutableMapOf<String,String>(
            Pair<String,String>("uid","487551829"),
            Pair<String,String>("name","memory"),
            Pair<String,String>("live","false"),
            Pair<String,String>("liveRoom","21955596"),
            Pair<String,String>("dynamicId","454332438125895159")
        ),
        mutableMapOf<String,String>(
            Pair<String,String>("uid","421347849"),
            Pair<String,String>("name","lily"),
            Pair<String,String>("live","false"),
            Pair<String,String>("liveRoom","21415012"),
            Pair<String,String>("dynamicId","454405156217475634")
        )
    ))

}