package me.joehosten.illusivestafftracker;

import games.negative.framework.BasePlugin;
import games.negative.framework.db.SQLDatabase;
import games.negative.framework.db.builder.DatabaseBuilder;
import games.negative.framework.db.builder.maria.MariaDatabaseBuilder;
import games.negative.framework.db.builder.maria.MariaTableBuilder;
import games.negative.framework.db.exception.DriverNotFoundException;
import games.negative.framework.db.exception.InvalidConnectionException;
import games.negative.framework.db.model.SQLColumnType;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.joehosten.illusivestafftracker.core.util.ConfigUtils;
import me.joehosten.illusivestafftracker.listeners.DailySummaryRunnable;
import me.joehosten.illusivestafftracker.listeners.DateCheckRunnable;
import me.joehosten.illusivestafftracker.listeners.PlayerLogListener;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.UUID;

public final class IllusiveStaffTracker extends BasePlugin {

    @Getter
    @Setter
    private static IllusiveStaffTracker instance;
    @Getter
    public HashMap<UUID, Long> map = new HashMap<>();
    @Getter
    public HashMap<UUID, Long> afkMap = new HashMap<>();
    @Getter
    @Setter
    private TextChannel textChannel;
    @Getter
    private SQLDatabase db;
    @Getter
    private FileConfiguration config;

    @SneakyThrows
    @Override
    public void onEnable() {
        setInstance(this);

        loadFiles(this, "config.yml");
        this.config = new ConfigUtils("config").getConfig();
        connect(config.getString("database.host"), config.getInt("database.port"), config.getString("database.database"), config.getString("database.username"), config.getString("database.password"));

        new Bot();
        registerListeners(new PlayerLogListener(this, db));


        Timer timer = new Timer();

        timer.schedule(new DateCheckRunnable(), getSummaryTime());
        new DailySummaryRunnable().runTaskTimerAsynchronously(this, 0L, 60 * 20L);

    }

    @Override
    public void onDisable() {
        new PlayerLogListener(this, db).saveAllPlayers();
        new DateCheckRunnable().cancel();
        new DailySummaryRunnable().cancel();
        Bot.getBot().getJda().shutdown();
        try {
            db.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void connect(String host, int port, String database, String username, String password) {
        MariaDatabaseBuilder builder = DatabaseBuilder.maria(host, port, database, username, password);
        MariaTableBuilder playtimeTable = builder.withTable("staff-time-tracking");
        playtimeTable.withColumn("uuid", SQLColumnType.VARCHAR, 64).setPrimary(true).build();
        playtimeTable.withColumn("time", SQLColumnType.LONG, 64).build();
        playtimeTable.withColumn("afkTime", SQLColumnType.LONG, 64).build();

        MariaTableBuilder staffLinkTable = builder.withTable("staff-link");
        staffLinkTable.withColumn("uuid", SQLColumnType.VARCHAR, 64);
        staffLinkTable.withColumn("discordId", SQLColumnType.VARCHAR, 64);

        this.db = null;
        try {
            db = builder.complete();
        } catch (InvalidConnectionException e) {
            System.out.println("Could not connect to database");
            e.printStackTrace();
        } catch (DriverNotFoundException e) {
            System.out.println("Could not find correct SQL driver");
            e.printStackTrace();
        }

    }

    private Date getSummaryTime() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("F HH:mm:ss");
        return dateFormatter.parse("7 23:00:00");
    }
}
