package com.dailyq.controller;

import com.dailyq.dto.DailyQuestionResponse;
import com.dailyq.dto.Result;
import com.dailyq.service.DailyQuestionService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/daily-question")
public class DailyQuestionController {

    private final DailyQuestionService dailyQuestionService;

    public DailyQuestionController(DailyQuestionService dailyQuestionService) {
        this.dailyQuestionService = dailyQuestionService;
    }

    @GetMapping("/today")
    public Result<DailyQuestionResponse> getTodayQuestion() {
        DailyQuestionResponse response = dailyQuestionService.getTodayQuestion();
        return Result.success(response);
    }

    @GetMapping("/date/{date}")
    public Result<DailyQuestionResponse> getQuestionByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        DailyQuestionResponse response = dailyQuestionService.getQuestionByDate(localDate);
        return Result.success(response);
    }
}
