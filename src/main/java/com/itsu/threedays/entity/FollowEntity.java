package com.itsu.threedays.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FOLLOW_ENTITY")
public class FollowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FROM_USER_ID")
    private UserEntity fromUser;

    @ManyToOne
    @JoinColumn(name = "TO_USER_ID")
    private UserEntity toUser;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

}
