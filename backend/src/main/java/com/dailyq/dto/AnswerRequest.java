package com.dailyq.dto;

import lombok.Data;

@Data
public class AnswerRequest {
    private Long dailyQuestionId;
    private String content;
    private String imageUrl;
}
