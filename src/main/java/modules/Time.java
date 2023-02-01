package modules;

import java.util.Date;

public class Time {
    public static long getTimestampInSeconds(int offsetDays) {
        Date date = new Date();
        return date.getTime()/1000-((long) offsetDays *24*60*60);
    }
}
