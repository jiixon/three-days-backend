package com.itsu.threedays.controller;

import com.itsu.threedays.dto.HabitDto;
import com.itsu.threedays.dto.HabitEditResponseDto;
import com.itsu.threedays.dto.HabitResponseDto;
import com.itsu.threedays.dto.HabitUpdateRequestDto;
import com.itsu.threedays.entity.HabitEntity;
import com.itsu.threedays.entity.UserEntity;
import com.itsu.threedays.service.CertifyService;
import com.itsu.threedays.service.HabitService;
import com.itsu.threedays.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("api")
public class HabitController {

    private final HabitService habitService;
    private final UserService userService;
    private final CertifyService certifyService;

    @PostMapping("habit")
        //습관 생성
    ResponseEntity<?> createHabit(@RequestBody HabitDto habitDto) {

        UserEntity byEmail = userService.findByEmail(habitDto.getEmail());
        log.info("byEmail.getEmail(): {}", byEmail.getEmail());

        HabitEntity habit = HabitEntity.builder()
                .title(habitDto.getTitle()) //습관명 저장
                .duration(habitDto.getDuration()) //습관 기간 저장
                .visible(habitDto.isVisible()) //공개여부 저장 //!!다시 확인하기
                .createdDate(LocalDateTime.now()) //생성일 저장
                .lastModifiedDate(LocalDateTime.now()) //변경일 저장
                .achievementRate(0) //달성률 저장
                .achievementCount(0) //달성횟수 저장
                .totalAchievementCount(0) //누적달성횟수 저장
                .comboCount(0) //콤보횟수 저장
                .stopDate(null) //중지일 저장
                .deleteYn(false) //삭제여부 저장
                .userId(byEmail)
                .build();

        habitService.saveHabit(habit);
        log.info("{}의 습관: 습관명-{}, 습관기간-{}, 공개여부-{}",
                byEmail.getNickname(), habit.getTitle(), habit.getDuration(), habit.isVisible());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("habits")
        //습관목록 조회(메인페이지)
    ResponseEntity<List<HabitResponseDto>> getHabitList(@RequestParam("email") String email) throws Exception {
        List<HabitEntity> habits = habitService.findUndeletedAndActiveHabits(email);
        log.info("habits: {}", habits);
        List<HabitResponseDto> habitResponseDtos = habits.stream()
                .map(habit -> {
                    HabitResponseDto responseDto = new HabitResponseDto();
                    responseDto.setId(habit.getId());
                    responseDto.setTitle(habit.getTitle());
                    responseDto.setDuration(habit.getDuration());
                    responseDto.setVisible(habit.isVisible());
                    responseDto.setComboCount(habit.getComboCount());
                    responseDto.setAchievementRate(habit.getAchievementRate());
                    responseDto.setAchievementCount(habit.getAchievementCount());
                    return responseDto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(habitResponseDtos);
    }


    @PutMapping("habits/{habitId}/edit")
        //습관수정(이름, 기간, 공개여부)
    ResponseEntity<HabitResponseDto> updateHabit(@PathVariable("habitId") Long habitId,
                                                 @RequestBody HabitUpdateRequestDto habitUpdateDto) throws Exception {
        HabitResponseDto updatedHabit = habitService.updateHabit(habitId, habitUpdateDto);

        return ResponseEntity.ok(updatedHabit);

    }

    @DeleteMapping("habits/{habitId}")
        //습관삭제
    ResponseEntity<?> deleteHabit(@PathVariable("habitId") Long habitId) {
        habitService.deleteHabit(habitId);
        if (certifyService.deleteCertification(habitId)) {
            return ResponseEntity.ok("Habit deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Habit authentication ID does not exist.");
        }
    }

    @PutMapping("habits/{habitId}/stop")
        //습관 중지
    ResponseEntity<String> stopHabit(@PathVariable("habitId") Long habitId) {
        try {
            habitService.stopHabit(habitId);
            return ResponseEntity.ok("Habit stopped successfully.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Habit not found.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }

    }

    @GetMapping("habits/edit-list")
        //편집시 습관목록조회(중지일 포함)
    ResponseEntity<List<HabitEditResponseDto>> getHabitEditList(@RequestParam("email") String email) throws Exception {
        List<HabitEntity> habits = habitService.findUndeletedAndAllHabits(email);
        log.info("undeletedAndAllHabits: {}", habits);
        List<HabitEditResponseDto> habitEditListDto = habits.stream()
                .map(habitEntity -> {
                    HabitEditResponseDto editResponseDto = new HabitEditResponseDto();
                    habitService.setCommonHabitFields(editResponseDto, habitEntity); //공통필드 설정
                    editResponseDto.setStopDate(habitEntity.getStopDate()); //중지일
                    return editResponseDto;
                })
                .sorted((habit1, habit2) -> {
                    LocalDateTime stopDate1 = habit1.getStopDate();
                    LocalDateTime stopDate2 = habit2.getStopDate();

                    if (stopDate1 == null && stopDate2 == null) {
                        return 0;
                    } else if (stopDate1 == null) {
                        return -1; // habit1이 null이므로 habit1이 먼저 정렬됨
                    } else if (stopDate2 == null) {
                        return 1; // habit2가 null이므로 habit2가 먼저 정렬됨
                    } else {
                        return stopDate1.compareTo(stopDate2); // 둘 다 null이 아니면 일반적으로 정렬
                    }
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(habitEditListDto);
    }

    @GetMapping("habits/{habitId}/reset")
        //매주마다 달성횟수 리셋
    ResponseEntity<String> resetAchievement(@PathVariable("habitId") Long habitId) {
        habitService.resetAchievement(habitId);
        return ResponseEntity.ok("Resetting the achievement count was successful.");
    }


}