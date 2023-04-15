package components;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static long getEpochInSeconds(int offsetDays) {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(offsetDays);
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()/1000;
    }

    public static String getUTCString(int offsetDays, DateTimeFormatter dtf) {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("UTC"));
        localDateTime = localDateTime.minusDays(offsetDays);
        return localDateTime.format(dtf);
    }

    public static LocalDateTime getUTC() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }

    public static void main(String[] args) {
        System.out.println(getUTCString(0, DateTimeFormatter.ofPattern("yyyyMMddhhmmss")));
    }
}
