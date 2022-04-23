package me.joehosten.illusivestafftracker.listeners;

import me.joehosten.illusivestafftracker.IllusiveStaffTracker;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class DiscordBotPingEvent extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (IllusiveStaffTracker.getInstance().checkMemberRoles(Objects.requireNonNull(e.getMember()), "staff") == null) return;
        Message message = e.getMessage();
        if (message.getMentionedUsers().contains(e.getJDA().getSelfUser())) {
            e.getMessage().reply("Use `-help` to get a list of commands.").queue();
        }
    }
}
