package net.darkscorner.darkscooldown;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Cooldown {
	
	private UUID uuid;
	private String code;
	private long startTime;
	private long endTime;
	
	private static Set<Cooldown> cooldowns = new HashSet<Cooldown>();
	
	private Plugin plugin;
	
	// constructor for loading from the config ONLY (don't use it otherwise)
	protected Cooldown(Plugin plugin, UUID playerUUID, String code, long startTime, long endTime) {
		this.uuid = playerUUID;
		this.code = code;
		this.startTime = startTime;
		this.endTime = endTime;
		this.plugin = plugin;
		
		cooldowns.add(this);
	}
	
	public Cooldown(Player player, String code, int duration) {
		this.uuid = player.getUniqueId();
		this.code = code;
		startTime = Instant.now().getEpochSecond();
		endTime = startTime + duration;
		
		plugin = Main.getPlugin(Main.class);
		plugin.getConfig().set("cooldowns." + code + "." + uuid + ".startTime", startTime);
		plugin.getConfig().set("cooldowns." + code + "." + uuid + ".endTime", endTime);
		
		if(getCooldown(Bukkit.getPlayer(uuid), code) != null) { // so there's no duplicate cooldowns in the set (same player with same code"
			cooldowns.remove(getCooldown(Bukkit.getPlayer(uuid), code));
		}
		cooldowns.add(this);
		
	}
	
	public UUID getPlayerUUID() {
		return uuid;
	}
	
	public String getCode() {
		return code;
	}
	
	public long getEndTime() {
		return endTime;
	}
	
	public void setDuration(int duration) {
		endTime = startTime + duration;
		
		plugin.getConfig().set("cooldowns." + code + "." + uuid + ".endTime", endTime);
	}
	
	public static Set<Cooldown> getCooldowns(Player player) {
		Set<Cooldown> playerCooldowns = new HashSet<Cooldown>();
		UUID pUUID = player.getUniqueId();
		
		for(Cooldown cooldown : cooldowns) {
			if(cooldown.getPlayerUUID().equals(pUUID)) {
				playerCooldowns.add(cooldown);
			}
		}
		
		return playerCooldowns;
	}
	
	public static Cooldown getCooldown(Player player, String code) {
		Set<Cooldown> cooldowns = getCooldowns(player);
		for(Cooldown cooldown : cooldowns) {
			if(cooldown.getCode().equals(code)) {
				return cooldown;
			}
		}
		
		return null;
	}
	
	public long getTimeRemaining() {
		long remaining = getEndTime() - Instant.now().getEpochSecond();
		return remaining;
	}
	
	public String getFormattedTimeLeft() {
		int timeLeft = (int) getTimeRemaining();
		int hours = timeLeft / 3600;
		int minutes = (timeLeft % 3600) / 60;
		int seconds = timeLeft % 60;
		String timeString = String.format("%02dh %02dm %02ds", hours, minutes, seconds);
		return timeString;
	}
	
	public void remove() {
		plugin.getConfig().set("cooldowns." + code + "." + uuid, null);
		cooldowns.remove(this);
	}
	
	public boolean isExpired() {
		if(endTime < Instant.now().getEpochSecond()) {
			return true;
		}
		return false;
	}
}
