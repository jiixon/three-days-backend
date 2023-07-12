package com.itsu.threedays.controller;

import com.itsu.threedays.dto.ProfileDto;
import com.itsu.threedays.entity.HabitEntity;
import com.itsu.threedays.exception.NotFoundException;
import com.itsu.threedays.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api")
public class UserProfileController {
    private final UserService userService;

    @PostMapping("userInfo")
    ResponseEntity<?> saveProfile(@RequestBody ProfileDto profileDto) {
        log.info("saveProfile Start!");
        try {
            List<HabitEntity> habits = userService.saveProfile(profileDto.getEmail(), profileDto.getNickname(), profileDto.getKeywords());

            return ResponseEntity.ok(habits); //처음 로그인 한 사용자 -> habits: [](null)
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }

    }

}