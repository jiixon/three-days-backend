package com.itsu.threedays.controller;

import com.itsu.threedays.exception.NotFoundException;
import com.itsu.threedays.service.CertifyService;
import com.itsu.threedays.service.HabitService;
import com.itsu.threedays.service.S3uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("api/habits")
public class CertifyController {
    private final CertifyService certifyService;
    private final HabitService habitService;
    private final S3uploader s3uploader;

    @PostMapping(value = "{habitId}/certify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> certifyHabit(@PathVariable("habitId") Long habitId, @RequestParam("images") List<MultipartFile> images,
                                   @RequestParam("level") int level, @RequestParam("review") String review) {
        try {
            List<String> imageUrls = s3uploader.upload(images, "certify-image");
            certifyService.certifyHabit(habitId, review, level, imageUrls);

            //습관 인증 -> 습관테이블 변경사항(달성률, 달성횟수, 콤보횟수)
            habitService.updateAchievementAndCombo(habitId);

            return ResponseEntity.ok("습관이 인증되었습니다.");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("{certifyId}/certify")
    ResponseEntity<String> deleteCertify(@PathVariable("certifyId") Long certifyId) {
        if (certifyService.deleteCertification(certifyId)) {
            return ResponseEntity.ok("습관인증이 삭제되었습니다");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("습관인증ID가 존재하지않습니다");
        }
    }

}