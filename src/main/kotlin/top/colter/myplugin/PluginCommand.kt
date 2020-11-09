package top.colter.myplugin

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand

// 简单指令
object AddCommand : SimpleCommand(
    PluginMain,
    "add",
    description = "添加群"
) {
    // 会自动创建一个 ID 为 "org.example.example-plugin:command.add" 的权限.
    // 通过 /add 调用, 参数自动解析
    @Handler
    suspend fun CommandSender.handle(group: Long) {
        try{
            PluginData.groupList.add(group)
            sendMessage("添加成功( •̀ ω •́ )✧")
        }catch (e:Exception){
            sendMessage("添加失败`(*>﹏<*)′")
        }
    }
}

object DeleteCommand : SimpleCommand(
        PluginMain,
        "delete",
        description = "删除群"
) {
    @Handler
    suspend fun CommandSender.handle(group: Long) {
        try{
            PluginData.groupList.remove(group)
            sendMessage("删除成功`(*>﹏<*)′")
        }catch (e:Exception){
            sendMessage("添加失败(っ °Д °;)っ")
        }
    }
}
