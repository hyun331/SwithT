package com.tweety.SwithT.withdrawal.repository;

import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.withdrawal.domain.WithdrawalRequest;
import com.twilio.rest.api.v2010.account.Balance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WithdrawalRepository extends JpaRepository<WithdrawalRequest,Long> {

    Page<WithdrawalRequest> findByMember(Member member, Pageable pageable);
    List<WithdrawalRequest> findByMemberIdAndDelYn(Long memberId, String delYn);
    List<WithdrawalRequest> findByMemberIdAndDelYnAndRequestTimeBetween(
            Long memberId, String delYn, LocalDateTime startDate, LocalDateTime endDate
    );


}
