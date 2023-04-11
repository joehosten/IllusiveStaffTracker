package me.joehosten.illusivestafftracker.commands;

import games.negative.framework.discord.command.SlashCommand;
import games.negative.framework.discord.command.SlashInfo;
import me.joehosten.illusivestafftracker.core.util.DbUtils;
import me.joehosten.illusivestafftracker.core.util.DiscordSrvUtils;
import me.joehosten.illusivestafftracker.core.util.TimeUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.UUID;

@SlashInfo(name = "selftimecheck", description = "Check your playtime for the week")
public class DiscordSelfCheck extends SlashCommand {
    @Override
    public void onCommand(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        User user = slashCommandInteractionEvent.getUser();
        UUID uuid = DiscordSrvUtils.getPlayer(user.getId()).getUniqueId();
        long rawPlayTime = Long.parseLong(DbUtils.getCurrentTime(uuid.toString()));
        String playTime = TimeUtil.format(rawPlayTime, 0);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Your playtime this week");
        eb.setDescription("You has actively played for **%active%**. \n\nYou need **%hours%** more hours to meet the requirement.".replaceAll("%active%", playTime).replaceAll("%hours%", TimeUtil.format(DiscordSrvUtils.calculateMissedQuota(uuid.toString()), 0).replaceAll("-", "")));
        eb.setThumbnail("https://crafatar.com/avatars/" + DiscordSrvUtils.getPlayer(user.getId()).getUniqueId() + "?overlay=1");
        slashCommandInteractionEvent.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }
}