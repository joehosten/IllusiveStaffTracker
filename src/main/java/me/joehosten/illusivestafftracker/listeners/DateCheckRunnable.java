package me.joehosten.illusivestafftracker.listeners;

import me.joehosten.illusivestafftracker.IllusiveStaffTracker;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.List;
import java.util.*;

public class DateCheckRunnable extends BukkitRunnable {

    private final List<String> toSend = new ArrayList<>();

    @Override
    public void run() {

        // discord stuff
        StringBuilder toSend = new StringBuilder();
        for (String uuid : IllusiveStaffTracker.getInstance().getConfig().getKeys(false)) {
            toSend.append("- **").append(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()).append("** has played for **").append(convertTime(IllusiveStaffTracker.getInstance().getConfig().getLong(uuid))).append("** this week\n");
        }
        System.out.println(toSend);
        IllusiveStaffTracker.getInstance().sendEmbed(Bukkit.getPlayer("hypews"), String.valueOf(toSend), false, Color.GRAY);

        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date

        if (date.getDay() == 1 && calendar.get(Calendar.HOUR_OF_DAY) == 22) {
            for (String key : IllusiveStaffTracker.getInstance().getConfig().getKeys(false)) {
                IllusiveStaffTracker.getInstance().getConfig().set(key, null);
            }
            IllusiveStaffTracker.getInstance().saveConfig();


        }
    }

    private String convertTime(Long ms) {
        long minutes = (ms / 1000) / 60;
        long seconds = (ms / 1000) % 60;

        return minutes + " minutes and "
                + seconds + " seconds";

    }
}
