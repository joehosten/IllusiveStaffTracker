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
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DateCheckRunnable extends BukkitRunnable {
    private final List<String> toSend = new ArrayList<>();

    @Override
    public void run() {
        LocalDateTime now = LocalDateTime.now();

        if (now.getDayOfWeek() == DayOfWeek.SUNDAY && now.getHour() == 23 && now.getMinute() == 1) {
            // discord stuff
            StringBuilder toSend = new StringBuilder();
            ArrayList<String> missedQuota = new ArrayList<>();
            for (String uuid : DbUtils.getAllUuids()) {
                long rawPlayTime = Long.parseLong(DbUtils.getCurrentTime(uuid)) - Long.parseLong(DbUtils.getAfkTime(uuid));
                String playTime = TimeUtil.format(rawPlayTime, 0, true);
                String afkTime = TimeUtil.format(Long.parseLong(DbUtils.getAfkTime(uuid)), 0, true);
                toSend.append("- **").append(DiscordSrvUtils.getUser(UUID.fromString(uuid)).getAsMention()).append("** has actively played for **").append(playTime).append(afkTime.length() == 0 ? "** this week." : "** this week and AFK'd for **%afk%**.".replace("%afk%", afkTime)).append("\n");
                if (DiscordSrvUtils.calculateMissedQuota(uuid) != 0) missedQuota.add(uuid);
            }
            toSend.append("\n").append("Staff need to meet **six hours** of playtime before they receive a strike. \n\nBelow are the members that have not met this requirement.\n\n");
            missedQuota.forEach(k -> toSend.append(DiscordSrvUtils.getUser(UUID.fromString(k)).getAsMention()).append(" - Missed quota by: **%missed%** \n".replaceAll("%missed%", TimeUtil.format(DiscordSrvUtils.calculateMissedQuota(k), 0).replaceAll("-", ""))));

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Weekly Summary");
            eb.setDescription(toSend);
            TextChannel tc = Bot.getBot().getJda().getTextChannelById("1014041763037581342");
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

            new PlayerLogListener(IllusiveStaffTracker.getInstance(), db).saveAllPlayers();
        }
    }

}
