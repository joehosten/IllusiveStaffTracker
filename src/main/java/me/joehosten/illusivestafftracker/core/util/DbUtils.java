package me.joehosten.illusivestafftracker.core.util;

import games.negative.framework.db.SQLDatabase;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.joehosten.illusivestafftracker.IllusiveStaffTracker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

@UtilityClass
public class DbUtils {

    public boolean existsInData(UUID uuid) {
        SQLDatabase db = IllusiveStaffTracker.getInstance().getDb();

        try {
            PreparedStatement ps = db.statement("SELECT * FROM `staff-time-tracking` WHERE `uuid` = ?");
            ps.setString(1, uuid.toString());
            ps.closeOnCompletion();
            ResultSet rs = ps.executeQuery();
            if (rs.isBeforeFirst()) return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public String getCurrentTime(String uuid) {
        SQLDatabase db = IllusiveStaffTracker.getInstance().getDb();

        try {
            PreparedStatement ps = db.statement("SELECT `time` FROM `staff-time-tracking` WHERE `uuid` = ?");
            ps.setString(1, uuid);
            ps.closeOnCompletion();
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("time");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "0";
    }

    @SneakyThrows
    public ArrayList<String> getAllUuids() {
        SQLDatabase db = IllusiveStaffTracker.getInstance().getDb();
        ArrayList<String> uuids = new ArrayList<>();
        try {
            PreparedStatement ps = db.statement("SELECT `uuid` FROM `staff-time-tracking`");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                uuids.add(rs.getString("uuid"));
            }
            ps.closeOnCompletion();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return uuids;
    }

    @SneakyThrows
    public String getDiscordId(String uuid) {
        SQLDatabase db = IllusiveStaffTracker.getInstance().getDb();
        try {
            PreparedStatement ps = db.statement("SELECT `discordId` FROM `staff-link` WHERE `uuid` = ?");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("discordId");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @SneakyThrows
    public String getMinecraftId(String discordId) {
        SQLDatabase db = IllusiveStaffTracker.getInstance().getDb();
        try {
            PreparedStatement ps = db.statement("SELECT `uuid` FROM `staff-link` WHERE `discordId` = ?");
            ps.setString(1, discordId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("uuid");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
