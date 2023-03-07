package me.joehosten.illusivestafftracker.listeners;

import games.negative.framework.db.SQLDatabase;
import me.joehosten.illusivestafftracker.Bot;
import me.joehosten.illusivestafftracker.IllusiveStaffTracker;
import me.joehosten.illusivestafftracker.core.util.DbUtils;
import me.joehosten.illusivestafftracker.core.util.TimeUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PlayerLogListener implements Listener {

    private final IllusiveStaffTracker plugin;
    private final HashMap<UUID, Long> map;
    private final SQLDatabase db;

    public PlayerLogListener(IllusiveStaffTracker plugin, SQLDatabase db) {
        this.plugin = plugin;
        this.map = plugin.getMap();
        this.db = db;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("illusive.staff")) return;
        clockIn(p.getUniqueId());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("illusive.staff")) return;
        clockOut(p);
    }

    private Long getTimeFromConfig(UUID p) {
        FileConfiguration config = IllusiveStaffTracker.getInstance().getConfig();
        return config.getLong(String.valueOf(p));
    }

    private void clockIn(UUID p) {
        map.put(p, System.currentTimeMillis());
        System.out.print("clocked in " + p);

        // Discord
        LocalDate ld = LocalDate.now();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription(Bukkit.getPlayer(p).getName() + " clocked in at " + ld.getDayOfMonth() + "/" + ld.getMonthValue() + "/" + ld.getYear() + TimeUtil.format(System.currentTimeMillis(), 0) + " (day/month/year)");
        eb.setThumbnail("https://crafatar.com/avatars/" + p + "?overlay=1");
        eb.setColor(Color.green);
        eb.setFooter(ld.getDayOfMonth() + "/" + ld.getMonthValue() + "/" + ld.getYear() + " (day/month/year)");
        TextChannel tc = Bot.getBot().getJda().getTextChannelById("1014041763037581342");
        Objects.requireNonNull(tc).sendMessageEmbeds(eb.build()).queue();
    }

    private void clockOut(Player p) {
        long logoutTime = System.currentTimeMillis();
        long loginTime = map.get(p.getUniqueId());
        map.remove(p.getUniqueId());

        SQLDatabase db = IllusiveStaffTracker.getInstance().getDb();
        try {
            String currentTime = DbUtils.getCurrentTime(p.getUniqueId());
            String newTime = logoutTime - loginTime + currentTime;
            String statement = DbUtils.existsInData(p.getUniqueId()) ? "UPDATE `staff-time-tracking` SET time=%t% WHERE uuid='%u%'".replaceAll("%t%", String.valueOf(p)).replaceAll("%t%", newTime) : "INSERT IGNORE INTO `staff-time-tracking` (uuid, time) VALUES ('%1%', %2%)".replaceAll("%1%", String.valueOf(p.getUniqueId())).replaceAll("%2%", String.valueOf(logoutTime - loginTime));
            PreparedStatement ps = db.statement(statement);
            ps.closeOnCompletion();
            ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Discord
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(p.getName() + " clocked out.");
        eb.setThumbnail("https://crafatar.com/avatars/" + p.getUniqueId() + "?overlay=1");
        eb.setColor(Color.green);
        LocalDate ld = LocalDate.now();
        eb.setDescription(p.getName() + " clocked in at " + ld.getDayOfMonth() + "/" + ld.getMonthValue() + "/" + ld.getYear() + TimeUtil.format(System.currentTimeMillis(), 0) + " (day/month/year)");
        TextChannel tc = Bot.getBot().getJda().getTextChannelById("1014041763037581342");
        Objects.requireNonNull(tc).sendMessageEmbeds(eb.build()).queue();
    }

    public void saveAllPlayers() {
        for (UUID p : map.keySet()) {
            long logoutTime = System.currentTimeMillis();
            long loginTime = map.get(p);
            map.remove(p);

            try {
                String currentTime = DbUtils.getCurrentTime(p);
                String newTime = logoutTime - loginTime + currentTime;
                String statement = DbUtils.existsInData(p) ? "UPDATE `staff-time-tracking` SET time=%t% WHERE uuid=%u%".replaceAll("%t%", String.valueOf(p)).replaceAll("%t%", newTime) : "INSERT IGNORE INTO `staff-time-tracking` (uuid, time) VALUES (%1%, %2%)".replaceAll("%1%", String.valueOf(p)).replaceAll("%2%", String.valueOf(logoutTime - loginTime));
                PreparedStatement ps = db.statement(statement);
                ps.closeOnCompletion();
                ps.executeQuery();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (Bukkit.getPlayer(p) != null) { // check if player is online
                clockIn(p);
            }
        }
        System.out.println("saved playrs");
    }
}
