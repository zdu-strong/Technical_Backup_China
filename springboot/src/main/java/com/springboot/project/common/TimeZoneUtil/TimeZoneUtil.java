package com.springboot.project.common.TimeZoneUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Component;

@Component
public class TimeZoneUtil {

    /**
     * return value like +08:00
     * 
     * @param timeZone
     * @return
     */
    public String getTimeZone(String timeZone) {
        ZoneId zoneId = ZoneId.of(timeZone);
        ZonedDateTime zonedDateTime = Instant.now().atZone(zoneId);
        var zoneOffset = String.format("%tz", zonedDateTime);
        zoneOffset = zoneOffset.substring(0, 3) + ":" + zoneOffset.substring(3, 5);
        return zoneOffset;
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
