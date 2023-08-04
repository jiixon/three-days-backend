package com.itsu.threedays.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto { //SNS 내프로필에 필요한 정보

    String kakaoImageUrl; //카카오프로필
    String nickname; //닉네임(유저가 정한)
    List<String> keywords; //키워드(유저가 정한)
    int totalAchievementRate; //달성률(달성중인 습관의 평균)
    int totalHabitCount; //달성중인 습관갯수
    int followerCount; //팔로워 수
    List<UserProfileHabitDto> habitList; //습관들
    boolean isFollowing;
}
