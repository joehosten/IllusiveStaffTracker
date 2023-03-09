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
    public User getUser(UUID uuid) {
        System.out.println(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(uuid));
        return Bot.getBot().getJda().getUserById(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(uuid));
    }

    public OfflinePlayer getPlayer(String id) {
        return Bukkit.getOfflinePlayer(DiscordSRV.getPlugin().getAccountLinkManager().getUuid(id));
    }

    public long calculateMissedQuota(String uuid) {

        long quota = Long.parseLong("21600000");
        long timeInAfk = Long.parseLong(DbUtils.getAfkTime(uuid));
        long timeInData = Long.parseLong(DbUtils.getCurrentTime(uuid));
        if (timeInData >= quota) return 0;

        return quota - timeInData - timeInAfk;
    }
}
