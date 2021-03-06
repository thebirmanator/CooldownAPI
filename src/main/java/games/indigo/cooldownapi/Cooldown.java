package games.indigo.cooldownapi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Cooldown {

    private UUID uuid;
    private String code;
    private long startTime;
    private long endTime;

    private static Set<Cooldown> cooldowns = new HashSet<>();

    private Plugin plugin;

    /**
     * Cooldown constructor for loading from the config ONLY (don't use it otherwise)
     *
     * @param playerUUID The UUID of the player
     * @param code       The cooldown id
     * @param startTime  The start time of the cooldown
     * @param endTime    The end time of the cooldown
     */
    protected Cooldown(UUID playerUUID, String code, long startTime, long endTime) {
        this.uuid = playerUUID;
        this.code = code;
        this.startTime = startTime;
        this.endTime = endTime;
        plugin = Main.getPlugin(Main.class);

        cooldowns.add(this);
    }

    /**
     * Cooldown constructor for creating a cooldown on a player
     *
     * @param player   The player to use
     * @param code     The id of the cooldown
     * @param duration The duration of the cooldown
     */
    public Cooldown(Player player, String code, int duration) {
        this.uuid = player.getUniqueId();
        this.code = code;
        startTime = Instant.now().getEpochSecond();
        endTime = startTime + duration;

        plugin = Main.getPlugin(Main.class);

        boolean replaceData = false;
        if (getCooldown(Bukkit.getPlayer(uuid), code) != null) { // so there's no duplicate cooldowns in the set (same player with same code)
            cooldowns.remove(getCooldown(Bukkit.getPlayer(uuid), code));
            replaceData = true;
        }
        cooldowns.add(this);

        addToDatabase(replaceData);

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

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try (Connection connection = Main.database.getConnection(Main.DATABASE_NAME)) {
                    PreparedStatement ps = connection.prepareStatement("UPDATE cooldowns SET endTime = " + endTime + " WHERE uuid = '" + uuid.toString() + "' AND type = '" + code + "';");
                    ps.execute();
                    ps.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static Set<Cooldown> getCooldowns(Player player) {
        Set<Cooldown> playerCooldowns = new HashSet<Cooldown>();
        UUID pUUID = player.getUniqueId();

        for (Cooldown cooldown : cooldowns) {
            if (cooldown.getPlayerUUID().equals(pUUID)) {
                playerCooldowns.add(cooldown);
            }
        }

        return playerCooldowns;
    }

    public static Cooldown getCooldown(Player player, String code) {
        Set<Cooldown> cooldowns = getCooldowns(player);
        for (Cooldown cooldown : cooldowns) {
            if (cooldown.getCode().equals(code)) {
                return cooldown;
            }
        }

        return null;
    }

    public long getTimeRemaining() {
        return getEndTime() - Instant.now().getEpochSecond();
    }

    public String getFormattedTimeLeft() {
        int timeLeft = (int) getTimeRemaining();
        int hours = timeLeft / 3600;
        int minutes = (timeLeft % 3600) / 60;
        int seconds = timeLeft % 60;

        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }

    public void remove() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try (Connection connection = Main.database.getConnection(Main.DATABASE_NAME)) {
                    PreparedStatement ps = connection.prepareStatement("DELETE FROM cooldowns WHERE uuid = '" + uuid + "' AND type = '" + code + "';");
                    ps.execute();
                    ps.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        cooldowns.remove(this);
    }

    public boolean isExpired() {
        return endTime < Instant.now().getEpochSecond();
    }

    private void addToDatabase(boolean replacing) {
        String sqlString = "INSERT INTO cooldowns (UUID, type, startTime, endTime) VALUES ('" + uuid + "', '" + code + "', '" + startTime + "', '" + endTime + "');";
        if (replacing) {
            sqlString = "UPDATE cooldowns SET startTime = " + startTime + ", endTime = " + endTime + " WHERE uuid = '" + uuid.toString() + "' AND type = '" + code + "';";
        }
        final String sql = sqlString;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try (Connection connection = Main.database.getConnection(Main.DATABASE_NAME)) {
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.execute();
                    ps.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
