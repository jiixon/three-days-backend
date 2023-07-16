package com.itsu.threedays.service;

import com.itsu.threedays.config.jwt.JwtTokenProvider;
import com.itsu.threedays.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) { //Refreshtoken으로 accesstoken 재발급

        UserEntity newUser = userService.fineByRefreshToken(refreshToken);

        return jwtTokenProvider.generateAccessToken(newUser);

    }

    public String createNewRefreshToken(String refreshToken) { //RefreshToken으로 RefreshToken으 재발급
        UserEntity user = userService.fineByRefreshToken(refreshToken);

        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
        user.setRefreshToken(newRefreshToken); //재발급한 refreshtoken 저장

        return newRefreshToken;

    }
}