package net.kokoricraft.actionschat.objects;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import net.kokoricraft.actionschat.RolplayReactions;

public class NekoGui implements InventoryHolder{
	
	private Inventory inventory;
	RolplayReactions plugin;
	
	public NekoGui(String name, RolplayReactions plugin) {
		this.plugin = plugin;
		this.inventory = Bukkit.createInventory(this, 54, plugin.getUtils().color(name));
	}
	
	@Override
	public Inventory getInventory() {
		return inventory;
	}

}
