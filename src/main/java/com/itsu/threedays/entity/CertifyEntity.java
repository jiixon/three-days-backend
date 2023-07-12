package com.itsu.threedays.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "CERTIFY_ENTITY")
public class CertifyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "IMAGE")
    private String image;

    @Column(name = "REVIEW")
    private String review;

    @Column(name = "LEVEL")
    private int level;

    @ManyToOne
    @JoinColumn(name = "HABIT_ID")
    private HabitEntity habit;
}