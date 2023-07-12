package com.itsu.threedays.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HabitResponseDto {
    Long id;
    String title;
    int duration;
    boolean visible;
    int comboCount;
    int achievementRate;
    int achievementCount;

    //추가할 것

}