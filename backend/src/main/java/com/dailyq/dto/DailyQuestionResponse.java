package com.dailyq.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DailyQuestionResponse {
    private Long id;
    private Long questionId;
    private LocalDate questionDate;
    private String content;
    private String category;
    private boolean hasAnswered;
}
