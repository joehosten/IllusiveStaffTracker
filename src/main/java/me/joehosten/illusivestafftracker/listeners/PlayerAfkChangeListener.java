package me.joehosten.illusivestafftracker.listeners;

import games.negative.framework.db.SQLDatabase;
import me.joehosten.illusivestafftracker.IllusiveStaffTracker;
import me.joehosten.illusivestafftracker.core.util.DbUtils;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerAfkChangeListener implements Listener {

    private final HashMap<UUID, Long> map;

    public PlayerAfkChangeListener(IllusiveStaffTracker plugin) {
        this.map = plugin.getAfkMap();
    }

    @EventHandler
    public void onAfkChange(AfkStatusChangeEvent e) {
        Player p = Bukkit.getPlayer(e.getAffected().getUUID());
        boolean afk = !e.getAffected().isAfk(); // flipping this because the return of isAfk is flipped: If player goes afk, it returns false for some reason
        assert p != null;
        if (!p.hasPermission("illusive.staff")) return;
        if (afk) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    map.put(p.getUniqueId(), System.currentTimeMillis());
                    System.out.println("10min");
                }
            }.runTaskLaterAsynchronously(IllusiveStaffTracker.getInstance(), 600L * 20L);
            System.out.println("player afk, putting map");
        } else {
            putIntoData(p.getUniqueId().toString(), map.get(p.getUniqueId()));
            map.remove(p.getUniqueId());

            System.out.println("player no longer afk, removing map and putting sql");
        }

    }

    private void putIntoData(String uuid, long time) {
        SQLDatabase db = IllusiveStaffTracker.getInstance().getDb();
        long afkOutTime = System.currentTimeMillis();
        try {
            String currentTimeStr = DbUtils.getAfkTime(uuid);
            long currentTime = Long.parseLong(currentTimeStr);
            long duration = (afkOutTime - time);
            long totalTime = currentTime + duration;
            String newTime = String.valueOf(totalTime);
            String statement = "UPDATE `staff-time-tracking` SET afkTime=%t% WHERE uuid='%u%'".replaceAll("%u%", uuid).replaceAll("%t%", newTime);
            PreparedStatement ps = db.statement(statement);
            ps.closeOnCompletion();
            ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private BukkitRunnable runnable(Player player) {
        return new BukkitRunnable() {
            @Override
            public void run() {

            }
        };
    }
}
