package com.tweety.SwithT.withdrawal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalResDto {

    private Long requestAmount;
    private LocalDateTime requestTime;
    private String description;

}
