package com.itsu.threedays.controller;

import com.itsu.threedays.dto.CreateUserRequest;
import com.itsu.threedays.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api")
public class TestController {
    private final UserService userService;

    @PostMapping("test")
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {
        userService.createUser(request.getNickname(), request.getEmail(), request.getPassword(), request.getProfileImage(), request.getRefreshToken());
        return ResponseEntity.ok("User created successfully!");
    }
}
