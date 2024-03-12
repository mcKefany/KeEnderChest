package dev.kefany;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import dev.kefany.commands.EnderCMD;
import dev.kefany.Listener.EnderListener;
import dev.kefany.utils.LogUtils;

public final class Main extends JavaPlugin
{
    public static Plugin instance;
    
    public static Plugin getInstance() {
        return Main.instance;
    }
    
    public void onEnable() {
        LogUtils.enable("&fDeveloped: &eKefany");
        LogUtils.enable("&fVersion: &e1.0");
        this.saveDefaultConfig();
        Main.instance = this;
        this.getServer().getPluginManager().registerEvents(new EnderListener(), this);
        this.getCommand("enderchest").setExecutor(new EnderCMD());
    }
}
