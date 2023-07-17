package com.itsu.threedays.service;

import com.itsu.threedays.entity.CertifyEntity;
import com.itsu.threedays.entity.CertifyImageEntity;
import com.itsu.threedays.entity.HabitEntity;
import com.itsu.threedays.exception.NotFoundException;
import com.itsu.threedays.repository.CertifyImageRepository;
import com.itsu.threedays.repository.CertifyRepository;
import com.itsu.threedays.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertifyService {

    private final HabitRepository habitRepository;
    private final CertifyRepository certifyRepository;
    private final CertifyImageRepository certifyImageRepository;

    public void certifyHabit(Long habitId, String review, int level, List<String> imageUrls) {
        //습관ID로 해당습관 찾기
        HabitEntity habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new NotFoundException("Habit not found with ID: " + habitId));

        CertifyEntity certifyEntity = new CertifyEntity();
        certifyEntity.setCreatedDate(LocalDateTime.now());
        certifyEntity.setLevel(level);
        certifyEntity.setReview(review);
        certifyEntity.setHabit(habit);
        certifyEntity.setImages(new ArrayList<>());
        certifyRepository.save(certifyEntity);

        for (String imageUrl : imageUrls) {
            CertifyImageEntity certifyImage = new CertifyImageEntity();
            certifyImage.setCertifyEntity(certifyEntity);
            certifyImage.setImageUrl(imageUrl);
            certifyImageRepository.save(certifyImage);
        }
    }

    public boolean deleteCertification(Long habitId) {
        try {
            certifyRepository.deleteById(habitId);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}