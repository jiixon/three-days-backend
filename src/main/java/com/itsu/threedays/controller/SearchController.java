package com.itsu.threedays.controller;

import com.itsu.threedays.dto.SearchDto;
import com.itsu.threedays.entity.HabitEntity;
import com.itsu.threedays.entity.ProfileEntity;
import com.itsu.threedays.service.HabitService;
import com.itsu.threedays.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("api")
public class SearchController {
    private final HabitService habitService;
    private final ProfileService profileService;


    @GetMapping("/search")
    ResponseEntity<List<SearchDto>> searchUserByHabitTitle(@RequestParam("habitTitle") String habitTitle) {
        List<HabitEntity> habits = habitService.getContentsByTitle(habitTitle);

        List<SearchDto> searchDtoList = habits.stream()
                .map(habit -> {
                    ProfileEntity profile = profileService.getProfile(habit.getUserId());
                    SearchDto searchDto = new SearchDto();
                    searchDto.setNickname(profile.getNickname());
                    searchDto.setUserId(habit.getUserId().getId());
                    searchDto.setKakaoProfileUrl(habit.getUserId().getProfileImage());
                    searchDto.setTitle(habit.getTitle());
                    return searchDto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(searchDtoList);
    }
}
