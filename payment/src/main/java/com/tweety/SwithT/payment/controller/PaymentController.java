package com.tweety.SwithT.payment.controller;

import com.tweety.SwithT.common.dto.CommonErrorDto;
import com.tweety.SwithT.common.dto.CommonResDto;
import com.tweety.SwithT.payment.dto.BalanceResDto;
import com.tweety.SwithT.payment.dto.LecturePayResDto;
import com.tweety.SwithT.payment.dto.PaymentListDto;
import com.tweety.SwithT.payment.dto.RefundReqDto;
import com.tweety.SwithT.payment.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/complete")
    public ResponseEntity<?> updateApplyStatus(@RequestBody LecturePayResDto lecturePayResDto) {
        try {
            // 결제 완료 처리 및 강의 상태 업데이트
            CommonResDto response = paymentService.handleApplyStatus(lecturePayResDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        } catch (ResponseStatusException e) {
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getReason());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refund/{id}")
    public ResponseEntity<?> processRefund(@PathVariable Long id,
                                           @RequestBody RefundReqDto refundReqDto // 환불 요청에 필요한 데이터
    ) {
        try {
            // 환불 처리
            paymentService.refund(id, refundReqDto.getCancelReason());
            CommonResDto commonResDto = new CommonResDto(
                    HttpStatus.OK, "환불 처리 완료", refundReqDto);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 나의 결제 내역 조회 API 추가
    @GetMapping("/my-payments")
    public ResponseEntity<?> myPaymentsList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<PaymentListDto> payments = paymentService.myPaymentsList(page, size);
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (RuntimeException e) {
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list/balance")
    public ResponseEntity<?> myBalanceList(
            @PageableDefault(size = 10)Pageable pageable) {
        Page<BalanceResDto> balanceResDtos = paymentService.getBalanceList(pageable);

        CommonResDto commonResDto
                = new CommonResDto(HttpStatus.OK, "수익 리스트 조회 성공", balanceResDtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }
    @GetMapping("/balanceExpected")
    public ResponseEntity<?> expectedBalance(
            ) {

        CommonResDto commonResDto
                = new CommonResDto(HttpStatus.OK, "예상 수익금", paymentService.expectedBalance());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }
    @GetMapping("/chart/balance")
    public ResponseEntity<?> getBalanceChartData(@RequestParam(required = false, defaultValue = "6") int months) {
        CommonResDto commonResDto=new CommonResDto(HttpStatus.OK, "수익 그래프 조회 성공",  paymentService.getBalanceChartData(months));
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/hello")
    public String hello() {
        System.out.println("hello");
        return "hello";
    }
}