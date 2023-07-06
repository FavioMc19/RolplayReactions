package net.kokoricraft.actionschat.objects;

import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import net.kokoricraft.actionschat.RoleplayReactions;

public class NekoItem {
	RoleplayReactions plugin;
	
	private String section_name;
	private String name;
	private List<String> lore;
	private int custom_model_data;
	private Material material;
	private int damage;
	private int amount;
	private String player_head;
	private String player_head_uuid;
	
	public NekoItem(RoleplayReactions plugin, ConfigurationSection section) {
		this.plugin = plugin;
		section_name = section.getName();
		if(section.contains("name"))
			name = section.getString("name");
		
		if(section.contains("lore"))
			lore = section.getStringList("lore");
		
		if(section.contains("custom_model_data"))
			custom_model_data = section.getInt("custom_model_data");
		
		if(section.contains("material")) {
			try {
				material = Material.valueOf(section.getString("material").toUpperCase());
			}catch(Exception ex) {
				plugin.getLogger().warning("The \""+section.getString("material")+"\" material is not correct. config section: ["+section.getName()+"]");
				material = Material.STONE;
			}
		}
		
		if(section.contains("damage"))
			damage = section.getInt("damage");
		
		if(section.contains("amount"))
			amount = section.getInt("amount");
		
		if(section.contains("player_head"))
			player_head = section.getString("player_head");
		
		if(section.contains("player_head_uuid"))
			player_head_uuid = section.getString("player_head_uuid");
	}
	
	public ItemStack getItemStack(Player player, Player other_player, Material mat) {
		ItemStack item = new ItemStack(mat == null ? material : mat);
		
		if(item.getType() == Material.PLAYER_HEAD && player_head != null)
			item = setHead(player_head, player_head_uuid != null ? UUID.fromString(player_head_uuid) : UUID.randomUUID());
		
		try {
			if(amount != 0)
				item.setAmount(amount);
			
			ItemMeta meta = item.getItemMeta();
			
			if(damage != 0 && meta instanceof Damageable)
				((Damageable) meta).setDamage(damage);
			
			meta.setDisplayName(plugin.getUtils().color(player, other_player, name));
			
			meta.setLore(plugin.getUtils().replace(plugin.getUtils().replace(plugin.getUtils().color(player, lore), "<other_player>", other_player != null ? other_player.getName() : "<unknown player>"), "<player>", player.getName()));
			
			if(custom_model_data != 0)
				meta.setCustomModelData(custom_model_data);
			
			item.setItemMeta(meta);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return item;
	}
	
	public ItemStack getItemStack(Player player, Player other) {
		return getItemStack(player, other, null);
	}
	
	public ItemStack getItemStack(Player player) {
		return getItemStack(player, null, null);
	}
	
	public ItemStack getItemStack() {
		return getItemStack(null);
	}
	
	public String getName() {
		return section_name;
	}
	
	private ItemStack setHead(String texture, UUID uuid) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		PlayerProfile profile = Bukkit.createPlayerProfile(uuid, null);
		try {
			String url = "";
			if(texture.length() < 60) url = "http://textures.minecraft.net/texture/"+texture;
			if(!texture.contains("textures.minecraft")) {
		        url = new String(Base64.getDecoder().decode(texture));
			}
			profile.getTextures().setSkin(new URL(url));
		} catch (Exception e) {
			e.printStackTrace();
		}
		meta.setOwnerProfile(profile);
		item.setItemMeta(meta);
		return item;
	}
}
