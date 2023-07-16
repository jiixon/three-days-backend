package com.itsu.threedays.service;

import com.itsu.threedays.entity.CertifyEntity;
import com.itsu.threedays.entity.HabitEntity;
import com.itsu.threedays.exception.NotFoundException;
import com.itsu.threedays.repository.CertifyRepository;
import com.itsu.threedays.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertifyService {

    private final HabitRepository habitRepository;
    private final CertifyRepository certifyRepository;

    public void certifyHabit(Long habitId, String review, int level, List<String> imageUrls) {
        //습관ID로 해당습관 찾기
        HabitEntity habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new NotFoundException("Habit not found with ID: " + habitId));

        CertifyEntity certification = new CertifyEntity();
        certification.setHabit(habit);
        certification.setLevel(level);
        certification.setReview(review);
        certification.setCreatedDate(LocalDateTime.now());
        certification.setImageUrls(imageUrls);
        certifyRepository.save(certification);

    }
}