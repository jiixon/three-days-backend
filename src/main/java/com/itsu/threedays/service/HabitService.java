package com.itsu.threedays.service;

import com.itsu.threedays.dto.HabitResponseDto;
import com.itsu.threedays.dto.HabitUpdateRequestDto;
import com.itsu.threedays.entity.HabitEntity;
import com.itsu.threedays.entity.UserEntity;
import com.itsu.threedays.exception.NotFoundException;
import com.itsu.threedays.repository.HabitRepository;
import com.itsu.threedays.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class HabitService {
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;

    public void saveHabit(HabitEntity habit) { //습관 저장
        habitRepository.save(habit);

    }

    public List<HabitEntity> findUndeletedAndActiveHabits(String email) throws Exception { //삭제여부 false, 중지일 null인 습관목록
        Optional<UserEntity> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            UserEntity user = byEmail.get();
            log.info("email로 user 찾기:{}", user.getEmail());
            return habitRepository.findAllByUserIdAndDeleteYnAndStopDateIsNull(user, false);
        } else {
            throw new Exception("User NOT FOUND!");
        }
    }

    public List<HabitEntity> findUndeletedAndAllHabits(String email) throws Exception { //삭제여부 false 전부(중지일 상관없이 = 그만두기 포함)
        Optional<UserEntity> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            UserEntity user = byEmail.get();
            log.info("email로 user 찾기:{}", user.getEmail());
            return habitRepository.findAllByDeleteYn(false);
        } else {
            throw new Exception("User NOT FOUND!");
        }
    }

    public HabitResponseDto updateHabit(Long habitId, HabitUpdateRequestDto habitUpdateDto) throws Exception {

        //습관 ID로 습관 찾기
        Optional<HabitEntity> byId = habitRepository.findById(habitId);
        if (byId.isPresent()) {
            HabitEntity habit = byId.get();
            log.info("습관ID로 습관찾기: {}", habit);

            //수정할 내용 업데이트
            habit.setTitle(habitUpdateDto.getTitle());
            habit.setDuration(habitUpdateDto.getDuration());
            habit.setVisible(habitUpdateDto.isVisible());

            //수정된 부분 DB 저장
            HabitEntity updatedHabit = habitRepository.save(habit);

            HabitResponseDto habitResponseDto = new HabitResponseDto();
//            habitResponseDto.setId(updatedHabit.getId());
//            habitResponseDto.setTitle(updatedHabit.getTitle());
//            habitResponseDto.setDuration(updatedHabit.getDuration());
//            habitResponseDto.setVisible(updatedHabit.isVisible());
//            habitResponseDto.setComboCount(updatedHabit.getComboCount());
//            habitResponseDto.setAchievementRate(updatedHabit.getAchievementRate());
//            habitResponseDto.setAchievementCount(updatedHabit.getAchievementCount());
            setCommonHabitFields(habitResponseDto, updatedHabit);

            return habitResponseDto;

        } else {
            throw new Exception("User NOT FOUND!");
        }

    }

    public void deleteHabit(Long habitId) {
        Optional<HabitEntity> byUserId = habitRepository.findById(habitId);
        if (byUserId.isPresent()) {
            HabitEntity habit = byUserId.get();
            log.info("습관ID로 습관찾기: 습관명 - {}", habit.getTitle());

            habit.setDeleteYn(true); //삭제여부 true로 변경
            habit.setLastModifiedDate(LocalDateTime.now()); //수정일 현재날짜로 변경
            habitRepository.save(habit);
        } else {
            throw new NotFoundException("Habit not found with id: " + habitId);

        }

    }

    /**
     * 유저가 습관 인증 후 → 습관 Entity에서
     * 1)달성횟수 +1 증가
     * 2)달성횟수에 따른 달성률 증가 (누적인증횟수/선택기간*현재주차)*100
     * 3)달성기간 숫자와 달성횟수가 동일하다면 (ex. 4/4) 콤보횟수 +1 증가
     **/

    public void updateAchievementAndCombo(Long habitId) {
        //습관ID로 해당습관 찾기
        HabitEntity habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new NotFoundException("Habit not found with ID: " + habitId));

        int newAchievementCount = habit.getAchievementCount() + 1;
        habit.setAchievementCount(newAchievementCount); //달성횟수 +1
        log.info("달성횟수: {}", newAchievementCount);

        int newTotalAchievementCount = habit.getTotalAchievementCount() + 1;
        habit.setTotalAchievementCount(newTotalAchievementCount); //누적달성횟수 +1
        log.info("누적달성횟수: {}", newTotalAchievementCount);

        LocalDate now = LocalDate.now(); //
        LocalDate habitCreate = habit.getCreatedDate().toLocalDate();
        long daysBetween = ChronoUnit.DAYS.between(now, habitCreate);
        int weeks = (int) (daysBetween / 7) + 1;
        log.info("현재주차: {}", weeks);

        double i = ((double) newTotalAchievementCount / habit.getDuration() * weeks) * 100;
        int newAchievementRate = (int) Math.round(i);
        log.info("달성률: {}", newAchievementRate);
        habit.setAchievementRate(newAchievementRate); //달성률 증가

        if (habit.getDuration() == newAchievementCount) {
            int newComboCount = habit.getComboCount() + 1;
            habit.setComboCount(newComboCount); //콤보횟수 +1
            log.info("콤보횟수: {}", newComboCount);
        }

        habitRepository.save(habit);

    }

    public void stopHabit(Long habitId) {
        Optional<HabitEntity> optionalHabit = habitRepository.findById(habitId);
        if (optionalHabit.isPresent()) {
            HabitEntity habit = optionalHabit.get();

            if (habit.getStopDate() == null) { //중지일이 null 이면
                habit.setStopDate(LocalDateTime.now()); //중지일을 현재 날짜로 설정
                habitRepository.save(habit);
            } else {
                throw new IllegalStateException("Habit is already stopped.");
            }

        } else {
            throw new NotFoundException("Habit not found with id: " + habitId);
        }
    }


    public void resetAchievement(Long habitId) {
        //습관ID로 해당습관 찾기
        HabitEntity habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new NotFoundException("Habit not found with ID: " + habitId));

        //synchronized 블록으로 동시성 문제 해결
        synchronized (habit) {
            //현재 날짜의 요일과 습관 생성일의 요일
            LocalDateTime now = LocalDateTime.now();
            DayOfWeek nowDayOfWeek = now.getDayOfWeek();
            LocalDateTime habitCreate = habit.getCreatedDate();
            DayOfWeek habitDayOfWeek = habitCreate.getDayOfWeek();

            log.info("nowDayOfWeek: {}", nowDayOfWeek);
            log.info("habitDayOfWeek: {}", habitDayOfWeek);

            if (nowDayOfWeek == habitDayOfWeek) {
                //현재날짜의 요일(ex.수요일)과 습관 생성일의 요일(ex.수요일)이 같으면,
                habit.setAchievementCount(0); //달성횟수 초기화
            }
        }
    }

    public void setCommonHabitFields(HabitResponseDto habitDto, HabitEntity habitEntity) {
        habitDto.setId(habitEntity.getId());
        habitDto.setTitle(habitEntity.getTitle());
        habitDto.setDuration(habitEntity.getDuration());
        habitDto.setVisible(habitEntity.isVisible());
        habitDto.setAchievementRate(habitEntity.getAchievementRate());
        habitDto.setAchievementCount(habitEntity.getAchievementCount());
    }


}