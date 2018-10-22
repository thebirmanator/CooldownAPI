package net.darkscorner.darkscooldown;

import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public void onEnable() {
		
		saveDefaultConfig();

		Set<String> cmdCodes = getConfig().getConfigurationSection("cooldowns").getKeys(false); 
		for(String code : cmdCodes) {
			
			Set<String> stringUUIDs = getConfig().getConfigurationSection("cooldowns." + code).getKeys(false);
			for(String stringUUID : stringUUIDs) {
				UUID uuid = UUID.fromString(stringUUID);
				String stringStart = getConfig().getString("cooldowns." + code + "." + uuid + ".startTime");
				long start = Long.parseLong(stringStart);
				String stringEnd = getConfig().getString("cooldowns." + code + "." + uuid + ".endTime");
				long end = Long.parseLong(stringEnd);
				Cooldown cooldown = new Cooldown(getPlugin(this.getClass()), uuid, code, start, end);
				if(getConfig().getBoolean("purge-expired-on-load")) {
					if(cooldown.isExpired()) {
						cooldown.remove();
					}
				}
			}
		}
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "DarksCooldownAPI enabled!");
	}
	
	public void onDisable() {
		saveConfig();
	}
}
