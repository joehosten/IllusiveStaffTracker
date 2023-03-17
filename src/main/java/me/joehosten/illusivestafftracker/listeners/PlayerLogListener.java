package me.joehosten.illusivestafftracker.listeners;

import games.negative.framework.db.SQLDatabase;
import me.joehosten.illusivestafftracker.Bot;
import me.joehosten.illusivestafftracker.IllusiveStaffTracker;
import me.joehosten.illusivestafftracker.core.util.DbUtils;
import me.joehosten.illusivestafftracker.core.util.DiscordSrvUtils;
import me.joehosten.illusivestafftracker.core.util.TimeUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
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

    private final HashMap<UUID, Long> map;
    private final SQLDatabase db;

    public PlayerLogListener(IllusiveStaffTracker plugin, SQLDatabase db) {
        this.map = plugin.getMap();
        this.db = db;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("illusive.staff")) return;
        clockIn(p.getUniqueId(), false);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("illusive.staff")) return;
        new PlayerAfkChangeListener(IllusiveStaffTracker.getInstance()).putIntoData(p.getUniqueId().toString(),IllusiveStaffTracker.getInstance().getAfkMap().containsKey(p.getUniqueId()) ?  IllusiveStaffTracker.getInstance().getAfkMap().get(p.getUniqueId()) : 0);
        IllusiveStaffTracker.getInstance().getAfkMap().remove(p.getUniqueId());
        clockOut(p, false);
    }

    public void clockIn(UUID p, boolean afk) {
        map.put(p, System.currentTimeMillis());
        System.out.print("clocked in " + p);

        // Discord
        LocalDate ld = LocalDate.now();
        EmbedBuilder eb = new EmbedBuilder();
        User mention = DiscordSrvUtils.getUser(p);
        String reason = !afk ? "" : " after being AFK.";
        eb.setAuthor(Bukkit.getOfflinePlayer(p).getName() + " clocked in.");
        eb.setDescription(mention.getAsMention() + " clocked in at " + TimeUtil.timeNow() + reason);
        eb.setThumbnail("https://crafatar.com/avatars/" + p + "?overlay=1");
        eb.setColor(Color.green);
        TextChannel tc = Bot.getBot().getJda().getTextChannelById("1083078866505060363");
        Objects.requireNonNull(tc).sendMessageEmbeds(eb.build()).queue();
    }

    public void clockOut(Player p, boolean afk) {
        long logoutTime = System.currentTimeMillis();
        long loginTime = map.get(p.getUniqueId());
        map.remove(p.getUniqueId());

        SQLDatabase db = IllusiveStaffTracker.getInstance().getDb();
        if (!DbUtils.existsInData(p.getUniqueId())) {
            long duration = (logoutTime - loginTime);
            try {
                PreparedStatement ps = db.statement("INSERT IGNORE INTO `staff-time-tracking` (uuid, time, afkTime) VALUES ('%1%', %2%, %3%)".replaceAll("%3%", "10").replaceAll("%1%", p.getUniqueId().toString()).replaceAll("%2%", String.valueOf(duration)));
                ps.closeOnCompletion();
                ps.executeQuery();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            String currentTime = DbUtils.getCurrentTime(p.getUniqueId().toString());
            long duration = (logoutTime - loginTime);
            long totalTime = Long.parseLong(currentTime) + duration;
            String newTime = String.valueOf(totalTime);
            try {
                PreparedStatement ps = db.statement("UPDATE `staff-time-tracking` SET time=%t% WHERE uuid='%u%'".replaceAll("%u%", String.valueOf(p.getUniqueId())).replaceAll("%t%", newTime));
                ps.closeOnCompletion();
                ps.executeQuery();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        // Discord
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(p.getName() + " clocked out.");
        eb.setThumbnail("https://crafatar.com/avatars/" + p.getUniqueId() + "?overlay=1");
        eb.setColor(Color.green);
        LocalDate ld = LocalDate.now();
        User mention = DiscordSrvUtils.getUser(p.getUniqueId());
        String reason = !afk ? "" : " due to being AFK.";
        eb.setDescription(mention.getAsMention() + " clocked out at " + TimeUtil.timeNow() + reason + "\n\nThey currently have** " + TimeUtil.format(Long.parseLong(DbUtils.getCurrentTime(p.getUniqueId().toString())), 0) + " **time online.");
        TextChannel tc = Bot.getBot().getJda().getTextChannelById("1083078866505060363");
        Objects.requireNonNull(tc).sendMessageEmbeds(eb.build()).queue();
    }

    public void saveAllPlayers() {
        for (UUID p : map.keySet()) {
            long logoutTime = System.currentTimeMillis();
            long loginTime = map.get(p);
            map.remove(p);

            if (!DbUtils.existsInData(p)) {
                long duration = (logoutTime - loginTime);
                try {
                    PreparedStatement ps = db.statement("INSERT IGNORE INTO `staff-time-tracking` (uuid, time) VALUES ('%1%', %2%)".replaceAll("%1%", p.toString()).replaceAll("%2%", String.valueOf(duration)));
                    ps.closeOnCompletion();
                    ps.executeQuery();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            } else {
                String currentTime = DbUtils.getCurrentTime(p.toString());
                long duration = (logoutTime - loginTime);
                long totalTime = Long.parseLong(currentTime) + duration;
                String newTime = String.valueOf(totalTime);
                try {
                    PreparedStatement ps = db.statement("UPDATE `staff-time-tracking` SET time=%t% WHERE uuid='%u%'".replaceAll("%u%", String.valueOf(p)).replaceAll("%t%", newTime));
                    ps.closeOnCompletion();
                    ps.executeQuery();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            if (Bukkit.getPlayer(p) != null) { // check if player is online
                clockIn(p, false);
            }
        }
        System.out.println("saved playrs");
    }
}
