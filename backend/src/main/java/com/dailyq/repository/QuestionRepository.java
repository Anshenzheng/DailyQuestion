package com.dailyq.repository;

import com.dailyq.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByStatusOrderByCreateTimeDesc(Integer status);
    List<Question> findByCategoryAndStatus(String category, Integer status);
    
    @Query("SELECT q FROM Question q WHERE q.status = 1 ORDER BY q.usedCount ASC, q.createTime ASC")
    List<Question> findRandomQuestion();
}
