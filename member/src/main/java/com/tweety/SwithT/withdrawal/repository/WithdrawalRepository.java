package com.tweety.SwithT.withdrawal.repository;

import com.tweety.SwithT.withdrawal.domain.WithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawalRepository extends JpaRepository<WithdrawalRequest,Long> {

}
