package com.itsu.threedays.controller;

import com.itsu.threedays.dto.CertifyDto;
import com.itsu.threedays.dto.FeedItemDto;
import com.itsu.threedays.dto.UserProfileDto;
import com.itsu.threedays.dto.UserProfileHabitDto;
import com.itsu.threedays.entity.*;
import com.itsu.threedays.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/feed")
@Slf4j
public class FeedController {

    private final UserService userService;
    private final ProfileService profileService;
    private final HabitService habitService;
    private final FollowService followService;
    private final CertifyService certifyService;

    //내프로필 피드 보기
    @GetMapping("profile/me")
    ResponseEntity<UserProfileDto> getMyProfileFeed(@RequestParam("email") String email) throws Exception {

        UserEntity byEmail = userService.findByEmail(email);
        ProfileEntity profileByEmail = profileService.getProfile(byEmail);
        List<HabitEntity> undeletedAndActiveHabits = habitService.findUndeletedAndActiveHabits(email); //그만두지 않고, 삭제도 하지않은 습관목록리스트
        log.info("그만두지 않고, 삭제도 하지않은 습관목록리스트: {}", undeletedAndActiveHabits);
        int totalAchievementRate = habitService.calculateAverageAchievementRate(undeletedAndActiveHabits);
        int followerCount = followService.getFollowerList(byEmail.getId()).size();

        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setKakaoImageUrl(byEmail.getProfileImage()); //카카오프로필
        userProfileDto.setNickname(profileByEmail.getNickname()); //닉네임(유저가 정한)
        userProfileDto.setKeywords(profileByEmail.getKeywords()); //키워드(유저가 정한)
        userProfileDto.setTotalAchievementRate(totalAchievementRate); //달성률(달성중인 습관의 평균)
        userProfileDto.setTotalHabitCount(undeletedAndActiveHabits.size()); //달성중인 습관갯수
        userProfileDto.setFollowerCount(followerCount); //팔로워 수
        userProfileDto.setFollowing(false);

        // 습관들과 인증 정보를 조회하여 UserProfileHabitDto와 CertifyDto에 담고, UserProfileDto에 추가
        List<UserProfileHabitDto> habitList = new ArrayList<>();
        for (HabitEntity habit : undeletedAndActiveHabits) {
            UserProfileHabitDto userProfileHabitDto = new UserProfileHabitDto();
            userProfileHabitDto.setHabitId(habit.getId());
            userProfileHabitDto.setTitle(habit.getTitle());
            userProfileHabitDto.setCreatedHabit(habit.getCreatedDate()); //습관이 만들어진 날짜로 저장
            try {
                List<CertifyEntity> certifiesByHabitId = certifyService.getCertifiesByHabitId(habit.getId());

                List<CertifyDto> certifyDtos = new ArrayList<>();
                for (CertifyEntity certify : certifiesByHabitId) {
                    CertifyDto certifyDto = new CertifyDto();
                    certifyDto.setCertifyId(certify.getId());
                    certifyDto.setReview(certify.getReview());
                    certifyDto.setLevel(certify.getLevel());
                    certifyDto.setCertifiedDate(certify.getCreatedDate()); //습관인증한 날짜로 저장

                    // CertifyEntity의 이미지 목록인 images에서 imageUrl을 추출하여 CertifyDto에 추가
                    List<String> imageUrls = new ArrayList<>();
                    for (CertifyImageEntity image : certify.getImages()) {
                        imageUrls.add(image.getImageUrl());
                    }
                    certifyDto.setImagUrls(imageUrls);
                    certifyDtos.add(certifyDto);
                }
                userProfileHabitDto.setCertifyDtos(certifyDtos);
            } catch (Exception e) {
                // 인증 정보가 없는 경우, 빈 목록 또는 null로 처리
                userProfileHabitDto.setCertifyDtos(Collections.emptyList());
            }
            habitList.add(userProfileHabitDto);

        }
        userProfileDto.setHabitList(habitList);

        return ResponseEntity.ok(userProfileDto);
    }

    //다른 유저의 프로필 피드보기
    @GetMapping("profile/{userId}")
    ResponseEntity<UserProfileDto> getUserProfileFeed(@PathVariable Long userId,
                                                      @RequestParam("email") String email) throws Exception {
        UserEntity byEmail = userService.findByEmail(email);
        List<FollowEntity> followingList = followService.getFollowingList(byEmail.getId());

        // 현재 보고 있는 프로필 페이지의 사용자를 팔로우했는지 여부 확인
        boolean isFollowing = followingList.stream()
                .anyMatch(follow -> follow.getToUser().getId().equals(userId));

        UserEntity user = userService.getUser(userId);
        ProfileEntity profileByEmail = profileService.getProfile(user);
        List<HabitEntity> undeletedAndActiveHabits = habitService.findUndeletedAndActiveHabits(user.getEmail()); //그만두지 않고, 삭제도 하지않은 습관목록리스트
        List<HabitEntity> publicHabitsByUserId = habitService.findPublicHabitsByUserId(userId); //그만두지X, 삭제X 이면서 공개인 습관목록리스트

        log.info("그만두지 않고, 삭제도 하지않고 공개인 습관목록리스트: {}", publicHabitsByUserId);
        int totalAchievementRate = habitService.calculateAverageAchievementRate(undeletedAndActiveHabits);
        int followerCount = followService.getFollowerList(user.getId()).size();

        log.info("카카오프로필: {}", user.getProfileImage());
        log.info("닉네임: {}", profileByEmail.getNickname());
        log.info("키워드: {}", profileByEmail.getKeywords());
        log.info("달성률(달성중인 습관의 평균): {}", totalAchievementRate);
        log.info("달성중인 습관갯수 : {}", undeletedAndActiveHabits.size());
        log.info("팔로워 수 : {}", followerCount);
        log.info("내가 이 유저를 팔로우했는지 여부: {}", isFollowing);

        UserProfileDto userProfileDto = new UserProfileDto();

        userProfileDto.setKakaoImageUrl(user.getProfileImage()); //카카오프로필
        userProfileDto.setNickname(profileByEmail.getNickname()); //닉네임(유저가 정한)
        userProfileDto.setKeywords(profileByEmail.getKeywords()); //키워드(유저가 정한)
        userProfileDto.setTotalAchievementRate(totalAchievementRate); //달성률(달성중인 습관의 평균)
        userProfileDto.setTotalHabitCount(undeletedAndActiveHabits.size()); //달성중인 습관갯수
        userProfileDto.setFollowerCount(followerCount); //팔로워 수
        userProfileDto.setFollowing(isFollowing);

        // 습관들과 인증 정보를 조회하여 UserProfileHabitDto와 CertifyDto에 담고, UserProfileDto에 추가
        List<UserProfileHabitDto> habitList = new ArrayList<>();
        for (HabitEntity habit : publicHabitsByUserId) {
            UserProfileHabitDto userProfileHabitDto = new UserProfileHabitDto();
            userProfileHabitDto.setHabitId(habit.getId());
            userProfileHabitDto.setTitle(habit.getTitle());
            userProfileHabitDto.setCreatedHabit(habit.getCreatedDate()); //습관이 만들어진 날짜로 저장

            try {
                List<CertifyEntity> certifiesByHabitId = certifyService.getCertifiesByHabitId(habit.getId());

                List<CertifyDto> certifyDtos = new ArrayList<>();
                for (CertifyEntity certify : certifiesByHabitId) {
                    CertifyDto certifyDto = new CertifyDto();
                    certifyDto.setCertifyId(certify.getId());
                    certifyDto.setReview(certify.getReview());
                    certifyDto.setLevel(certify.getLevel());
                    certifyDto.setCertifiedDate(certify.getCreatedDate()); //습관인증한 날짜로 저장

                    // CertifyEntity의 이미지 목록인 images에서 imageUrl을 추출하여 CertifyDto에 추가
                    List<String> imageUrls = new ArrayList<>();
                    for (CertifyImageEntity image : certify.getImages()) {
                        imageUrls.add(image.getImageUrl());
                    }
                    certifyDto.setImagUrls(imageUrls);
                    certifyDtos.add(certifyDto);
                }
                userProfileHabitDto.setCertifyDtos(certifyDtos);
            } catch (Exception e) {
                // 인증 정보가 없는 경우, 빈 목록 또는 null로 처리
                userProfileHabitDto.setCertifyDtos(Collections.emptyList());
            }
            habitList.add(userProfileHabitDto);

        }
        userProfileDto.setHabitList(habitList);

        return ResponseEntity.ok(userProfileDto);
    }

    @GetMapping("users")
//메인페이지 피드
    ResponseEntity<List<FeedItemDto>> getUserFeed(@RequestParam("email") String email) throws Exception {
        UserEntity byEmail = userService.findByEmail(email); //이메일로 유저 접근
        List<UserEntity> followingUsers = followService.getFollowingUsers(byEmail.getId());
        List<CertifyEntity> allCertifies = new ArrayList<>();

        // 모든 팔로우한 유저의 인증 정보를 가져와서 리스트에 추가
        for (UserEntity user : followingUsers) {
            List<HabitEntity> habits = habitService.findPublicHabitsByUserId(user.getId());
            for (HabitEntity habit : habits) {
                List<CertifyEntity> certifies = certifyService.getCertifiesByHabitId(habit.getId());
                allCertifies.addAll(certifies);
            }
        }

        // 인증 정보를 최신 순서로 정렬
        allCertifies.sort(Comparator.comparing(CertifyEntity::getCreatedDate).reversed());

        List<FeedItemDto> feedItemDtos = allCertifies.stream()
                .map(certify -> {
                    FeedItemDto feedItemDto = new FeedItemDto();
                    feedItemDto.setUserId(certify.getHabit().getUserId().getId());
                    feedItemDto.setKakaoImageUrl(certify.getHabit().getUserId().getProfileImage());
                    ProfileEntity profile = profileService.getProfile(certify.getHabit().getUserId());
                    feedItemDto.setNickname(profile.getNickname());
                    feedItemDto.setTitle(certify.getHabit().getTitle());

                    // CertifyEntity의 이미지 목록인 images에서 imageUrl을 추출하여 CertifyDto에 추가
                    List<String> imageUrls = certify.getImages().stream()
                            .map(CertifyImageEntity::getImageUrl)
                            .collect(Collectors.toList());
                    feedItemDto.setCertifyImages(imageUrls);

                    feedItemDto.setReview(certify.getReview());
                    feedItemDto.setLevel(certify.getLevel());
                    feedItemDto.setCreatedHabit(certify.getHabit().getCreatedDate());
                    feedItemDto.setCertifiedDate(certify.getCreatedDate());

                    return feedItemDto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(feedItemDtos);
    }
}
