package me.thebirmanator.cooldownapi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

import net.antfarms.serviceconnector.ServiceConnector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public static ServiceConnector sc;

	public void onEnable() {
		sc = Bukkit.getServicesManager().getRegistration(ServiceConnector.class).getProvider();
		
		saveDefaultConfig();

		loadData();
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "CooldownAPI enabled!");
	}
	
	public void onDisable() {
		saveConfig();
	}

	private void loadData() {
		try(Connection connection = sc.getSql().getConnection()) {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM cooldowns");
			ResultSet results = ps.executeQuery();
			while(results.next()) {
				String uuid = results.getString("uuid");
				String code = results.getString("type");
				int startTime = results.getInt("startTime");
				int endTime = results.getInt("endTime");
				Cooldown cooldown = new Cooldown(UUID.fromString(uuid), code, startTime, endTime);
				if(getConfig().getBoolean("purge-expired-on-load")) {
					if(cooldown.isExpired()) {
						cooldown.remove();
					}
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
