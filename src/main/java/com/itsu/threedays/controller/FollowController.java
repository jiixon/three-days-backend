package com.itsu.threedays.controller;

import com.itsu.threedays.dto.FollowUserDto;
import com.itsu.threedays.entity.FollowEntity;
import com.itsu.threedays.entity.HabitEntity;
import com.itsu.threedays.entity.ProfileEntity;
import com.itsu.threedays.entity.UserEntity;
import com.itsu.threedays.repository.FollowRepository;
import com.itsu.threedays.service.FollowService;
import com.itsu.threedays.service.HabitService;
import com.itsu.threedays.service.ProfileService;
import com.itsu.threedays.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/users")
public class FollowController {
    private final FollowService followService;
    private final UserService userService;
    private final ProfileService profileService;
    private final HabitService habitService;
    private final FollowRepository followRepository;

    @PostMapping("{fromUserId}/follow/{toUserId}")
//팔로우하기
    ResponseEntity<String> follow(@PathVariable Long fromUserId, @PathVariable Long toUserId) {
        UserEntity fromUser = userService.getUser(fromUserId);
        UserEntity toUser = userService.getUser(toUserId);
        log.info("{}이 {}를 팔로우했습니다.", fromUser.getNickname(), toUser.getNickname());

        FollowEntity follow = FollowEntity.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .createdDate(LocalDateTime.now())
                .build();
        followRepository.save(follow);
        return ResponseEntity.ok("팔로우가 성공적으로 완료되었습니다.");
    }

    //팔로잉목록 - 닉네임, 프로필, 달성중인 습관갯수, 평균 달성률
    @GetMapping("followingList/me")
    ResponseEntity<List<FollowUserDto>> getFollowingList(@RequestParam("email") String email) {
        UserEntity byEmail = userService.findByEmail(email);
        List<FollowEntity> followingList = followService.getFollowingList(byEmail.getId());
        log.info("followingList :{}", followingList);
        List<FollowUserDto> followDtoList = followingList.stream().map(follow -> {
            UserEntity toUser = follow.getToUser();

            return getFollowUserDto(toUser);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(followDtoList);
    }

    private FollowUserDto getFollowUserDto(UserEntity toUser) {
        ProfileEntity profile = profileService.getProfile(toUser);
        List<HabitEntity> h = null;
        try {
            h = habitService.findUndeletedAndActiveHabits(toUser.getEmail());
            int totalRate = habitService.calculateAverageAchievementRate(h);
            return FollowUserDto.builder()
                    .id(toUser.getId())
                    .profileImageUrl(toUser.getProfileImage()) //다른 유저의 프로필 이미지
                    .nickname(profile.getNickname()) //다른 유저의 닉네임
                    .totalAchievementRate(totalRate) //다른 유저의 습관평균달성률
                    .totalHabitCount(h.size()).build(); //다른 유저의 습관갯수
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //팔로워목록 - 닉네임, 프로필, 달성중인 습관갯수, 평균 달성률
    @GetMapping("followerList/me")
    ResponseEntity<List<FollowUserDto>> getFollowerList(@RequestParam("email") String email) {
        UserEntity byEmail = userService.findByEmail(email);
        List<FollowEntity> followerList = followService.getFollowerList(byEmail.getId());
        log.info("followerList :{}", followerList);
        List<FollowUserDto> followerDtoList = followerList.stream().map(fwl -> {
            UserEntity fromUser = fwl.getFromUser();
            return getFollowUserDto(fromUser);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(followerDtoList);
    }

    //팔로우 삭제하기
    @DeleteMapping("{fromUserId}/unfollow/{toUserId}")
    //팔로우 취소
    ResponseEntity<String> unFollow(@PathVariable Long fromUserId, @PathVariable Long toUserId) {
        try {
            followService.unFollowUser(fromUserId, toUserId);
            return ResponseEntity.ok("언팔로우가 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
