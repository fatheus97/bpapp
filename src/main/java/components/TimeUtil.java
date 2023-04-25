package components;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class TimeUtil {
    private static final Properties PROPS = new Properties();

    static {
        try {
            PROPS.load(NetworkUtil.class.getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String ZONE = PROPS.getProperty("zone");

    private TimeUtil() {
    }

    public static long getEpochInSeconds(int offsetDays) {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of(ZONE)).minusDays(offsetDays);
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000;
    }

    public static String getUTCString(int offsetDays, DateTimeFormatter dtf) {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of(ZONE));
        localDateTime = localDateTime.minusDays(offsetDays);
        return localDateTime.format(dtf);
    }

    public static LocalDateTime getUTC() {
        return LocalDateTime.now(ZoneId.of(ZONE));
    }
}
