package top.colter.myplugin

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

// 定义一个配置. 所有属性都会被追踪修改, 并自动保存.
// 配置是插件与用户交互的接口, 但不能用来保存插件的数据.
object PluginConfig : AutoSavePluginConfig() {

    // 登陆的QQ号
    var loginQQId : Long by value()
    // 管理群 私聊bot,报错都会发送此群
    var adminGroup : Long by value()

}