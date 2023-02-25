package net.kokoricraft.actionschat.hooks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import net.kokoricraft.actionschat.RolplayReactions;

public class DiscordHook {
	
	RolplayReactions plugin;
	
	public boolean hasDiscord = false;
	
	public DiscordHook(RolplayReactions plugin){
		this.plugin = plugin;
		hasDiscord = Bukkit.getPluginManager().isPluginEnabled("DiscordSRV");
	}
	
	public void sendMessage(String text) {
		TextChannel textChannel = DiscordSRV.getPlugin().getJda().getTextChannelById(plugin.getConfigManager().discordsrv_channel);
        textChannel.sendMessage(ChatColor.stripColor(text).replace("\n", System.getProperty("line.separator"))).queue();
	}
}
