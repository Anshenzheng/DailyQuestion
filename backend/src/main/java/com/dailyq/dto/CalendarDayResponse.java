package com.dailyq.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CalendarDayResponse {
    private LocalDate date;
    private boolean hasAnswered;
    private Long answerId;
}
