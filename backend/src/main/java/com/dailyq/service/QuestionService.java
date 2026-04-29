package com.dailyq.service;

import com.dailyq.dto.QuestionRequest;
import com.dailyq.entity.Question;
import com.dailyq.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Transactional
    public Question create(QuestionRequest request) {
        Question question = new Question();
        question.setContent(request.getContent());
        question.setCategory(request.getCategory() != null ? request.getCategory() : "daily");
        question.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        question.setUsedCount(0);
        return questionRepository.save(question);
    }

    @Transactional
    public Question update(Long id, QuestionRequest request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("问题不存在"));
        if (request.getContent() != null) {
            question.setContent(request.getContent());
        }
        if (request.getCategory() != null) {
            question.setCategory(request.getCategory());
        }
        if (request.getStatus() != null) {
            question.setStatus(request.getStatus());
        }
        return questionRepository.save(question);
    }

    @Transactional
    public void delete(Long id) {
        questionRepository.deleteById(id);
    }

    public Question getById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("问题不存在"));
    }

    public List<Question> getAllActive() {
        return questionRepository.findByStatusOrderByCreateTimeDesc(1);
    }

    public List<Question> getAll() {
        return questionRepository.findAll();
    }
}
