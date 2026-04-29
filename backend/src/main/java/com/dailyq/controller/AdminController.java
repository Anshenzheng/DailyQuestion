package com.dailyq.controller;

import com.dailyq.dto.AdminLoginRequest;
import com.dailyq.dto.QuestionRequest;
import com.dailyq.dto.Result;
import com.dailyq.entity.Question;
import com.dailyq.service.AdminService;
import com.dailyq.service.QuestionService;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final QuestionService questionService;

    public AdminController(AdminService adminService,
                           QuestionService questionService) {
        this.adminService = adminService;
        this.questionService = questionService;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody AdminLoginRequest request) {
        String token = adminService.login(request);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        return Result.success(result);
    }

    @PostMapping("/questions")
    public Result<Question> createQuestion(@Valid @RequestBody QuestionRequest request) {
        Question question = questionService.create(request);
        return Result.success(question);
    }

    @PutMapping("/questions/{id}")
    public Result<Question> updateQuestion(@PathVariable Long id,
                                            @RequestBody QuestionRequest request) {
        Question question = questionService.update(id, request);
        return Result.success(question);
    }

    @DeleteMapping("/questions/{id}")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        questionService.delete(id);
        return Result.success();
    }

    @GetMapping("/questions")
    public Result<List<Question>> getAllQuestions() {
        List<Question> questions = questionService.getAll();
        return Result.success(questions);
    }

    @GetMapping("/questions/{id}")
    public Result<Question> getQuestionById(@PathVariable Long id) {
        Question question = questionService.getById(id);
        return Result.success(question);
    }
}
