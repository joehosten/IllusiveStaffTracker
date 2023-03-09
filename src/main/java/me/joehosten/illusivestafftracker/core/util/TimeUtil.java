package me.joehosten.illusivestafftracker.core.util;

import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Time Utility
 *
 * @author Negative
 */
@UtilityClass
public class TimeUtil {

    private final String format;

    static {
        format = "%day%%hour%%min%%sec%";
    }

    public String format(long l1, long l2) {
        return format(l1, l2, false);
    }


    public String format(long l1, long l2, boolean shortened) {
        long newTime = l1 - l2;

        // Anything over a day

        int toSec = (int) (newTime / 1000) % 60;
        int toMin = (int) ((newTime / (1000 * 60)) % 60);
        int toHour = (int) ((newTime / (1000 * 60 * 60)) % 24);
        int toDays = (int) (newTime / (1000 * 60 * 60 * 24));

        boolean dayNotZero = toDays != 0;
        boolean hourNotZero = toHour != 0;
        boolean minuteNotZero = toMin != 0;
        boolean secondNotZero = toSec != 0;


        String day;
        if (shortened)
            day = "d";
        else
            day = (toDays == 1 ? "day" : "days");

        String hour;
        if (shortened)
            hour = "h";
        else
            hour = (toHour == 1 ? "hour" : "hours");

        String minute;
        if (shortened)
            minute = "m";
        else
            minute = (toMin == 1 ? "minute" : "minutes");

        String second;
        if (shortened)
            second = "s";
        else
            second = (toSec == 1 ? "second" : "seconds");

        String dayFormat = (dayNotZero ? toDays + (shortened ? "" : " ") + day + " " : "");
        String hourFormat = (hourNotZero ? toHour + (shortened ? "" : " ") + hour + " " : "");
        String minuteFormat = (minuteNotZero ? toMin + (shortened ? "" : " ") + minute + " " : "");
        String secondFormat = (secondNotZero ? toSec + (shortened ? "" : " ") + second + " " : "");

        return new StringHelper(format)
                .replace("%day%", dayFormat)
                .replace("%hour%", hourFormat)
                .replace("%min%", minuteFormat)
                .replace("%sec%", secondFormat)
                .getMessage();
    }

    public Long longFromString(String s) {
        StringBuilder builder = new StringBuilder();
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        int days = 0;
        int weeks = 0;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                builder.append(c);
            } else {
                switch (c) {
                    case 's':
                        if (builder.length() != 0) {
                            seconds += Integer.parseInt(builder.toString());
                            builder = new StringBuilder();
                        }
                        break;
                    case 'm':
                        if (builder.length() != 0) {
                            minutes += Integer.parseInt(builder.toString());
                            builder = new StringBuilder();
                        }
                        break;
                    case 'h':
                        if (builder.length() != 0) {
                            hours += Integer.parseInt(builder.toString());
                            builder = new StringBuilder();
                        }
                        break;
                    case 'd':
                        if (builder.length() != 0) {
                            days += Integer.parseInt(builder.toString());
                            builder = new StringBuilder();
                        }
                        break;
                    case 'w':
                        if (builder.length() != 0) {
                            weeks += Integer.parseInt(builder.toString());
                            builder = new StringBuilder();
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Not a valid duration format.");
                }
            }
        }
        return 1000L * (seconds + minutes * 60L + hours * 60 * 60L + days * 24 * 60 * 60L + weeks * 7 * 24 * 60 * 60L);
    }

    public String timeNow() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss aa");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        return df.format(cal.getTime());
    }
}
