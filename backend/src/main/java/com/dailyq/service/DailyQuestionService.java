package com.dailyq.service;

import com.dailyq.dto.DailyQuestionResponse;
import com.dailyq.entity.DailyQuestion;
import com.dailyq.entity.Question;
import com.dailyq.repository.AnswerRepository;
import com.dailyq.repository.DailyQuestionRepository;
import com.dailyq.repository.QuestionRepository;
import com.dailyq.security.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DailyQuestionService {

    private final DailyQuestionRepository dailyQuestionRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public DailyQuestionService(DailyQuestionRepository dailyQuestionRepository,
                                QuestionRepository questionRepository,
                                AnswerRepository answerRepository) {
        this.dailyQuestionRepository = dailyQuestionRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    @Transactional
    public DailyQuestionResponse getTodayQuestion() {
        LocalDate today = LocalDate.now();
        return getQuestionByDate(today);
    }

    @Transactional
    public DailyQuestionResponse getQuestionByDate(LocalDate date) {
        Optional<DailyQuestion> optional = dailyQuestionRepository.findByQuestionDate(date);
        
        DailyQuestion dailyQuestion;
        if (optional.isPresent()) {
            dailyQuestion = optional.get();
        } else {
            if (date.isAfter(LocalDate.now())) {
                throw new RuntimeException("不能查看未来的问题");
            }
            dailyQuestion = generateDailyQuestion(date);
        }
        
        DailyQuestionResponse response = new DailyQuestionResponse();
        response.setId(dailyQuestion.getId());
        response.setQuestionId(dailyQuestion.getQuestion().getId());
        response.setQuestionDate(dailyQuestion.getQuestionDate());
        response.setContent(dailyQuestion.getQuestion().getContent());
        response.setCategory(dailyQuestion.getQuestion().getCategory());
        
        Long userId = UserContext.getCurrentUserId();
        if (userId != null) {
            boolean hasAnswered = answerRepository.existsByUserIdAndAnswerDate(userId, date);
            response.setHasAnswered(hasAnswered);
        } else {
            response.setHasAnswered(false);
        }
        
        return response;
    }

    @Transactional
    public DailyQuestion generateDailyQuestion(LocalDate date) {
        Optional<DailyQuestion> existing = dailyQuestionRepository.findByQuestionDate(date);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        List<Question> questions = questionRepository.findRandomQuestion();
        if (questions.isEmpty()) {
            throw new RuntimeException("没有可用的问题");
        }
        
        Question question = questions.get(0);
        question.setUsedCount(question.getUsedCount() + 1);
        questionRepository.save(question);
        
        DailyQuestion dailyQuestion = new DailyQuestion();
        dailyQuestion.setQuestionId(question.getId());
        dailyQuestion.setQuestionDate(date);
        
        return dailyQuestionRepository.save(dailyQuestion);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void generateTodayQuestion() {
        LocalDate today = LocalDate.now();
        if (!dailyQuestionRepository.existsByQuestionDate(today)) {
            generateDailyQuestion(today);
            log.info("已生成今日问题: {}", today);
        }
    }
}
