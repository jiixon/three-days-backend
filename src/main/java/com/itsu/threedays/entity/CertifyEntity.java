package com.itsu.threedays.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @ElementCollection
    @CollectionTable(name = "CERTIFY_IMAGES", joinColumns = @JoinColumn(name = "CERTIFY_ID"))
    @Column(name = "IMAGE_URLS")
    private List<String> imageUrls;

    @Column(name = "REVIEW")
    private String review;

    @Column(name = "LEVEL")
    private int level;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "HABIT_ID")
    private HabitEntity habit;
}