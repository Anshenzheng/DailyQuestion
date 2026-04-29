package com.dailyq.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "open_id", unique = true, nullable = false)
    private String openId;

    @Column(name = "union_id")
    private String unionId;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "gender", columnDefinition = "TINYINT")
    private Integer gender;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
