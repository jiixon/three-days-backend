package com.itsu.threedays.service;

import com.itsu.threedays.entity.ProfileEntity;
import com.itsu.threedays.entity.UserEntity;
import com.itsu.threedays.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileEntity getProfile(UserEntity user) {
        ProfileEntity byUserId = profileRepository.findByUserId(user);
        log.info("byUserId - profileEntity :{}", byUserId);
        return byUserId;
    }
}
