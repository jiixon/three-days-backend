package com.itsu.threedays.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitDto {

    String email;
    String title; //습관명
    int duration; //습관 기간
    boolean visible; //습관 공개여부 (true: 공개, false: 비공개)


}