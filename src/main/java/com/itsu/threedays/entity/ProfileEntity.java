package com.itsu.threedays.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "PROFILE_ENTITY")
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NICKNAME")
    private String nickname;

    @ElementCollection
    @CollectionTable(name = "PROFILE_KEYWORDS", joinColumns = @JoinColumn(name = "PROFILE_ID"))
    @Column(name = "KEYWORDS")
    private List<String> keywords;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private UserEntity userId;


}