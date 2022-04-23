package me.joehosten.illusivestafftracker.listeners;

import org.bukkit.scheduler.BukkitRunnable;

public class PlayerSaveTask extends BukkitRunnable {
    @Override
    public void run() {
        new PlayerLogListener().saveAllPlayers();
        System.out.println("saved all players");
    }
}
