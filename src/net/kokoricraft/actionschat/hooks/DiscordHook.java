package net.kokoricraft.actionschat.hooks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import github.scarsz.discordsrv.DiscordSRV;
import net.dv8tion.jda.api.entities.TextChannel;
import net.kokoricraft.actionschat.RoleplayReactions;

public class DiscordHook {
	
	RoleplayReactions plugin;
	
	public boolean hasDiscord = false;
	
	public DiscordHook(RoleplayReactions plugin){
		this.plugin = plugin;
		hasDiscord = Bukkit.getPluginManager().isPluginEnabled("DiscordSRV");
	}
	
	public void sendMessage(String text) {
		TextChannel textChannel = DiscordSRV.getPlugin().getJda().getTextChannelById(plugin.getConfigManager().discordsrv_channel);
        textChannel.sendMessage(ChatColor.stripColor(text).replace("\n", System.getProperty("line.separator"))).queue();
	}
}
