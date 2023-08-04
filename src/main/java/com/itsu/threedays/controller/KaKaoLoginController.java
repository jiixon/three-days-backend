package com.itsu.threedays.controller;

import com.itsu.threedays.config.jwt.JwtTokenProvider;
import com.itsu.threedays.dto.KakaoUserInfoDto;
import com.itsu.threedays.dto.TokenDto;
import com.itsu.threedays.dto.UserDto;
import com.itsu.threedays.entity.UserEntity;
import com.itsu.threedays.entity.role.Role;
import com.itsu.threedays.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
@Slf4j
@Controller
public class KaKaoLoginController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private static final String DEFAULT_IMAGE_URL = "https://itsubucket.s3.ap-northeast-2.amazonaws.com/certify-image/threedays2023_image.png";

    @PostMapping("/login")
    ResponseEntity<KakaoUserInfoDto> responseJwtToken(@RequestBody UserDto userDto) { //파베 토큰, 엑세스 토큰, 디바이스 아디 받아옴
        String KAKAO_USERINFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + userDto.getKakaoAccessToken());
        //Bearer 이후 한칸 띄기(kakao) +  accesstoken 헤더에 넣어야함

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate = new RestTemplate(); //서버에서 다른서버로 연결할 때 쓰는 RestTemplate
        try {
            ResponseEntity<String> response = restTemplate.exchange(KAKAO_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
            //카카오에서 받은 거를 response로 담김
            log.info("response : {}", response);
            log.info("response.getBody() : {}", response.getBody());

            JSONObject jsonObject = new JSONObject(response.getBody());
            JSONObject kakao_account = jsonObject.getJSONObject("kakao_account");//kakao-account : 키값

            String email = kakao_account.getString("email");
            log.info("email : {}", email);
            String nickname = kakao_account.getJSONObject("profile").getString("nickname");
            log.info("nickname : {}", nickname);
            //String profileImageUrl = kakao_account.getJSONObject("profile").getString("profile_image_url");
            //log.info("profileImageUrl : {}", profileImageUrl);
            //security config - password는 암호화해서 저장

            if (!userService.isUserExist(email)) {
                log.info("회원가입 시작");
                UserEntity user = UserEntity.builder()
                        .role(Role.USER)
                        .email(email)
                        .nickname(nickname)
                        .password(new BCryptPasswordEncoder().encode(email))
                        .profileImage(DEFAULT_IMAGE_URL)
                        .build();
                String accessToken = tokenProvider.generateAccessToken(user);
                log.info("accessToken: {}", accessToken);

                String refreshToken = tokenProvider.generateRefreshToken(user);
                log.info("refreshToken: {}", refreshToken);

                user.setRefreshToken(refreshToken);

                userService.saveUser(user);
                log.info("user: {}", user);
                KakaoUserInfoDto kakaoUserInfoDto = new KakaoUserInfoDto();
                kakaoUserInfoDto.setUserId(user.getId());
                kakaoUserInfoDto.setEmail(user.getEmail());
                kakaoUserInfoDto.setTokenDto(TokenDto.builder().accessToken(accessToken).refreshToken(refreshToken).build());
                kakaoUserInfoDto.setNickname(user.getNickname());

                return ResponseEntity.ok(kakaoUserInfoDto);

            }

        } catch (HttpClientErrorException e) {
            log.error("access token err : {}", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}