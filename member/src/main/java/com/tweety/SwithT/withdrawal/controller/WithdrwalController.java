package com.tweety.SwithT.withdrawal.controller;
import com.tweety.SwithT.common.dto.CommonResDto;
import com.tweety.SwithT.withdrawal.dto.WithdrwalReqDto;
import com.tweety.SwithT.withdrawal.service.WithdrwalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WithdrwalController {

    private final WithdrwalService withdrwalService;

    public WithdrwalController(WithdrwalService withdrwalService) {
        this.withdrwalService = withdrwalService;
    }

    @PostMapping("/withdrwal")
    public ResponseEntity<CommonResDto> balanceWithdrawal(@RequestBody WithdrwalReqDto dto){

        System.out.println("컨트롤러 "+dto.getMemberId());
        System.out.println("컨트롤러 "+dto.getAmount());
        withdrwalService.WithdrwalRequest(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "출금 요청 성공", "요청 금액 :"+dto.getAmount());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);

    }
}
