package top.colter.myplugin

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand

// 简单指令
object DemoCommand : SimpleCommand(
    PluginMain,
    "foo",
    description = "示例指令"
) {
    // 会自动创建一个 ID 为 "org.example.example-plugin:command.foo" 的权限.


    // 通过 /foo 调用, 参数自动解析
    @Handler
    suspend fun CommandSender.handle(int: Int, str: String) { // 函数名随意, 但参数需要按顺序放置.

        sendMessage("/foo 的第一个参数是 $int, 第二个是 $str")
    }
}