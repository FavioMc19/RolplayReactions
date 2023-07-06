package net.kokoricraft.actionschat.hooks;

import org.bukkit.plugin.RegisteredServiceProvider;

import net.kokoricraft.actionschat.RoleplayReactions;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultHook {
	
	RoleplayReactions plugin;
	
	public VaultHook(RoleplayReactions plugin) {
		this.plugin = plugin;
		hasVault = initHooks();
	}
	
	public boolean hasVault;
	private Economy econ;
	private Permission perms;
	
	public boolean initHooks() {
		boolean enable = setupEconomy();
		
		setupPermissions();
		
		return enable;
	}
	
	public Economy getEconomy() {
		return econ;
	}
	
	public Permission getPermissions() {
		return perms;
	}
	
	public boolean hasEnable() {
		return hasVault;
	}
	
	private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        
        if (rsp == null)
            return false;
        
        econ = rsp.getProvider();
        
        return econ != null;
    }
	
	private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
}
