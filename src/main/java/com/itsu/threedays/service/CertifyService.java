package com.itsu.threedays.service;

import com.itsu.threedays.dto.CertifyDto;
import com.itsu.threedays.entity.CertifyEntity;
import com.itsu.threedays.entity.HabitEntity;
import com.itsu.threedays.exception.NotFoundException;
import com.itsu.threedays.repository.CertifyRepository;
import com.itsu.threedays.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertifyService {

    private final HabitRepository habitRepository;
    private final CertifyRepository certifyRepository;

    public void certifyHabit(Long habitId, CertifyDto certifyDto) {
        //습관ID로 해당습관 찾기
        HabitEntity habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new NotFoundException("Habit not found with ID: " + habitId));


        //CertifyEntity에 인증 정보를 저장하고 Habit과 맵핑
        CertifyEntity certifyEntity = new CertifyEntity();
        certifyEntity.setReview(certifyDto.getReview());
        certifyEntity.setLevel(certifyDto.getLevel());
        certifyEntity.setImage(certifyDto.getImage());
        certifyEntity.setHabit(habit);

        //습관인증 DB 저장
        certifyRepository.save(certifyEntity);


    }


}