package com.dailyq.controller;

import com.dailyq.dto.AnswerRequest;
import com.dailyq.dto.AnswerResponse;
import com.dailyq.dto.CalendarDayResponse;
import com.dailyq.dto.Result;
import com.dailyq.service.AnswerService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping
    public Result<AnswerResponse> saveAnswer(@RequestBody AnswerRequest request) {
        AnswerResponse response = answerService.saveAnswer(request);
        return Result.success(response);
    }

    @GetMapping("/date/{date}")
    public Result<AnswerResponse> getAnswerByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        AnswerResponse response = answerService.getAnswerByDate(localDate);
        return Result.success(response);
    }

    @GetMapping("/history")
    public Result<List<AnswerResponse>> getHistoryAnswers() {
        List<AnswerResponse> responses = answerService.getHistoryAnswers();
        return Result.success(responses);
    }

    @GetMapping("/calendar/{year}/{month}")
    public Result<List<CalendarDayResponse>> getMonthAnswers(
            @PathVariable int year,
            @PathVariable int month) {
        List<CalendarDayResponse> responses = answerService.getMonthAnswers(year, month);
        return Result.success(responses);
    }

    @GetMapping("/{id}")
    public Result<AnswerResponse> getAnswerById(@PathVariable Long id) {
        AnswerResponse response = answerService.getAnswerById(id);
        return Result.success(response);
    }
}
