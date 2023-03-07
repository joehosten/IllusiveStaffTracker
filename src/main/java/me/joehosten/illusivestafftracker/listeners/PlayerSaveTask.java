package me.joehosten.illusivestafftracker.listeners;

import me.joehosten.illusivestafftracker.IllusiveStaffTracker;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerSaveTask extends BukkitRunnable {
    @Override
    public void run() {
        new PlayerLogListener(IllusiveStaffTracker.getInstance(), IllusiveStaffTracker.getInstance().getDb()).saveAllPlayers();
        System.out.println("saved all players");
    }
}
