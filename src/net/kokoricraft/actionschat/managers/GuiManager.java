package net.kokoricraft.actionschat.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kokoricraft.actionschat.RolplayReactions;
import net.kokoricraft.actionschat.objects.Action;
import net.kokoricraft.actionschat.objects.NekoGui;

public class GuiManager {
	
	RolplayReactions plugin;
	
	Map<Player, NekoGui> reactions_inventorys;
	Map<Player, NekoGui> config_inventorys;
	List<String> items_order;
	List<Action> gui_actions;
	NekoGui players;
	
	public GuiManager(RolplayReactions plugin) {
		this.plugin = plugin;
		reactions_inventorys = new HashMap<Player, NekoGui>();
		config_inventorys = new HashMap<Player, NekoGui>();
		items_order = new ArrayList<String>();
		gui_actions = new ArrayList<Action>();
	}
	
	public void openActions(Player player, Player other) {
		NekoGui gui = config_inventorys.getOrDefault(player, new NekoGui(plugin.getUtils().color("&eRolplayReactions"), plugin));
		gui.getInventory().clear();
		
		List<Action> actions = new ArrayList<Action>();
		
		for(String name : items_order) {
			Action action = plugin.getManager().actions.get(name);
			if(action == null || !action.hasEnableGui())
				continue;
			
			actions.add(action);
		}
		
		for(Action action : gui_actions) {
			if(!action.hasEnableGui() || actions.contains(action))
				continue;
			
			actions.add(action);
		}
		
		for(Action action : actions) {
			if(plugin.getManager().hasLocked(player, other, action.getName())) {
				ItemStack itemstack = plugin.getConfigManager().locked_item.getItemStack(player);
				ItemMeta meta = itemstack.getItemMeta();
				meta.setLocalizedName("locked:"+action.getName()+" "+other.getName());
				meta.setDisplayName(plugin.getUtils().replace(plugin.getUtils().replace(meta.getDisplayName(), "<action>", action.getName()), "<other_player>", other.getName()));
				meta.setLore(plugin.getUtils().replace(plugin.getUtils().replace(meta.getLore(), "<action>", action.getName()), "<other_player>", other.getName()));
				itemstack.setItemMeta(meta);
				gui.getInventory().addItem(itemstack);
			}else {
				if(action.getItemGui() == null) {
					continue;
				}
				
				ItemStack itemstack = action.getItemGui().getItemStack(player, other);
				ItemMeta meta = itemstack.getItemMeta();
				meta.setLocalizedName("action: "+action.getCommand()+" "+other.getName());
				itemstack.setItemMeta(meta);
				
				gui.getInventory().addItem(itemstack);
			}
		}
		
		player.openInventory(gui.getInventory());
		config_inventorys.put(player, gui);
	}
}
