package com.dailyq.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class QuestionRequest {
    @NotBlank(message = "问题内容不能为空")
    private String content;
    private String category = "daily";
    private Integer status = 1;
}
