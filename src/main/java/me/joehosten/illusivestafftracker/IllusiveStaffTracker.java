package me.joehosten.illusivestafftracker;

import games.negative.framework.BasePlugin;
import lombok.Getter;
import lombok.Setter;
import me.joehosten.illusivestafftracker.commands.CommandCheck;
import me.joehosten.illusivestafftracker.listeners.DateCheckRunnable;
import me.joehosten.illusivestafftracker.listeners.PlayerLogListener;
import me.joehosten.illusivestafftracker.listeners.PlayerSaveTask;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public final class IllusiveStaffTracker extends BasePlugin {

    @Getter
    @Setter
    private static IllusiveStaffTracker instance;

    @Getter
    @Setter
    private TextChannel textChannel;

    @Override
    public void onEnable() {
        setInstance(this);

        getConfig().options().copyDefaults();
        saveConfig();

        registerListeners(new PlayerLogListener());
        registerCommands(new CommandCheck());

        new DateCheckRunnable().runTaskTimerAsynchronously(this, 0L, 3600L * 20L);
        new PlayerSaveTask().runTaskTimerAsynchronously(this, 0L, 600L * 20L);

        // Discord
        String botToken = "NzAyNjc3NDI2ODE5NDMyNDc5.XqDhWQ.wwE5WFgJAK-6z5Gd8mlKiWLzFCo";
        String textChannelId = "965844461772996628";
        JDA jda;
        try {
            jda = JDABuilder.createDefault(botToken).build().awaitReady();
        } catch (InterruptedException | LoginException e) {
            throw new RuntimeException(e);
        }
        textChannel = jda.getTextChannelById(textChannelId);

        sendEmbed(Bukkit.getOfflinePlayer("hypews"), "Bot online", true, Color.RED);
    }

    @Override
    public void onDisable() {
        new PlayerLogListener().saveAllPlayers();
    }

    public void sendEmbed(OfflinePlayer player, String content, boolean contentAuthorLine, Color color) {
        if (textChannel == null) return;

        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor(contentAuthorLine ? content : player.getName(),
                        null,
                        "https://crafatar.com/avatars/" + player.getUniqueId().toString() + "?overlay=1");
        builder.setColor(color);
        if (!contentAuthorLine) {
            builder.setDescription(content);
        }
        LocalDate ld = LocalDate.now();
        builder.setFooter(ld.getDayOfMonth() + "/" + ld.getMonthValue() + "/" + ld.getYear() + " (day/month/year)");

        textChannel.sendMessageEmbeds(builder.build()).queue();
    }
}
