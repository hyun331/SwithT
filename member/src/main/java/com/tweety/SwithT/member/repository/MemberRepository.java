package com.tweety.SwithT.member.repository;

import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.member.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    List<Member> findAllByRole(Role role);
    Optional<Member> findByProviderAndProviderId(String provider,String providerId);

}
