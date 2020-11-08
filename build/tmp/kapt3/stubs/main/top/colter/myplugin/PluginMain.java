package top.colter.myplugin;

import java.lang.System;

@com.google.auto.service.AutoService(value = {net.mamoe.mirai.console.plugin.jvm.JvmPlugin.class})
@kotlin.Metadata(mv = {1, 4, 0}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\b\u0010\u0011\u001a\u00020\u0010H\u0016R\u001a\u0010\u0003\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001a\u0010\t\u001a\u00020\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000e\u00a8\u0006\u0012"}, d2 = {"Ltop/colter/myplugin/PluginMain;", "Lnet/mamoe/mirai/console/plugin/jvm/KotlinPlugin;", "()V", "goodWorkCount", "", "getGoodWorkCount", "()I", "setGoodWorkCount", "(I)V", "tempTime", "", "getTempTime", "()J", "setTempTime", "(J)V", "onDisable", "", "onEnable", "qqbot-plugin"})
public final class PluginMain extends net.mamoe.mirai.console.plugin.jvm.KotlinPlugin {
    private static int goodWorkCount = 0;
    private static long tempTime = 0L;
    public static final top.colter.myplugin.PluginMain INSTANCE = null;
    
    public final int getGoodWorkCount() {
        return 0;
    }
    
    public final void setGoodWorkCount(int p0) {
    }
    
    public final long getTempTime() {
        return 0L;
    }
    
    public final void setTempTime(long p0) {
    }
    
    @java.lang.Override
    public void onEnable() {
    }
    
    @java.lang.Override
    public void onDisable() {
    }
    
    private PluginMain() {
        super(null, null);
    }
}