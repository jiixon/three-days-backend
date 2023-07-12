package com.itsu.threedays.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "HABIT_ENTITY")
public class HabitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TITLE")
    private String title; //습관명

    @Column(name = "DURATION")
    private int duration; //습관 기간

    @Column(name = "VISIBLE")
    private boolean visible; //공개여부

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate; //생성일

    @Column(name = "LAST_MODIFIED_DATE")
    private LocalDateTime lastModifiedDate; //변경일

    @Column(name = "DELETE_YN")
    private boolean deleteYn; //삭제여부

    @Column(name = "STOP_DATE")
    private LocalDateTime stopDate; //중지일

    @Column(name = "COMBO_COUNT")
    private int comboCount; //콤보횟수

    @Column(name = "ACHIEVEMENT_RATE")
    private int achievementRate; //달성률

    @Column(name = "ACHIEVEMENT_COUNT")
    private int achievementCount; //달성횟수

    @Column(name = "TOTAL_ACHIEVEMENT_COUNT")
    private int totalAchievementCount; //누적달성횟수

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity userId;


}







