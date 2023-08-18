package com.springboot.project.common.TimeZoneUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Component;

@Component
public class TimeZoneUtils {

    /**
     * return value like +08:00
     * 
     * @param timeZone
     * @return
     */
    public String getTimeZone(String timeZone) {
        ZoneId zoneId = ZoneId.of(timeZone);
        ZonedDateTime zonedDateTime = Instant.now().atZone(zoneId);
        timeZone = String.format("%tz", zonedDateTime);
        timeZone = timeZone.substring(0, 3) + ":" + timeZone.substring(3, 5);
        return timeZone;
    }

    /**
     * return value like +00:00
     * 
     * @param timeZone
     * @return
     */
    public String getTimeZoneOfUTC() {
        return this.getTimeZone("UTC");
    }

}
