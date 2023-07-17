package util;

import java.time.LocalDateTime;

public class TaskUtil {
    public static LocalDateTime getDateTimeFromString(String dateTimeString) {
        String[] strings = dateTimeString.split("-");

        int year = Integer.parseInt(strings[0]);
        int month = Integer.parseInt(strings[1]);
        int day = Integer.parseInt(strings[2]);
        int hour = Integer.parseInt(strings[3]);
        int minute = Integer.parseInt(strings[4]);

        return LocalDateTime.of(year, month, day, hour, minute);
    }
}
