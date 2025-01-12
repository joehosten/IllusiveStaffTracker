package me.joehosten.illusivestafftracker.commands;

import games.negative.framework.discord.command.SlashCommand;
import games.negative.framework.discord.command.SlashInfo;
import me.joehosten.illusivestafftracker.Bot;
import me.joehosten.illusivestafftracker.core.util.DbUtils;
import me.joehosten.illusivestafftracker.core.util.DiscordSrvUtils;
import me.joehosten.illusivestafftracker.core.util.TimeUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@SlashInfo(name = "test", description = "test")
public class DiscordTestCommand extends SlashCommand {
    @Override
    public void onCommand(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        if (!slashCommandInteractionEvent.getUser().getId().equals("462296411141177364")) return;
        StringBuilder toSend = new StringBuilder();
        ArrayList<String> missedQuota = new ArrayList<>();
        for (String uuid : DbUtils.getAllUuids()) {
            long rawPlayTime = Long.parseLong(DbUtils.getCurrentTime(uuid));
            String playTime = TimeUtil.format(rawPlayTime, 0, true);
            toSend.append("- **").append(DiscordSrvUtils.getUser(UUID.fromString(uuid)).getAsMention()).append("** has actively played for **").append(playTime).append("\n");
            if (DiscordSrvUtils.calculateMissedQuota(uuid) != 0) missedQuota.add(uuid);
        }
        toSend.append("\n").append("Staff need to meet **six hours** of playtime before they receive a strike. \n\nBelow are the members that have not met this requirement yet.\n\n");
        missedQuota.forEach(k -> toSend.append(DiscordSrvUtils.getUser(UUID.fromString(k)).getAsMention()).append(" - Missed quota by: **%missed%** \n".replaceAll("%missed%", TimeUtil.format(DiscordSrvUtils.calculateMissedQuota(k), 0).replaceAll("-", ""))));

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Daily Summary");
        eb.setDescription(toSend);
        TextChannel tc = Bot.getBot().getJda().getTextChannelById("1087875275343278080");
        Objects.requireNonNull(tc).sendMessageEmbeds(eb.build()).queue();
    }
}

