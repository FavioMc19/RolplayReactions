package net.kokoricraft.actionschat.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.kokoricraft.actionschat.RolplayReactions;
import net.kokoricraft.actionschat.enums.PlayerType;
import net.md_5.bungee.api.chat.TextComponent;

public class Action {
	
	private String name;
	private RolplayReactions plugin;
	private PlayerType player_type;
	private List<String> actions;
	private List<String> dual_actions;
	private String command;
	private Map<String, Integer> delays;
	private boolean enabled_gui;
	private NekoItem item_gui;
	
	public Action(String name, RolplayReactions plugin) {
		this.name = name;
		this.plugin = plugin;
	}
	
	public void setPlayerType(PlayerType player_type) {
		this.player_type = player_type;
	}
	
	public void setActions(List<String> actions) {
		this.actions = actions;
	}
	
	public void setActionsDual(List<String> actions) {
		this.dual_actions = actions;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public void setDelays(Map<String, Integer> delays) {
		this.delays = delays;
	}
	
	public void setEnabledGui(Boolean enable) {
		this.enabled_gui = enable;
	}
	
	public void setItemGui(NekoItem item) {
		this.item_gui = item;
	}
	
	public Map<String, Integer> getDelays(){
		return delays;
	}
	
	public String getName() {
		return name;
	}
	
	public PlayerType getPlayerType() {
		return player_type;
	}
	
	public List<String> getActions(){
		return actions;
	}
	
	public List<String> getActionsDual(){
		return dual_actions;
	}
	
	public String getCommand() {
		return command;
	}
	
	public boolean hasEnableGui() {
		return enabled_gui;
	}
	
	public NekoItem getItemGui() {
		return item_gui;
	}
	
	public void process(Player player, Player other_player) {
		if(other_player != null) {
			plugin.getConfigManager().addCount(player, other_player, name);
		}
		
		List<String> actions = this.actions;
		
		if(dual_actions != null && !dual_actions.isEmpty() && other_player != null) {
			actions = dual_actions;
		}
		
		for(String text : actions) {
			String action = text.split(" ")[0];
			String value = text.replace(action+" ", "").replace("<player>", player.getName())
					.replace("<other_player>", other_player != null ? other_player.getName() : "<unknown player>");
			
			if(value.contains("<count>") && other_player != null) {
				int count = plugin.getConfigManager().getCount(player, other_player, name);
				value = value.replaceAll("<count>", count+"");
			}
			
			run(player, other_player, action.toLowerCase(), value);
		}
	}
	
	private void run(Player sender, Player other, String action, String value) {
		
		if(value.contains("<random_")) {
			String[] randoms = StringUtils.substringsBetween(value, "<random_", ">");
			for(String key : randoms) {
				if(key.contains("player")) {
					String type = key.split("-")[1];
					List<Player> players = new ArrayList<Player>();
					
					if(type.equals("all"))
						players.addAll(Bukkit.getOnlinePlayers());
					
					if(type.equals("world"))
						players = sender.getWorld().getPlayers();
					
					int ran = ThreadLocalRandom.current().nextInt(0, players.size());
					value = value.replaceAll("<random_"+key+">", players.get(ran).getName()+"");
				}else {
					int min = Integer.parseInt(key.split("-")[0]);
					int max = Integer.parseInt(key.split("-")[1]);
					int ran = ThreadLocalRandom.current().nextInt(min, max + 1);
					value = value.replaceAll("<random_"+key+">", ran+"");
				}
			}
		}
		
		String[] data = value.split(" ");
		
		Player player;
		
		switch(action) {
		case "[broadcast]":
			if(!plugin.getConfigManager().hover_enabled) {
				Bukkit.broadcastMessage(plugin.getUtils().color(value));
				if(plugin.getDiscordHook().hasDiscord && plugin.getConfigManager().discordsrv_enabled)
					plugin.getDiscordHook().sendMessage(plugin.getUtils().color(value));
				
				return;
			}
			
			TextComponent component = plugin.getUtils().getTextComponent(value);
			
			List<String> lines = plugin.getUtils().replace(plugin.getConfigManager().hover_message, 
					new String[] {"<player>", "<other_player>", "<command>", "<action>"},
					new String[] {sender.getName(), other == null ? "" : other.getName(), command, name});
			
			
			component.setHoverEvent(plugin.getUtils().getHoverEvent(lines));
			
			Bukkit.spigot().broadcast(component);
			if(plugin.getDiscordHook().hasDiscord && plugin.getConfigManager().discordsrv_enabled)
				plugin.getDiscordHook().sendMessage(plugin.getUtils().color(value));
			break;
			
		case "[message]":
			if(data.length == 1)
				return;
			
			player = Bukkit.getPlayerExact(data[0]);
			player.sendMessage(plugin.getUtils().color(player, value.replaceFirst(data[0]+" ", "")));
			break;
			
		case "[console]":
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getUtils().color(value));
			break;
			
		case "[sound]":
			player = Bukkit.getPlayerExact(data[0]);
			String sound_name = data[1].toUpperCase();
			float volume = Float.parseFloat(data[2]);
			float pitch = Float.parseFloat(data[3]);
			int sound_amount = Integer.parseInt(data[4]);
			int sound_sleep = Integer.parseInt(data[5]);
			
			plugin.getManager().sendSound(player, sound_amount, sound_sleep, Sound.valueOf(sound_name), volume, pitch);
			break;
			
		case "[particle]":
			String list_player = data[0];
			Player to_player = Bukkit.getPlayerExact(data[1]);
			String particle_name = data[2].toUpperCase();
			int particle_amount = Integer.parseInt(data[3]);
			int particle_sleep = Integer.parseInt(data[4]);
			double particle_random = data.length > 5 ? Double.parseDouble(data[5]) : -1;
			
			List<Player> players = new ArrayList<Player>();
			
			if(Bukkit.getPlayerExact(list_player) != null) {
				players.add(Bukkit.getPlayerExact(list_player));
			}else if(list_player.toLowerCase().equals("<all>")){
				players = to_player.getWorld().getPlayers();
			}
			
			plugin.getManager().sendParticle(players, to_player, particle_amount, particle_sleep, Particle.valueOf(particle_name), particle_random);
			break;
			
		case "[item]":
			player = Bukkit.getPlayer(data[0]);
			String item_name = data[1];
			if(player == null)
				return;
			
			ItemStack item = plugin.getManager().getItem(sender, other, item_name);
			if(item != null)
				plugin.getUtils().sendItem(player, item);
			break;
		}
	}
}
