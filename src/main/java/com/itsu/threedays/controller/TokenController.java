package com.itsu.threedays.controller;

import com.itsu.threedays.config.jwt.JwtTokenProvider;
import com.itsu.threedays.dto.TokenDto;
import com.itsu.threedays.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Duration;

@RequiredArgsConstructor
@RequestMapping("api")
@Controller
@Slf4j
public class TokenController {

    /**
     * Accesstoken 만료 & Refreshtoken 유효 -> Accesstoken 새로 발급
     * Refreshtoken 만료기간 7days 이하로 남았으면 Refreshtoken도 재발급
     */

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;


    @PostMapping("/token")
    ResponseEntity<?> reissueToken(@RequestBody TokenDto tokenDto) {

        TokenDto responseTokenDto = new TokenDto();

        //Accesstoken 만료 & Refreshtoken 유효 -> Accesstoken 새로 발급
        if (!jwtTokenProvider.validateToken(tokenDto.getAccessToken()) && jwtTokenProvider.validateToken(tokenDto.getRefreshToken())) {

            String newAccessToken = tokenService.createNewAccessToken(tokenDto.getRefreshToken());
            log.info("newAccessToken: {}", newAccessToken);
            responseTokenDto.setAccessToken(newAccessToken); //새로 발급한 accesstoken dto에 저장

            //refreshtoken 7days 이하로 남아있는지 확인
            if (jwtTokenProvider.getExpiryDuration(tokenDto.getRefreshToken()).compareTo(Duration.ofDays(7)) < 0) {
                String newRefreshToken = tokenService.createNewRefreshToken(tokenDto.getRefreshToken());
                log.info("newRefreshToken: {}", newRefreshToken);
                responseTokenDto.setRefreshToken(newRefreshToken); //새로 발급한 refreshtoken dto에 저장
            }

        }

        if (responseTokenDto.getAccessToken() != null) {
            return ResponseEntity.ok().body(responseTokenDto);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}