package com.itsu.threedays.config.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //Request Header에서 토큰 추출
        String token = resolveToken(request);
        log.info("jwt filter!");

        //Token 유효성 검사
        if (jwtTokenProvider.validateToken(token)){

            //토큰 인증받은 유저인 UsernamePasswordAuthenticiationToken을 리턴
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            log.info("authentication!");

            SecurityContextHolder.getContext().setAuthentication(auth); //토큰이 유효한 유저임 -> SecurityContext에 저장
        }

        filterChain.doFilter(request,response);
    }

    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader("authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
