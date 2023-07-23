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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
@Slf4j
@Controller
public class KaKaoLoginController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    ResponseEntity<?> responseJwtToken(@RequestBody UserDto userDto) { //파베 토큰, 엑세스 토큰, 디바이스 아디 받아옴
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
                        //.profile_image(profileImageUrl)
                        .build();
                String accessToken = tokenProvider.generateAccessToken(user);
                log.info("accessToken: {}", accessToken);

                String refreshToken = tokenProvider.generateRefreshToken(user);
                log.info("refreshToken: {}", refreshToken);

                user.setRefreshToken(refreshToken);

                userService.saveUser(user);
                log.info("user: {}", user);

                return new ResponseEntity<>(KakaoUserInfoDto.builder()
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .tokenDto(TokenDto.builder().accessToken(accessToken).refreshToken(refreshToken).build())
                        .build(), HttpStatus.OK);

            }
//            else {
//                log.info("이미 등록된 회원");
//                if (!userIdService.getUserId(email).get().getFireBaseToken().equals(loginDto.getFirebaseToken())) { //파이어베이스 토큰 다르면 업데이트
//                    log.info("firebaseToken Update");
//                    User userId = userService.getUserId(email).get();
////                    userId.setFireBaseToken(userDto.getFirebaseToken());
//                    userService.saveUser(userId);
//                }
//
//                User updateUser = new User(email, "", Collections.singleton(simpleGrantedAuthority));
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(updateUser, userDto.getKakaoAccessToken(), Collections.singleton(simpleGrantedAuthority));
//                String token = tokenProvider.createToken(usernamePasswordAuthenticationToken);
//                log.info("token 발급: {}", token);
//                return new ResponseEntity<>(token, HttpStatus.OK);
//            }
        } catch (HttpClientErrorException e) {
            log.error("access token err : {}", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/test")
    public ResponseEntity<String> testApi(HttpServletRequest request) {
        String jwt = tokenProvider.resolveToken(request);
        log.info("testApi - jwt: {}", jwt);
        if (jwt == null) {
            return new ResponseEntity<>("JWT not found!", HttpStatus.BAD_REQUEST);
        }

        if (tokenProvider.validateToken(jwt)) {
            return new ResponseEntity<>("JWT is valid", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("JWT is invalid", HttpStatus.UNAUTHORIZED);
        }
    }

}