package net.kokoricraft.actionschat.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import net.kokoricraft.actionschat.RolplayReactions;

public class CommandsCompleter implements TabCompleter {
	
	RolplayReactions plugin;
	
	public CommandsCompleter(RolplayReactions plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
		if(a.length == 1) {
			return getArguments(List.of("lang", "reload", "list"), a[0]);
		}
		
		switch(a[0].toLowerCase()) {
		case "lang":
			return getArguments(List.of("english", "spanish"), a[1]);
		}
		
		return new ArrayList<String>();
	}
	
	private List<String> getArguments(List<String> allcommands, String arg){
		List<String> endArgs = new ArrayList<String>();
		for(String text : allcommands) {
			if(text.toLowerCase().startsWith(arg.toLowerCase())) {
				endArgs.add(text);
			}
		}
		return endArgs;
	}
	
}
