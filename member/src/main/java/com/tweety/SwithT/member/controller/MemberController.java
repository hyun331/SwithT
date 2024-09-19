package com.tweety.SwithT.member.controller;

import com.tweety.SwithT.common.Auth.JwtTokenProvider;
import com.tweety.SwithT.common.dto.CommonResDto;
import com.tweety.SwithT.member.dto.MemberSaveReqDto;
import com.tweety.SwithT.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class MemberController {

    @Value("${jwt.secretKeyRt}")
    private String secretKeyRt;

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;


    @Autowired
    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider, RedisTemplate<String, Object> redisTemplate) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/member/register")
    public ResponseEntity<CommonResDto> memberCreatePost(@RequestPart(value = "data") MemberSaveReqDto dto) {
        CommonResDto commonResDto
                = new CommonResDto(HttpStatus.OK, "회원가입 성공", memberService.memberCreate(dto).getId());

        return new ResponseEntity<>(commonResDto, HttpStatus.OK);

    }

}
