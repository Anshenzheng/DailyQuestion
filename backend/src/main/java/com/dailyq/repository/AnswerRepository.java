package com.dailyq.repository;

import com.dailyq.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByUserIdAndAnswerDate(Long userId, LocalDate answerDate);
    List<Answer> findByUserIdOrderByAnswerDateDesc(Long userId);
    List<Answer> findByUserIdAndAnswerDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    boolean existsByUserIdAndAnswerDate(Long userId, LocalDate answerDate);
}
