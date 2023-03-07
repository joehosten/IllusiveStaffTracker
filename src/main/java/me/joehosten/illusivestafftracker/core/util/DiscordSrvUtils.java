package me.joehosten.illusivestafftracker.core.util;

import github.scarsz.discordsrv.DiscordSRV;
import lombok.experimental.UtilityClass;
import me.joehosten.illusivestafftracker.Bot;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@UtilityClass
public class DiscordSrvUtils {
    public User getPlayer(UUID uuid) {
        return Bot.getBot().getJda().getUserById(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(uuid));
    }

    public OfflinePlayer getPlayer(String id) {
        return Bukkit.getOfflinePlayer(DiscordSRV.getPlugin().getAccountLinkManager().getUuid(id));
    }
}
