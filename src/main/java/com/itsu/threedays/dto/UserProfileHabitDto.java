package com.itsu.threedays.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileHabitDto {
    Long habitId;
    String title;
    LocalDateTime createdHabit;
    List<CertifyDto> certifyDtos;

}
