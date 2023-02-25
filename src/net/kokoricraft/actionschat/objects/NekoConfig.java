package net.kokoricraft.actionschat.objects;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


public class NekoConfig {
	
	private File configFile;
	private FileConfiguration config;
	private Boolean needUpdate;
	private Boolean hasDefault;
	
	public NekoConfig(String path, JavaPlugin plugin) {
		needUpdate = false;
		
		configFile = new File(plugin.getDataFolder() + File.separator+ path);
		config = YamlConfiguration.loadConfiguration(configFile);
		
		if(!configFile.exists()) {
			try {
				plugin.saveResource(path, false);
				config = YamlConfiguration.loadConfiguration(configFile);
				hasDefault = true;
			}catch(Exception ex) {}
		}else {
			hasDefault = false;
		}
	}
	
	public Object get(String path, Object def) {
		if(!config.contains(path) && def != null) {
			config.set(path, def);
			this.needUpdate = true;
		}
		return config.get(path, def);
	}
	
	public String getString(String path, String def) {
		if(!config.contains(path) && def != null) {
			config.set(path, def);
			this.needUpdate = true;
		}
		return config.getString(path, def);
	}
	
	public int getInt(String path, int def) {
		if(!config.contains(path)) {
			config.set(path, def);
			this.needUpdate = true;
		}
		return config.getInt(path, def);
	}
	
	public double getDouble(String path, double def) {
		if(!config.contains(path)) {
			config.set(path, def);
			this.needUpdate = true;
		}
		return config.getDouble(path, def);
	}
	
	public List<String> getStringList(String path, List<String> def){
		if(!config.contains(path) && def != null) {
			config.set(path, def);
			this.needUpdate = true;
		}
		return config.getStringList(path);
	}
	
	public ConfigurationSection getConfigurationSection(String path) {
		return config.getConfigurationSection(path);
	}
	
	public Boolean isConfigurationSection(String path) {
		return config.isConfigurationSection(path);
	}
	
	public Boolean getBoolean(String path, Boolean def) {
		if(!config.contains(path) && def != null) {
			config.set(path, def);
			this.needUpdate = true;
		}
		return config.getBoolean(path, def);
	}
	
	public String getString(String path) {
		return config.getString(path);
	}
		
	public int getInt(String path) {
		return config.getInt(path);
	}
	
	public double getDouble(String path) {
		return config.getDouble(path);
	}
	
	public Boolean getBoolean(String path) {
		return config.getBoolean(path);
	}
	
	public List<String> getStringList(String path){
		return config.getStringList(path);
	}
	
	public ItemStack getItemStack(String path) {
		return config.getItemStack(path);
	}
	
	public void set(String path, Object object) {
		config.set(path, object);
	}
	
	public Boolean contains(String paht) {
		return config.contains(paht);
	}
	
	public void update() {
		if(needUpdate) {
			saveConfig();
			this.needUpdate = false;
		}
	}
	
	public void forceUpdate() {
		saveConfig();
		this.needUpdate = false;
	}
	
	public Boolean hasDefault() {
		return this.hasDefault;
	}
	
	public FileConfiguration getConfig() {
		return this.config;
	}
	
	public void saveConfig() {
        try {
            this.config.save(this.configFile);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
