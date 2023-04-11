package me.joehosten.illusivestafftracker.commands;

import games.negative.framework.discord.command.SlashCommand;
import games.negative.framework.discord.command.SlashInfo;
import me.joehosten.illusivestafftracker.core.util.DbUtils;
import me.joehosten.illusivestafftracker.core.util.DiscordSrvUtils;
import me.joehosten.illusivestafftracker.core.util.TimeUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Objects;
import java.util.UUID;

@SlashInfo(name = "staffcheck", description = "Check a player's staff time")
public class DiscordCommandCheck extends SlashCommand {
    public DiscordCommandCheck() {
        setData(k -> {
            k.addOption(OptionType.USER, "user", "Please select the user", true);
            k.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
        });
    }

    @Override
    public void onCommand(SlashCommandInteractionEvent slashCommandInteractionEvent) {
        User user = Objects.requireNonNull(slashCommandInteractionEvent.getOption("user")).getAsUser();
        UUID uuid = DiscordSrvUtils.getPlayer(user.getId()).getUniqueId();
        long rawPlayTime = Long.parseLong(DbUtils.getCurrentTime(uuid.toString()));
        String playTime = TimeUtil.format(rawPlayTime, 0);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(user.getName() + "'s playtime this week");
        eb.setDescription(user.getAsMention() + " has actively played for **%active%**.".replaceAll("%active%", playTime));
        eb.setThumbnail("https://crafatar.com/avatars/" + DiscordSrvUtils.getPlayer(user.getId()).getUniqueId() + "?overlay=1");
        slashCommandInteractionEvent.replyEmbeds(eb.build()).setEphemeral(true).queue();

    }
}
