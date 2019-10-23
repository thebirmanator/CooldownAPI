package games.indigo.cooldownapi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import games.indigo.databaseconnector.DatabaseConnector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    static DatabaseConnector database;
    static final String DATABASE_NAME = "instance_testing";

    public void onEnable() {
        database = Bukkit.getServicesManager().getRegistration(DatabaseConnector.class).getProvider();

        saveDefaultConfig();

        loadData();
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "CooldownAPI enabled!");
    }

    public void onDisable() {
        saveConfig();
    }

    private void loadData() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try (Connection connection = database.getConnection(DATABASE_NAME)) {
                // TODO: IntelliJ might complain here but this is valid sql
                PreparedStatement ps = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS cooldowns (\n" +
                        "`uuid` VARCHAR(36) NOT NULL,\n" +
                        "`type` VARCHAR(50) NOT NULL,\n" +
                        "`startTime` INT(11) NOT NULL,\n" +
                        "`endTime` INT(11) NOT NULL\n" +
                        ") COLLATE='latin1_swedish_ci' ENGINE=InnoDB;"
                );

                ps.execute();

                ps = connection.prepareStatement("SELECT * FROM cooldowns");
                ResultSet results = ps.executeQuery();
                while (results.next()) {
                    String uuid = results.getString("uuid");
                    String code = results.getString("type");
                    int startTime = results.getInt("startTime");
                    int endTime = results.getInt("endTime");
                    Cooldown cooldown = new Cooldown(UUID.fromString(uuid), code, startTime, endTime);
                    if (getConfig().getBoolean("purge-expired-on-load")) {
                        if (cooldown.isExpired()) {
                            cooldown.remove();
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
