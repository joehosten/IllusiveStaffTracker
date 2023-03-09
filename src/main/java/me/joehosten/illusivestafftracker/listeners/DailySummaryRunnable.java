package me.joehosten.illusivestafftracker.listeners;

import games.negative.framework.db.SQLDatabase;
import me.joehosten.illusivestafftracker.Bot;
import me.joehosten.illusivestafftracker.IllusiveStaffTracker;
import me.joehosten.illusivestafftracker.core.util.DbUtils;
import me.joehosten.illusivestafftracker.core.util.DiscordSrvUtils;
import me.joehosten.illusivestafftracker.core.util.TimeUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class DailySummaryRunnable extends BukkitRunnable {
    @Override
    public void run() {
        LocalDateTime now = LocalDateTime.now();

        if (now.getHour() == 11) {
            // discord stuff
            StringBuilder toSend = new StringBuilder();
            ArrayList<String> missedQuota = new ArrayList<>();
            for (String uuid : DbUtils.getAllUuids()) {
                long rawPlayTime = Long.parseLong(DbUtils.getCurrentTime(uuid)) - Long.parseLong(DbUtils.getAfkTime(uuid));
                String playTime = TimeUtil.format(rawPlayTime, 0);
                String afkTime = TimeUtil.format(Long.parseLong(DbUtils.getAfkTime(uuid)), 0);
                toSend.append("- **").append(DiscordSrvUtils.getUser(UUID.fromString(uuid)).getAsMention()).append("** has actively played **").append(playTime).append("** so far this week and AFK'd for **%afk%**.".replace("%afk%", afkTime)).append("\n");
                if (DiscordSrvUtils.calculateMissedQuota(uuid) != 0) missedQuota.add(uuid);
            }
            toSend.append("\n").append("Staff need to meet **six hours** of playtime before they receive a strike. \n\nBelow are the members that have not met this requirement yet.\n\n");
            missedQuota.forEach(k -> toSend.append(DiscordSrvUtils.getUser(UUID.fromString(k)).getAsMention()).append(" - Missed quota by: **").append(TimeUtil.format(DiscordSrvUtils.calculateMissedQuota(k), 0).replaceAll("-", "")).append("**\n"));

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Daily Summary");
            eb.setDescription(toSend);
            TextChannel tc = Bot.getBot().getJda().getTextChannelById("1014041763037581342");
            Objects.requireNonNull(tc).sendMessageEmbeds(eb.build()).queue();
        }
    }
}
