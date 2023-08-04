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
public class CertifyDto {
    Long certifyId;
    List<String> imagUrls;
    String review;
    int level;
    LocalDateTime certifiedDate;

}
