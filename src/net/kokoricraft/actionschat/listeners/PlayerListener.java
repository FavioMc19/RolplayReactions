package net.kokoricraft.actionschat.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import net.kokoricraft.actionschat.RoleplayReactions;
import net.kokoricraft.actionschat.objects.NekoGui;

public class PlayerListener implements Listener{
	
	RoleplayReactions plugin;
	
	public PlayerListener(RoleplayReactions plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		if(event.isCancelled())
			return;
		
		String[] data = event.getMessage().toLowerCase().split(" ");
		
		if(plugin.getManager().actions.containsKey(data[0])) {
			plugin.getManager().useAction(event.getPlayer(), event.getMessage());
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getClickedInventory() == null)
			return;
		
		if(!(event.getClickedInventory().getHolder() instanceof NekoGui))
			return;
		
		event.setCancelled(true);
		
		ItemStack itemstack = event.getCurrentItem();
		
		if(itemstack == null || itemstack.getType() == Material.AIR)
			return;
		
		Player player = (Player)event.getWhoClicked();
		
		String data = itemstack.getItemMeta().getLocalizedName();
		
		if(data.startsWith("action: ")) {
			String action = data.replaceAll("action: ", "");
			plugin.getManager().useAction(player, action);
			player.closeInventory();
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onPlayerInteract(PlayerInteractEntityEvent event) {
		if(!(event.getRightClicked() instanceof Player))
			return;
		
		Player player = event.getPlayer();
		Player other = (Player)event.getRightClicked();
		
		if(!player.isSneaking())
			return;
		
		if(!plugin.getConfigManager().gui_enabled)
			return;
		
		plugin.getGuiManager().openActions(player, other);
		event.setCancelled(true);
	}
}
