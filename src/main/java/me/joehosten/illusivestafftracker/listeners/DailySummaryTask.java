package me.joehosten.illusivestafftracker.listeners;

import me.joehosten.illusivestafftracker.IllusiveStaffTracker;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.UUID;

public class DailySummaryTask extends BukkitRunnable {

    private final IllusiveStaffTracker plugin;

    public DailySummaryTask(IllusiveStaffTracker plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // discord stuff
        StringBuilder toSend = new StringBuilder();
        for (String uuid : IllusiveStaffTracker.getInstance().getConfig().getKeys(false)) {
            toSend.append("- **").append(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()).append("** has played for **").append(plugin.convertTime(IllusiveStaffTracker.getInstance().getConfig().getLong(uuid))).append("** this week\n");
        }
        System.out.println(toSend);
        IllusiveStaffTracker.getInstance().sendEmbed(Bukkit.getOfflinePlayer("hypews"), "**DAILY SUMMARY**\n" + toSend, false, Color.GRAY);
    }
}
