package com.dailyq.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AnswerResponse {
    private Long id;
    private Long userId;
    private Long questionId;
    private Long dailyQuestionId;
    private String content;
    private String imageUrl;
    private LocalDate answerDate;
    private LocalDateTime createTime;
    private String questionContent;
}
