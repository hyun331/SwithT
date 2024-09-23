package com.tweety.SwithT.member.controller;

import com.tweety.SwithT.common.Auth.JwtTokenProvider;
import com.tweety.SwithT.common.dto.CommonErrorDto;
import com.tweety.SwithT.common.dto.CommonResDto;
import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.member.dto.MemberLoginDto;
import com.tweety.SwithT.member.dto.MemberRefreshDto;
import com.tweety.SwithT.member.dto.MemberSaveReqDto;
import com.tweety.SwithT.member.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class MemberController {

    @Value("${jwt.secretKeyRt}")
    private String secretKeyRt;

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    @Qualifier("2")
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public MemberController(MemberService memberService
            , JwtTokenProvider jwtTokenProvider
            , @Qualifier("2") RedisTemplate<String, Object> redisTemplate) {


        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;

    }

    @PostMapping("/member/create")
    public ResponseEntity<?> memberCreate(@Valid @RequestBody MemberSaveReqDto dto) {

        System.out.println("여기까지 요청이 오나?");
        try {
            Member member = memberService.memberCreate(dto);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED,
                    "Member is successfully created", "Member number is " + member.getId());

            return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);

        } catch (Exception e) {

            CommonResDto errorResponse = new CommonResDto(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create member", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody MemberLoginDto dto){

        Member member = memberService.login(dto);

        // AccesToken
        String jwtToken = jwtTokenProvider.createToken(String.valueOf(member.getId()),member.getEmail(), member.getRole().toString());
        // RefreshToken
        String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(member.getId()),member.getEmail(), member.getRole().toString());

        redisTemplate.opsForValue().set(member.getEmail(), refreshToken, 240, TimeUnit.HOURS); // 240시간

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        loginInfo.put("refreshToken", refreshToken);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Login is successful", loginInfo);

        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> generateNewAccessToken(@RequestBody MemberRefreshDto dto) {

        //왜 try catch로 감싸나? 여기서 검증 실패나면 에러가 발생하고 예외가 터지니까 잡아내야한다.
        String rt = dto.getRefreshToken();
        Claims claims = null;
        try {
            //여기서는 코드를 통해서 rt를 검증하는 것 이고
            claims = Jwts.parser().setSigningKey(secretKeyRt).parseClaimsJws(rt).getBody(); //이 한줄이 검증을 위한 한줄이다

        } catch (Exception e) {
            return new ResponseEntity<>(
                    new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), "invalid refresh Token"), HttpStatus.BAD_REQUEST);
        }

        // claims에서 이메일 꺼낼 수잇다. 이거 왜 꺼낸거임?
        String id = claims.getId();
        String email = claims.getSubject();
        String role = claims.get("role").toString();

        //여기서는 redis를 조회하여 rt를 추가 검증하는 것 이다. 그럼 여기서 redis를 조회해봐야겟지? 당연히 이메일로 조회해야겟지?
        // 이메일을 넣어서 가지고 오자, object이기 때문에 형 변환 해야한다. 우선은 toString으로 했음.
        Object obj = redisTemplate.opsForValue().get(email); // rt를 검증했을 때 500에러가 나기 때문에 401 에러를 내기 위해서 코드를 변형했다.

        if ( obj == null || !obj.toString().equals(rt)){
            return new ResponseEntity<>(
                    new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), "invalid refresh Token"), HttpStatus.BAD_REQUEST);
        }
        String newAt = jwtTokenProvider.createToken(id,email, role);
        Map<String, Object> info = new HashMap<>();
        info.put("token", newAt);

        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "At is renewed", info);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);

    }

    @GetMapping("/hi")
    public String test(){
        System.out.println("hello");
        return "hello";
    }

}
