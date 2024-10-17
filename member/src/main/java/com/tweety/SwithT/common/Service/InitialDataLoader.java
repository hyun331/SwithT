package com.tweety.SwithT.common.service;

import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.member.domain.Role;
import com.tweety.SwithT.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class InitialDataLoader implements CommandLineRunner {

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private MemberRepository memberRepository;

	@Override
	public void run(String... args) throws Exception {

		// ADMIN 계정 생성
		if (memberRepository.findByEmail("admin@gmail.com").isEmpty()) {
			Member admin = Member.builder()
					.email("admin@gmail.com")
					.profileImage("https://minseong-file.s3.ap-northeast-2.amazonaws.com/member/%EC%9D%B4%EC%9E%AC%EC%9A%A9+%EC%82%AC%EC%A7%84.webp")
					.password(passwordEncoder.encode("12341234")) // 비밀번호 인코딩
					.name("AdminUser")
					.birthday(LocalDate.now())
					.phoneNumber("010-1234-5678")
					.role(Role.ADMIN) // ADMIN 역할
					.build();
			memberRepository.save(admin); // 관리자 계정
		}
	}

}
