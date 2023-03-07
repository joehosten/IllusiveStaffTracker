package me.joehosten.illusivestafftracker.commands;

import games.negative.framework.discord.command.SlashCommand;
import games.negative.framework.discord.command.SlashInfo;
import me.joehosten.illusivestafftracker.core.util.DbUtils;
import me.joehosten.illusivestafftracker.core.util.DiscordSrvUtils;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Objects;

@SlashInfo(name = "staffcheck", description = "Check a player's staff time")
public class DiscordCommandCheck extends SlashCommand {
    public DiscordCommandCheck() {
        setData(k -> {
            k.addOption(OptionType.USER, "user", "Please select the user", true);
        });
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        User user = Objects.requireNonNull(slashCommandInteractionEvent.getOption("user")).getAsUser();
//        DbUtils.getCurrentTime(DiscordSrvUtils.getPlayer(user.getId()).getUniqueId());
        slashCommandInteractionEvent.reply(DbUtils.getCurrentTime(DiscordSrvUtils.getPlayer(user.getId()).getUniqueId()) + " success").queue();
    }
}
