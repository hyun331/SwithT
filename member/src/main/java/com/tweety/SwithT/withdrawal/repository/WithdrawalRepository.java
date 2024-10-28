package com.tweety.SwithT.withdrawal.repository;

import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.withdrawal.domain.WithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalRepository extends JpaRepository<WithdrawalRequest,Long> {

    List<WithdrawalRequest> findByMember(Member member);


}
