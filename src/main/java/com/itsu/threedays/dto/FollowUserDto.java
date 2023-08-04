package com.itsu.threedays.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowUserDto {
    Long id;
    String nickname; //닉네임
    String profileImageUrl; //프로필 url
    int totalAchievementRate; //달성률(달성중인 습관의 평균)
    int totalHabitCount; //달성중인 습관갯수
}
