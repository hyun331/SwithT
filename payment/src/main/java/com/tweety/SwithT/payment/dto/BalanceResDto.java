package com.tweety.SwithT.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResDto {
    private Long incomeAmount;
    private LocalDateTime createdTime;
    private String description;


}
