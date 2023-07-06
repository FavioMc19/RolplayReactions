package net.kokoricraft.actionschat.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.kokoricraft.actionschat.RoleplayReactions;
import net.kokoricraft.actionschat.enums.PlayerType;
import net.kokoricraft.actionschat.objects.Action;
import net.kokoricraft.actionschat.objects.NekoConfig;
import net.kokoricraft.actionschat.objects.NekoItem;

public class ConfigManager {
	
	RoleplayReactions plugin;
	
	public ConfigManager(RoleplayReactions plugin) {
		this.plugin = plugin;
		loadCounts();
	}
	
	NekoConfig actions_config;
	NekoConfig items_config;
	NekoConfig count_config;
	NekoConfig config;
	NekoConfig messages_config;
	
	public Boolean gui_enabled;
	public Boolean hover_enabled;
	public List<String> hover_message;
	public Boolean discordsrv_enabled;
	public String discordsrv_channel;
	
	public void loadConfig() {
		config = new NekoConfig("config.yml", plugin);
		
		gui_enabled = config.getBoolean("enabled_gui", true);
		
		hover_enabled = config.getBoolean("hover.enabled", true);
		hover_message = config.getStringList("hover.message", List.of("&f<command> <player>"));
		
		discordsrv_enabled = config.getBoolean("discordsrv.enabled");
		discordsrv_channel = config.getString("discordsrv.channel", "INSERT CHANNEL ID HERE");
	}
	
	
	public String messages_need_player;
	public String messages_wait_delay;
	public String messages_config_reloaded;
	public String messages_lang_changed;
	public String messages_lang_no_exist;
	public String messages_lock;
	public String messages_unlock;
	public String messages_no_permission;
	
	public void loadMessages() {
		messages_config = new NekoConfig("messages.yml", plugin);
		
		messages_need_player = messages_config.getString("need_player");
		messages_wait_delay = messages_config.getString("wait_delay");
		messages_config_reloaded = messages_config.getString("config_reloaded");
		messages_lang_changed = messages_config.getString("lang_changed");
		messages_lang_no_exist = messages_config.getString("lang_no_exist");
		messages_lock = messages_config.getString("lock_action");
		messages_unlock = messages_config.getString("unlock_action");
		messages_no_permission = messages_config.getString("no_permission");
	}
	
	public void loadActions() {
		actions_config = new NekoConfig("actions.yml", plugin);
		FileConfiguration config = actions_config.getConfig();
		
		plugin.getManager().actions.clear();
		plugin.getGuiManager().gui_actions.clear();
		
		for(String name : config.getConfigurationSection("").getKeys(false)) {
			String command = config.getString(name+".command");
			
			if(command == null)
				continue;
			
			String player_type = config.getString(name+".player_type");
			
			if(player_type == null)
				player_type = "Single";
			
			List<String> actions = config.getStringList(name+".actions");
			List<String> dual_actions = config.getStringList(name+".dual_actions");
			
			Map<String, Integer> delays = new HashMap<String, Integer>();
			
			if(config.contains(name+".delays"))
				for(String rank : config.getConfigurationSection(name+".delays").getKeys(false))
					delays.put(rank, config.getInt(name+".delays."+rank));
			
			Action action = new Action(name, plugin);
			
			if(config.contains(name+".gui")) {
				String material = config.getString(name+".gui.material");
				action.setEnabledGui(config.getBoolean(name+".gui.enabled"));
				
				if(material.contains("custom:")) {
					action.setItemGui(plugin.getManager().items.get(material.replaceAll("custom: ", "")));
				}else {
					action.setItemGui(new NekoItem(plugin, config.getConfigurationSection(name+".gui")));
				}
				plugin.getGuiManager().gui_actions.add(action);
			}
			
			action.setActions(actions);
			action.setActionsDual(dual_actions);
			action.setCommand(command.toLowerCase());
			action.setPlayerType(PlayerType.valueOf(player_type.toUpperCase()));
			action.setDelays(delays);
			
			plugin.getManager().actions.put(command.toLowerCase(), action);
		}
	}
	
	public NekoItem locked_item;
	
	public void loadItems() {
		items_config = new NekoConfig("items.yml", plugin);
		FileConfiguration config = items_config.getConfig();
		
		if(config.contains("items")) {
			for(String name : config.getConfigurationSection("items").getKeys(false)) {
				NekoItem nekoitem = new NekoItem(plugin, config.getConfigurationSection("items."+name));
				plugin.getManager().items.put(name, nekoitem);
			}
		}
		
		if(config.contains("menu_order_priority"))
			plugin.getGuiManager().items_order = config.getStringList("menu_order_priority");
		
		if(config.contains("locked_item"))
			locked_item = new NekoItem(plugin, config.getConfigurationSection("locked_item"));
	}
	
	public void loadCounts() {
		count_config = new NekoConfig("counts.yml", plugin);
	}
	
	public int getCount(Player player, Player other_player, String action) {
		String p_uuid = player.getUniqueId().toString();
		String p2_uuid = other_player.getUniqueId().toString();
		
		return count_config.getInt("players."+p_uuid+".count."+p2_uuid+"."+action) + count_config.getInt("players."+p2_uuid+".count."+p_uuid+"."+action);
	}
	
	public void addCount(Player player, Player other_player, String action) {
		String p_uuid = player.getUniqueId().toString();
		String p2_uuid = other_player.getUniqueId().toString();
		
		int current = count_config.getInt("players."+p_uuid+".count."+p2_uuid+"."+action);
		
		count_config.set("players."+p_uuid+".count."+p2_uuid+"."+action, current+1);
		count_config.saveConfig();
	}
	
	public void regenConfig(CommandSender sender, String name) {
		boolean exist = genConfig(name, "messages.yml") && genConfig(name, "items.yml") && genConfig(name, "actions.yml");
		
		if(exist) {
			plugin.loadConfig();
			sender.sendMessage("Configuracion de lenguaje cambiada.");
			return;
		}
		sender.sendMessage("La configuracion de lenguaje \""+name+"\" no existe");
		sender.sendMessage("Puedes tradir la configuracion y enviarla a nuestro discord.");
	}
	
	private boolean genConfig(String name, String file_name) {
		String languaje = name;
		
		if(name.equals("english")) {
			languaje = "";
		}else {
			languaje = name+"/";
		}
		try {
			File file = new File(plugin.getDataFolder()+"/"+file_name);
			InputStream imputstream = plugin.getResource(languaje+file_name);
			
			try(FileOutputStream out = new FileOutputStream(file, false)){
				int read;
				byte[] bytes = new byte[8192];
				while((read = imputstream.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
			}
			return true;
		}catch(Exception ex) {
			return false;
		}
	}
}
