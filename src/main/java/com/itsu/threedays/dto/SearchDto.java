package com.itsu.threedays.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDto {
    String kakaoProfileUrl; //카카오프로필(user)
    String nickname; // 닉네임(profile)
    Long userId; //해당 유정Id(user)
    String title;

}
