package com.itsu.threedays.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("health")
public class HealthController {

    @GetMapping
    public ResponseEntity<String> getApi() {


        return ResponseEntity.ok("서버 정상");
    }

}
