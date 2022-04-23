package me.joehosten.illusivestafftracker.commands;

import games.negative.framework.command.Command;
import games.negative.framework.command.annotation.CommandInfo;
import games.negative.framework.message.Message;
import me.joehosten.illusivestafftracker.IllusiveStaffTracker;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

@CommandInfo(name = "staffcheck", aliases = {"timecheck"}, permission = "illusive.command.staffcheck", args = "player", description = "Check the playtime of a staff member for the current week.")
public class CommandCheck extends Command {

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            new Message("&cUsage: /staffcheck <player>").send(sender);
            return;
        }

        new Message("&3" + args[0] + "'s playtime so far for this week: &b%playtime%.").replace("%playtime%", getPlayTime(Bukkit.getOfflinePlayer(args[0]))).send(sender);
    }

    private String getPlayTime(OfflinePlayer p) {
        FileConfiguration staff = IllusiveStaffTracker.getInstance().getConfig();
        long time = staff.getLong(String.valueOf(p.getUniqueId()));
        return IllusiveStaffTracker.getInstance().convertTime(time);
    }
}
