package me.joehosten.illusivestafftracker.commands.discord;

import me.joehosten.illusivestafftracker.IllusiveStaffTracker;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class DiscordCommandCheckLegacy extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        List<String> ignored = Arrays.asList("462296411141177364", "918054245184450600", "918054242026131467");
        if (ignored.contains(e.getMessage().getId())) return;
        if (e.getAuthor().isBot()) return;


        List<String> command = Arrays.asList(e.getMessage().getContentRaw().split(" "));
        if (!command.get(0).equalsIgnoreCase("-staffcheck")) return;


        List<String> allowed = Arrays.asList("462296411141177364", "321737250524102677", "117396465017421827");
        if (!allowed.contains(e.getAuthor().getId())) {
            e.getChannel().sendMessage("<@" + e.getAuthor().getId() + ">, you cannot issue this command!").queue();
            return;
        }


        String name = command.get(1);
        OfflinePlayer p = Bukkit.getOfflinePlayer(name);
        FileConfiguration config = IllusiveStaffTracker.getInstance().getConfig();
        String time = IllusiveStaffTracker.getInstance().convertTime(config.getLong(String.valueOf(p.getUniqueId())));

        e.getChannel().sendMessage("**" + p.getName() + "** has **" + time + "** logged this week.").queue();
    }
}
