package me.joehosten.illusivestafftracker.listeners;

import org.bukkit.scheduler.BukkitRunnable;

public class DailySummaryTask extends BukkitRunnable {

    @Override
    public void run() {
        // discord stuff
//        StringBuilder toSend = new StringBuilder();
//        for (String uuid : IllusiveStaffTracker.getInstance().getConfig().getKeys(false)) {
//            toSend.append("- **").append(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()).append("** has played for **").append(plugin.convertTime(IllusiveStaffTracker.getInstance().getConfig().getLong(uuid))).append("** this week\n");
//        }
//        System.out.println(toSend);
//        IllusiveStaffTracker.getInstance().sendEmbed(Bukkit.getOfflinePlayer("hypews"), "**DAILY SUMMARY**\n" + toSend, false, Color.GRAY);
    }
}
