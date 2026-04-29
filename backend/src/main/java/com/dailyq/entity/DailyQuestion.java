package com.dailyq.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "daily_questions")
public class DailyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "question_date", nullable = false)
    private LocalDate questionDate;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
