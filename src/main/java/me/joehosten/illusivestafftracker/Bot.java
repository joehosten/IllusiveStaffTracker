package me.joehosten.illusivestafftracker;

import games.negative.framework.discord.DiscordBot;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.joehosten.illusivestafftracker.commands.DiscordCommandCheck;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.List;

public class Bot extends DiscordBot {

    @Getter
    @Setter
    private static Bot bot;
    @Getter
    @Setter
    private JDA jda;

    @SneakyThrows
    public Bot() {
        setBot(this);
        String botToken = IllusiveStaffTracker.getInstance().getConfig().getString("bot.token");
        JDABuilder builder = create(botToken, List.of(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_PRESENCES));
        jda = builder.build().awaitReady();
        registerGlobalCommand(new DiscordCommandCheck());
        initializeCommands(jda);
    }


}
