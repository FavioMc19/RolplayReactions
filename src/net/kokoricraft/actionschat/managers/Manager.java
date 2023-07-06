package net.kokoricraft.actionschat.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import net.kokoricraft.actionschat.RoleplayReactions;
import net.kokoricraft.actionschat.enums.PlayerType;
import net.kokoricraft.actionschat.objects.Action;
import net.kokoricraft.actionschat.objects.NekoConfig;
import net.kokoricraft.actionschat.objects.NekoItem;
import net.kokoricraft.actionschat.objects.PlayBase;
import net.kokoricraft.actionschat.objects.PlayParticle;
import net.kokoricraft.actionschat.objects.PlaySound;

public class Manager {
	
	RoleplayReactions plugin;
	
	private List<PlayBase> plays;
	private Map<UUID, Long> delays;
	public Map<String, Action> actions;
	public Map<String, NekoItem> items;
	
	public Manager(RoleplayReactions plugin) {
		this.plugin = plugin;
		
		plays = new ArrayList<PlayBase>();
		delays = new HashMap<UUID, Long>();
		actions = new HashMap<String, Action>();
		items = new HashMap<String, NekoItem>();
	}
	
	BukkitTask task;
	
	private void task() {
		if(task != null)
			return;
		
		task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				if(plays.isEmpty()) {
					task.cancel();
					task = null;
					return;
				}
				
				for(int i = 0; i < plays.size(); i++) {
					PlayBase play = plays.get(i);
					if(!play.ended()) {
						if(play.getSleep() >= play.getMaxSleep()) {
							play.sendAction();
							continue;
						}
						
						if(!play.usedFirst())
							play.useFirst();
						
						play.sleep();
						continue;
					}
					plays.remove(play);
				}
			}
			
		}, 0, 1L);
	}
	
	public void sendParticle(List<Player> players, Player to, int max_amount, int max_sleep, Particle particle, double random_size) {
		PlayParticle play = new PlayParticle(to, players, max_amount, max_sleep, particle, random_size);
		plays.add(play);
		task();
	}
	
	public void sendSound(Player player, int max_amount, int max_sleep, Sound sound, float volume, float pitch) {
		PlaySound play = new PlaySound(player, max_amount, max_sleep, sound, volume, pitch);
		plays.add(play);
		task();
	}
	
	public void useAction(Player player, String message) {
		String[] data = message.split(" ");
		
		Action action = actions.get(data[0].toLowerCase());
		
		Player other_player = null;
		
		if(action.getActions() != null && !action.getActions().isEmpty() && action.getActions().get(0).equals("[togglelock]")) {
			if(data.length < 3) {
				player.sendMessage("use "+action.getCommand()+" <player name> <all/action name>");
				return;
			}
			String other = data[1];
			String lock_action = data[2];
			other_player = Bukkit.getPlayer(other);
			
			if(other == null) {
				player.sendMessage("Este jugador no esta conectado");
				return;
			}
			
			Boolean locked = !hasActionLocked(player, other_player, lock_action);
			
			setLocked(player, other_player, lock_action, locked);
			
			String lock_message = "";
			
			if(locked)
				lock_message = plugin.getConfigManager().messages_lock;
			
			if(!locked)
				lock_message = plugin.getConfigManager().messages_lock;
			
			player.sendMessage(plugin.getUtils().color(lock_message.replaceAll("<other_player", other_player.getName()).replaceAll("<action>", lock_action)));
			
			return;
		}
		
		
		if(data.length > 1 && (action.getPlayerType() == PlayerType.DUAL || action.getPlayerType() == PlayerType.OPTIONAL))
			other_player = Bukkit.getPlayer(data[1]);
		
		if(action.getPlayerType().equals(PlayerType.DUAL) && other_player == null) {
			//necesitas ingresar a un jugador
			player.sendMessage(plugin.getUtils().color(plugin.getConfigManager().messages_need_player));
			return;
		}
		
		if(!action.getDelays().isEmpty() && delays.getOrDefault(player.getUniqueId(), 0L) != 0) {
			int delay = getDelay(player, action);
			long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()-delays.get(player.getUniqueId()));
			
			if(seconds < delay) {
				player.sendMessage(plugin.getUtils().color(plugin.getConfigManager().messages_wait_delay));
				return;
			}
		}
		
		action.process(player, other_player);
		
		delays.put(player.getUniqueId(), System.currentTimeMillis());
	}
	
	private int getDelay(Player player, Action action) {
		if(plugin.getVault().hasEnable()) {
			String first_rank = plugin.getVault().getPermissions().getPrimaryGroup(player);
			
			if(action.getDelays().containsKey(first_rank))
				return action.getDelays().get(first_rank);
		}
		
		if(action.getDelays().containsKey("default"))
			return action.getDelays().get("default");
		
		return 0;
	}
	
	public ItemStack getItem(Player player, Player other_player, String name) {
		if(items.containsKey(name))
			return items.get(name).getItemStack(player, other_player);
		
		ItemStack item = new ItemStack(Material.STONE);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(plugin.getUtils().color("&cUnknown item: "+name));
		item.setItemMeta(meta);
		
		return item;
	}
	
	public boolean hasLocked(Player player, Player other, String action) {
		String player_uuid = player.getUniqueId().toString();
		String other_uuid = other.getUniqueId().toString();
		
		NekoConfig config = plugin.getConfigManager().count_config;
		
		List<String> player_locked = config.getStringList("players."+player_uuid+".locked."+action);
		List<String> other_locked = config.getStringList("players."+other_uuid+".locked."+action);
		
		List<String> player_locked_all = config.getStringList("players."+player_uuid+".locked.all");
		List<String> other_locked_all = config.getStringList("players."+other_uuid+".locked.all");
		
		if(player_locked.contains(other_uuid) || other_locked.contains(player_uuid))
			return true;
		
		if(player_locked_all.contains(other_uuid) || other_locked_all.contains(player_uuid))
			return true;
		
		return false;
	}
	
	public boolean hasActionLocked(Player player, Player other, String action) {
		String player_uuid = player.getUniqueId().toString();
		String other_uuid = other.getUniqueId().toString();
		
		NekoConfig config = plugin.getConfigManager().count_config;
		List<String> player_locked = config.getStringList("players."+player_uuid+".locked."+action);
		return player_locked.contains(other_uuid);
	}
	
	public void setLocked(Player player, Player other, String action, Boolean lock) {
		String player_uuid = player.getUniqueId().toString();
		String other_uuid = other.getUniqueId().toString();
		
		NekoConfig config = plugin.getConfigManager().count_config;
		
		List<String> lockeds = config.getStringList("players."+player_uuid+".locked."+action);
		
		if(lock) {
			lockeds.add(other_uuid);
		}else {
			lockeds.remove(other_uuid);
		}
		
		config.set("players."+player_uuid+".locked."+action, lockeds);
		config.saveConfig();
	}
}
