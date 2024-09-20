package com.tweety.SwithT.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/mt")
    public String test() {
        System.out.println("mt까지 옵니다");
        return "여기 위치 mt 입니다";
    }

}
