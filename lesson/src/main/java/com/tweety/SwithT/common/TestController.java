package com.tweety.SwithT.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/lesson")
    public String test(){
        System.out.println("lesson에 잘 옵니다.");
        return "lesson에 잘 옵니다.";
    }

}
