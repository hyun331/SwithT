package com.tweety.SwithT.withdrawal.controller;

import com.tweety.SwithT.common.dto.CommonResDto;
import com.tweety.SwithT.withdrawal.dto.WithdrawalReqDto;
import com.tweety.SwithT.withdrawal.dto.WithdrawalResDto;
import com.tweety.SwithT.withdrawal.service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @Autowired
    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<CommonResDto> balanceWithdrawal(@RequestBody WithdrawalReqDto dto) {

        withdrawalService.WithdrwalRequest(dto);
        CommonResDto commonResDto
                = new CommonResDto(HttpStatus.OK, "출금 요청 성공", "요청 금액 :" + dto.getAmount() + " 요청 시각 :" + dto.getRequestTime());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);

    }

    @GetMapping("/list/withdrawal")
    public ResponseEntity<CommonResDto> getWithdrawalRequest(@PageableDefault(size = 10) Pageable pageable) {

        Page<WithdrawalResDto> withdrawalResDtos = withdrawalService.getRequestList(pageable);

        CommonResDto commonResDto
                = new CommonResDto(HttpStatus.OK, "출금 리스트 조회 성공", withdrawalResDtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);


    }
    @GetMapping("/chart/withdrawal")
    public ResponseEntity<CommonResDto> getWithdrawalChartData(@RequestParam(required = false, defaultValue = "6") int months) {
        CommonResDto commonResDto
                = new CommonResDto(HttpStatus.OK, "출금 그래프 조회 성공", withdrawalService.getChartList(months));
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);


    }

}
