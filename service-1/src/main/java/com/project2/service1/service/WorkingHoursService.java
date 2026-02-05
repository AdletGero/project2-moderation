package com.project2.service1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
@RequiredArgsConstructor
public class WorkingHoursService {

    private static final ZoneId ALMATY = ZoneId.of("Asia/Almaty");
    private static final LocalTime START = LocalTime.of(9, 0);
    private static final LocalTime END = LocalTime.of(18, 0);

    public boolean isWorkingHours(Instant instant){
        ZonedDateTime localTime = instant.atZone(ALMATY);
        DayOfWeek day = localTime.getDayOfWeek();

        if(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY){
            return false;
        }
        LocalTime now = localTime.toLocalTime();
        return now.isAfter(START) && now.isBefore(END);
    }
}
