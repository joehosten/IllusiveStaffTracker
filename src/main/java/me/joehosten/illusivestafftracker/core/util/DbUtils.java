package me.joehosten.illusivestafftracker.core.util;

import games.negative.framework.db.SQLDatabase;
import lombok.experimental.UtilityClass;
import me.joehosten.illusivestafftracker.IllusiveStaffTracker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public String getCurrentTime(UUID uuid) {
        SQLDatabase db = IllusiveStaffTracker.getInstance().getDb();

        try {
            PreparedStatement ps = db.statement("SELECT `time` FROM `staff-time-tracking` WHERE `uuid` = ?");
            ps.setString(1, uuid.toString());
            ps.closeOnCompletion();
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("time");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "0";
    }

}
