package net.kokoricraft.actionschat.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.google.common.collect.Lists;

import net.kokoricraft.actionschat.RolplayReactions;
import net.kokoricraft.actionschat.enums.PlayerType;
import net.kokoricraft.actionschat.objects.Action;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Commands implements CommandExecutor {
	RolplayReactions plugin;
	
	public Commands(RolplayReactions plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
		if(a.length == 0) {
			s.sendMessage("use /"+l+" help");
			return true;
		}
		
		switch(a[0].toLowerCase()) {
		case "lang":
			return langCommand(s, l, a);
		case "reload":
			return reloadCommand(s, l, a);
		case "list":
			return listCommand(s, l, a);
		case "test":
			return testCommand(s, l, a);
		}
		
		return true;
	}
	
	private boolean testCommand(CommandSender s, String l, String[] a) {
		String text = StringUtils.join(a, " ").replaceAll("test ", "");
		
		plugin.getUtils().test(text);
		return true;
	}

	private boolean listCommand(CommandSender s, String l, String[] a) {
		List<Action> actions = new ArrayList<Action>();
		actions.addAll(plugin.getManager().actions.values());
		
		List<List<Action>> sections = Lists.partition(actions, 9);
		
		boolean white = true;
		s.sendMessage(plugin.getUtils().color("&3=================================================="));
		s.sendMessage(plugin.getUtils().color("                            &d[&5RolplayReactions&d]"));
		s.sendMessage(plugin.getUtils().color("               &6Required: <player>&8 | &6Optional: [player]"));
		s.sendMessage("");
		
		for(List<Action> section : sections) {
			TextComponent section_component = new TextComponent();
			
			for(Action action : section) {
				TextComponent action_component = new TextComponent(action.getName()+" ");
				
				action_component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, action.getCommand()));
				action_component.setColor(white ? ChatColor.WHITE : ChatColor.GRAY);
				action_component.setHoverEvent(plugin.getUtils().getHoverEvent(List.of("&e"+action.getCommand()+
						(action.getPlayerType().equals(PlayerType.DUAL) ? " <player>" : (action.getPlayerType().equals(PlayerType.OPTIONAL) ? " [player]" : "")))));
				
				section_component.addExtra(action_component);
				white = !white;
			}
			s.spigot().sendMessage(section_component);
		}
		
		s.sendMessage(plugin.getUtils().color("&3=================================================="));
		return true;
	}
	
	private boolean reloadCommand(CommandSender s, String l, String[] a) {
		if(!per(s, "rolplayreactions.command.reload")) {
			s.sendMessage(plugin.getUtils().color(plugin.getConfigManager().messages_no_permission));
			return true;
		}
		
		plugin.loadConfig();
		s.sendMessage(plugin.getUtils().color(plugin.getConfigManager().messages_config_reloaded));
		return true;
	}
	
	private boolean langCommand(CommandSender s, String l, String[] a) {
		if(!per(s, "rolplayreactions.command.lang")) {
			s.sendMessage(plugin.getUtils().color(plugin.getConfigManager().messages_no_permission));
			return true;
		}
		
		if(a.length < 2) {
			s.sendMessage("use /"+l+" lang <lang name>");
			return true;
		}
		
		String lang = a[1].toLowerCase();
		
		plugin.getConfigManager().regenConfig(s, lang);
		return true;
	}
	
	private boolean per(CommandSender sender, String permission) {
		return sender.hasPermission(permission);
	}
}
