package me.joehosten.illusivestafftracker.commands;

import games.negative.framework.db.SQLDatabase;
import games.negative.framework.discord.command.SlashCommand;
import games.negative.framework.discord.command.SlashInfo;
import me.joehosten.illusivestafftracker.Bot;
import me.joehosten.illusivestafftracker.IllusiveStaffTracker;
import me.joehosten.illusivestafftracker.core.util.DbUtils;
import me.joehosten.illusivestafftracker.core.util.DiscordSrvUtils;
import me.joehosten.illusivestafftracker.core.util.TimeUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@SlashInfo(name = "test", description = "test")
public class DiscordTestCommand extends SlashCommand {
    @Override
    public void onCommand(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        if (!slashCommandInteractionEvent.getUser().getId().equals("462296411141177364")) return;
        LocalDateTime now = LocalDateTime.now();

            // discord stuff
            StringBuilder toSend = new StringBuilder();
            ArrayList<String> missedQuota = new ArrayList<>();
            for (String uuid : DbUtils.getAllUuids()) {
                long rawPlayTime = Long.parseLong(DbUtils.getCurrentTime(uuid)) - Long.parseLong(DbUtils.getAfkTime(uuid));
                String playTime = TimeUtil.format(rawPlayTime, 0);
                String afkTime = TimeUtil.format(Long.parseLong(DbUtils.getAfkTime(uuid)), 0);
                toSend.append("- **").append(DiscordSrvUtils.getUser(UUID.fromString(uuid)).getAsMention()).append("** has actively played for **").append(playTime).append("** this week and AFK'd for **%afk%**.".replace("%afk%", afkTime)).append("\n");
                if (DiscordSrvUtils.calculateMissedQuota(uuid) != 0) missedQuota.add(uuid);
            }
            toSend.append("\n").append("Staff need to meet **six hours** of playtime before they receive a strike. \n\nBelow are the members that did not meet this requirement this week.\n\n");
            missedQuota.forEach(k -> toSend.append(DiscordSrvUtils.getUser(UUID.fromString(k)).getAsMention()).append(" - Missed quota by: **").append(TimeUtil.format(DiscordSrvUtils.calculateMissedQuota(k), 0).replaceAll("-", "")).append("**\n"));

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Weekly Summary");
            eb.setDescription(toSend);
            TextChannel tc = Bot.getBot().getJda().getTextChannelById("1083078866505060363");
            Objects.requireNonNull(tc).sendMessageEmbeds(eb.build()).queue();

            // clear the config
            SQLDatabase db = IllusiveStaffTracker.getInstance().getDb();

            try {
                PreparedStatement ps = db.statement("TRUNCATE `staff-time-tracking`");
                ps.closeOnCompletion();
                ps.executeQuery();
                System.out.println("cleared database for the new week");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            IllusiveStaffTracker.getInstance().saveConfig();

    }
}
