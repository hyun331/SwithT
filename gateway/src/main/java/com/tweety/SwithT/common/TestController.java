package com.tweety.SwithT.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        System.out.println("여기까지 잘 옵니다 여긴 게이트웨이 서버");
        return "test";
    }
}
