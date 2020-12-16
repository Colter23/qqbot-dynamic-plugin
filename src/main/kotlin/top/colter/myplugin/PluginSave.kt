package top.colter.myplugin

fun save(){
    PluginMain.logger.info("Start Save Data... 开始保存数据...")

    PluginConfig.followList = PluginMain.followList
    PluginConfig.groupList = PluginMain.groupList
    PluginConfig.friendList = PluginMain.friendList
    PluginConfig.followMemberGroup = PluginMain.followMemberGroup
    PluginConfig.subData = mutableListOf()
    PluginMain.subData.forEach { item ->
        item.forEach { (t, u) ->
            PluginConfig.subData.add(u)
        }
    }

    PluginMain.logger.info("Save Data End... 保存数据完成...")
}



