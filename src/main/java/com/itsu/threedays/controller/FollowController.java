package com.itsu.threedays.controller;

import com.itsu.threedays.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/users")
public class FollowController {
    private final FollowService followService;

    @PostMapping("{fromUserId}/follow/{toUserId}")
//팔로우하기
    ResponseEntity<?> Follow(@PathVariable Long fromUserId, @PathVariable Long toUserId) {
        followService.followUser(fromUserId, toUserId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
