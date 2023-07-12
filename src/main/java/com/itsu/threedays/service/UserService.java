package com.itsu.threedays.service;


import com.itsu.threedays.entity.UserEntity;
import com.itsu.threedays.repository.HabitRepository;
import com.itsu.threedays.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;

    public UserEntity findByEmail(String email) {
        Optional<UserEntity> byEmail = userRepository.findByEmail(email);
        return byEmail.get();
    }

}