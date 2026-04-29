package com.dailyq.repository;

import com.dailyq.entity.DailyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyQuestionRepository extends JpaRepository<DailyQuestion, Long> {
    Optional<DailyQuestion> findByQuestionDate(LocalDate questionDate);
    boolean existsByQuestionDate(LocalDate questionDate);
}
