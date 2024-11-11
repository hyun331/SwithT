package com.tweety.SwithT.payment.repository;

import com.tweety.SwithT.payment.domain.Balance;
import com.tweety.SwithT.payment.domain.Payments;
import com.tweety.SwithT.payment.domain.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    List<Balance> findByStatus(Status status);
    Balance findByPayments(Payments payments);
    List<Balance> findByMemberId(Long memberId);

    Page<Balance> findByMemberIdAndDelYn(Long memberId, String delYn, Pageable pageable);
    List<Balance> findByMemberIdAndDelYn(Long memberId, String delYn);
    List<Balance> findByMemberIdAndDelYnAndBalancedTimeBetween(
            Long memberId, String delYn, LocalDateTime startDate, LocalDateTime endDate);
    List<Balance> findByMemberIdAndStatus(Long memberId, Status status);
}

