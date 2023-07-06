package net.kokoricraft.actionschat.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kokoricraft.actionschat.RoleplayReactions;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Utils {
	RoleplayReactions plugin;
	
	private boolean hasPapi;
	
	public Utils(RoleplayReactions plugin) {
		this.plugin = plugin;
		hasPapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI").isEnabled();
	}
	
	public String color(String text) {
		return color(null, text);
	}
	
	public String color(Player player, String text) {
		return setPlaceholders(player, ChatColor.translateAlternateColorCodes('&', colorHex(text)));
	}
	
	public String color(Player player, Player other, String text) {
		return color(player, text).replaceAll("<other_player>", other == null ? "" : other.getName())
				.replaceAll("<player>", player.getName());
	}
	
	public List<String> color(Player player, List<String> texts){
		List<String> nList = new ArrayList<String>();
		
		for(String text : texts) {
			nList.add(player == null ? color(text) : color(player, text));
		}
		return nList;
	}
	
	public List<String> color(List<String> texts){
		return color(null, texts);
	}
	
	public Boolean isHex(String text) {
		if(text == null)
			return false;
		
		if(text.startsWith("&"))
			text = text.substring(1, text.length());
		
		 Matcher matcher = hex_separator_pattern.matcher(text);
	 
	     return matcher.matches();
	}
	
	Pattern hex_separator_pattern = Pattern.compile("(&#|#)([A-Fa-f0-9]{6})");
	
	public List<String> hexSeparator(String text) {
		List<String> texts = new ArrayList<String>();
		
		 Matcher matcher = hex_separator_pattern.matcher(text);
		 
		 int index = 0;
		 
		 while(matcher.find()) {
			 texts.add(text.substring(index, matcher.start()));
			 texts.add(matcher.group().startsWith("&") ? matcher.group() : "&"+matcher.group());
			 index = matcher.end();
		 }
		 
		 texts.add(text.substring(index, text.length()));
		 return texts;
	}
	
	public String colorHex(String text) {
		String message = "";
		
		Matcher matcher = hex_separator_pattern.matcher(text);
		
		int index = 0;
		while(matcher.find()) {
			message += text.substring(index, matcher.start()) + net.md_5.bungee.api.ChatColor.of((matcher.group().startsWith("&") ? matcher.group().substring(1, matcher.group().length()) : matcher.group()));
			index = matcher.end();
		}
		return message += text.substring(index, text.length());
	}
	
	Pattern hex_parse_pattern = Pattern.compile("�x(�[A-Fa-f0-9]){6}");
	
	public String parseHex(String text) {
		String nText = "";
		int index = 0;
		Matcher matcher = hex_parse_pattern.matcher(text);
		
		 while(matcher.find()) {
			 nText+= text.substring(index, matcher.start())+matcher.group().replaceAll("�x", "").replaceAll("�", "");
			 index = matcher.end();
		 }
		 
		 return nText += text.substring(index, text.length());
	}
	
	Pattern papi_pattern = Pattern.compile("%");
	
	public void test(String message) {
		Matcher matcher = papi_pattern.matcher(message);
		List<String> parts = new ArrayList<String>();
		
		int index = 0;
		
		while(matcher.find()) {
			if(index == 0) {
				parts.add(message.substring(index, matcher.start()));
				index = matcher.end();
				continue;
			}
			
			parts.add(message.substring(index-1, matcher.end()));
				
		}
		TextComponent component = getTextComponent(message);
		
		Player player = Bukkit.getPlayer("FavioMC19");
		
		for(BaseComponent com : getComponents(component)) {
			player.spigot().sendMessage(com);
		}
		Bukkit.broadcast(parts.toString(), "*");
	}
	
	public boolean isPapiVar(String text) {
		if(!text.contains("_"))
			return false;
		
		String name = text.split("_")[0];
		PlaceholderExpansion expansion = PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().findExpansionByIdentifier( name.substring(1, name.length())).orElse(null);
		return expansion != null;
	}
	
	public net.md_5.bungee.api.ChatColor getLastColor(TextComponent component) {
		  List<BaseComponent> extras = component.getExtra();
		  
		  if(extras != null && !extras.isEmpty())
		    return getLastColor(extras);
		  
		  return component.getColor();
		}

		public net.md_5.bungee.api.ChatColor getLastColor(List<BaseComponent> list) {
		  BaseComponent last = list.get(list.size() - 1);
		  List<BaseComponent> extras = last.getExtra();
		  
		  if(extras != null && !extras.isEmpty())
		    return getLastColor(extras);
		  
		  return last.getColor();
		}
		
		public List<BaseComponent> getComponents(TextComponent component){
			List<BaseComponent> components = new ArrayList<BaseComponent>();
			
			if(component.getExtra() == null || component.getExtra().isEmpty()) {
				components.add(component);
				return components;
			}
			
			components = getComponents(component.getExtra());
			
			return components;
		}
		
		public List<BaseComponent> getComponents(List<BaseComponent> list){
			List<BaseComponent> components = new ArrayList<BaseComponent>();
			for(BaseComponent component : list) {
				if(component.getExtra() == null || component.getExtra().isEmpty()) {
					components.add(component);
					continue;
				}
				components.addAll(getComponents(component.getExtra()));
			}
			return components;
		}
	
	public TextComponent getTextComponent(String text) {
		TextComponent base = new TextComponent();
		
		String color = null;
		for(String part : hexSeparator(text)) {
			if(isHex(part)) {
				color = part;
			}else {
				TextComponent component = new TextComponent(color(part));
				if(color != null)
					component.setColor(net.md_5.bungee.api.ChatColor.of(color.startsWith("&") ? color.substring(1, color.length()) : color));
				base.addExtra(component);
			}
		}
		return base;
	}
	
	public String colorHex2(String text) {
		String message = "";
		List<String> parts = hexSeparator(text);
		
		String color = null;
		for(String part : parts) {
			if(isHex(part)) {
				color = part.substring(1, part.length());
			}else {
				if(color == null) {
					message += part;
					continue;
				}
				message += net.md_5.bungee.api.ChatColor.of(color)+part;
			}
		}
		return message;
	}
	
	public String setPlaceholders(Player player, String text) {
		if(!hasPapi)
			return text;
		
		return PlaceholderAPI.setPlaceholders(player, text);
	}
	
	public  void sendItem(Player player, ItemStack item) {
		Boolean isFull = !Arrays.asList(player.getInventory().getStorageContents()).contains(null);
		
		if(hasSpace(player, item, item.getAmount()) || !isFull) {
			player.getInventory().addItem(item.clone());
			return;
		}
		
		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				player.getWorld().dropItem(player.getLocation(), item);
			}
			
		}, 1L);
	}
	
	public boolean hasSpace(Player player, ItemStack itemstack, int amount) {
		int free_space = amount;
		
		for(ItemStack item : player.getInventory()) {
			if(item == null || item.getType() == Material.AIR)
				continue;
			
			if(!item.isSimilar(itemstack))
				continue;
			
			if(item.getAmount() < item.getMaxStackSize()) {
				int cache_space = item.getMaxStackSize() - item.getAmount();
				if(free_space - cache_space <= 0) {
					return true;
				}else {
					free_space = free_space - cache_space;
				}
			}
		}
		if(free_space <= 0)
			return true;
		
		return Arrays.asList(player.getInventory().getStorageContents()).contains(null);
	}
	
	public String replace(String text, String value, String next) {
		text = text.replaceAll(value, next);
		return text;
	}
	
	public List<String> replace(List<String> list, String value, String next){
		List<String> nlist = new ArrayList<String>();
		for(String text : list) {
			nlist.add(replace(text, value, next));
		}
		return nlist;
	}
	
	public List<String> replace(List<String> list, String[] keys, String[] values){
		List<String> nlist = new ArrayList<String>();
		for(String text : list) {
			nlist.add(StringUtils.replaceEach(text, keys, values));
		}
		return nlist;
	}
	
	@SuppressWarnings("deprecation")
	public HoverEvent getHoverEvent(List<String> list) {
		TextComponent component = getTextComponent(StringUtils.join(list, System.lineSeparator()));
		return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder().append(component).create());
	}
}
