package com.dailyq.service;

import com.dailyq.dto.AnswerRequest;
import com.dailyq.dto.AnswerResponse;
import com.dailyq.dto.CalendarDayResponse;
import com.dailyq.entity.Answer;
import com.dailyq.entity.DailyQuestion;
import com.dailyq.repository.AnswerRepository;
import com.dailyq.repository.DailyQuestionRepository;
import com.dailyq.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final DailyQuestionRepository dailyQuestionRepository;

    public AnswerService(AnswerRepository answerRepository,
                         DailyQuestionRepository dailyQuestionRepository) {
        this.answerRepository = answerRepository;
        this.dailyQuestionRepository = dailyQuestionRepository;
    }

    @Transactional
    public AnswerResponse saveAnswer(AnswerRequest request) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        
        DailyQuestion dailyQuestion = dailyQuestionRepository.findById(request.getDailyQuestionId())
                .orElseThrow(() -> new RuntimeException("每日问题不存在"));
        
        LocalDate answerDate = dailyQuestion.getQuestionDate();
        
        Optional<Answer> existingAnswer = answerRepository.findByUserIdAndAnswerDate(userId, answerDate);
        
        Answer answer;
        if (existingAnswer.isPresent()) {
            answer = existingAnswer.get();
            if (request.getContent() != null) {
                answer.setContent(request.getContent());
            }
            if (request.getImageUrl() != null) {
                answer.setImageUrl(request.getImageUrl());
            }
        } else {
            answer = new Answer();
            answer.setUserId(userId);
            answer.setQuestionId(dailyQuestion.getQuestionId());
            answer.setDailyQuestionId(dailyQuestion.getId());
            answer.setContent(request.getContent());
            answer.setImageUrl(request.getImageUrl());
            answer.setAnswerDate(answerDate);
        }
        
        answer = answerRepository.save(answer);
        
        return toResponse(answer);
    }

    public AnswerResponse getAnswerByDate(LocalDate date) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        
        Optional<Answer> optional = answerRepository.findByUserIdAndAnswerDate(userId, date);
        if (optional.isPresent()) {
            return toResponse(optional.get());
        }
        return null;
    }

    public List<AnswerResponse> getHistoryAnswers() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        
        List<Answer> answers = answerRepository.findByUserIdOrderByAnswerDateDesc(userId);
        return answers.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<CalendarDayResponse> getMonthAnswers(int year, int month) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        List<Answer> answers = answerRepository.findByUserIdAndAnswerDateBetween(userId, startDate, endDate);
        
        List<CalendarDayResponse> result = new ArrayList<>();
        for (Answer answer : answers) {
            CalendarDayResponse day = new CalendarDayResponse();
            day.setDate(answer.getAnswerDate());
            day.setHasAnswered(true);
            day.setAnswerId(answer.getId());
            result.add(day);
        }
        
        return result;
    }

    public AnswerResponse getAnswerById(Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("回答不存在"));
        
        Long userId = UserContext.getCurrentUserId();
        if (!answer.getUserId().equals(userId)) {
            throw new RuntimeException("无权查看他人的回答");
        }
        
        return toResponse(answer);
    }

    private AnswerResponse toResponse(Answer answer) {
        AnswerResponse response = new AnswerResponse();
        response.setId(answer.getId());
        response.setUserId(answer.getUserId());
        response.setQuestionId(answer.getQuestionId());
        response.setDailyQuestionId(answer.getDailyQuestionId());
        response.setContent(answer.getContent());
        response.setImageUrl(answer.getImageUrl());
        response.setAnswerDate(answer.getAnswerDate());
        response.setCreateTime(answer.getCreateTime());
        if (answer.getQuestion() != null) {
            response.setQuestionContent(answer.getQuestion().getContent());
        }
        return response;
    }
}
