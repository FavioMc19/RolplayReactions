package net.kokoricraft.actionschat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.kokoricraft.actionschat.commands.Commands;
import net.kokoricraft.actionschat.commands.CommandsCompleter;
import net.kokoricraft.actionschat.hooks.DiscordHook;
import net.kokoricraft.actionschat.hooks.VaultHook;
import net.kokoricraft.actionschat.listeners.PlayerListener;
import net.kokoricraft.actionschat.managers.ConfigManager;
import net.kokoricraft.actionschat.managers.GuiManager;
import net.kokoricraft.actionschat.managers.Manager;
import net.kokoricraft.actionschat.utils.Metrics;
import net.kokoricraft.actionschat.utils.Utils;

public class RolplayReactions extends JavaPlugin{
	
	private Manager manager;
	private Utils utils;
	private ConfigManager config;
	private GuiManager guimanager;
	private VaultHook vault;
	private DiscordHook discord;
	
	public void onEnable() {
		initClass();
		registerEvents();
		initHooks();
		registerCommands();
	}
	
	public void initClass() {
		manager = new Manager(this);
		utils = new Utils(this);
		guimanager = new GuiManager(this);
		config = new ConfigManager(this);
		loadConfig();
	}
	
	public void registerEvents() {
		Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
	}
	
	public void registerCommands() {
		getCommand("RolplayReactions").setExecutor(new Commands(this));
		getCommand("RolplayReactions").setTabCompleter(new CommandsCompleter(this));
	}
	
	public void initHooks() {
		this.vault = new VaultHook(this);
		this.discord = new DiscordHook(this);
		new Metrics(this, 17552);
	}
	
	public Manager getManager() {
		return manager;
	}
	
	public Utils getUtils() {
		return utils;
	}
	
	public ConfigManager getConfigManager() {
		return config;
	}
	
	public GuiManager getGuiManager() {
		return guimanager;
	}
	
	public VaultHook getVault() {
		return vault;
	}
	
	public DiscordHook getDiscordHook() {
		return discord;
	}
	
	public void loadConfig() {
		config.loadItems();
		config.loadActions();
		config.loadConfig();
		config.loadMessages();
	}
}
