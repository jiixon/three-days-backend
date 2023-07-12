package com.itsu.threedays.controller;

import com.itsu.threedays.dto.CertifyDto;
import com.itsu.threedays.exception.NotFoundException;
import com.itsu.threedays.service.CertifyService;
import com.itsu.threedays.service.HabitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("api/habits")
public class CertifyController {
    private final CertifyService certifyService;
    private final HabitService habitService;

    @PostMapping("{habitId}/certify")
        //습관인증
    ResponseEntity<?> certifyHabit(@PathVariable("habitId") Long habitId, @RequestBody CertifyDto certifyDto) {
        try {
            certifyService.certifyHabit(habitId, certifyDto); //습관 인증 저장

            //습관 인증 -> 습관테이블 변경사항(달성률, 달성횟수, 콤보횟수)
            habitService.updateAchievementAndCombo(habitId);
            return ResponseEntity.ok("습관이 인증되었습니다.");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}