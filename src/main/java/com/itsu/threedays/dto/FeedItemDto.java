package com.itsu.threedays.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedItemDto { //메인 피드
    Long userId;
    String nickname;
    String kakaoImageUrl;
    List<String> certifyImages;
    String title;
    String review;
    int level;
    LocalDateTime createdHabit;
    LocalDateTime certifiedDate;
}
