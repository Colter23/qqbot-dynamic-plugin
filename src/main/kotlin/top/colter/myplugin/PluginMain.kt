@file:Suppress("unused")
package top.colter.myplugin

import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin

//@AutoService(JvmPlugin::class)
object MyPluginMain : KotlinPlugin(
    JvmPluginDescription(
        "top.colter.dynamic-plugin",
        "1.0"
    )
) {
    override fun onEnable() {

    }

    override fun onDisable() {

    }

}

