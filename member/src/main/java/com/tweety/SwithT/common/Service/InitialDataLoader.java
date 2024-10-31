package com.tweety.SwithT.common.service;

import com.tweety.SwithT.member.domain.Gender;
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

		// ADMIN 계정
		if (memberRepository.findByEmail("admin@gmail.com").isEmpty()) {
			Member admin = Member.builder()
					.email("admin@gmail.com")
					.profileImage("https://minseong-file.s3.ap-northeast-2.amazonaws.com/member/%EC%9D%B4%EC%9E%AC%EC%9A%A9+%EC%82%AC%EC%A7%84.webp")
					.password(passwordEncoder.encode("12341234")) // 비밀번호 인코딩
					.name("AdminUser")
					.birthday(LocalDate.now())
					.phoneNumber("010-1234-5678")
					.role(Role.ADMIN) // ADMIN
					.availableMoney(0L)
					.address("서울특별시 보라매로 87")
					.detailAddress("산이빌딩 4층")
					.build();
			memberRepository.save(admin); // 관리자 계정
		}

		// TUTEE
		if (memberRepository.findByEmail("tutee1@gmail.com").isEmpty()) {
			for (int i = 1; i <= 30; i++) {
				Member user = Member.builder()
						.email("tutee" + i + "@gmail.com")
						.profileImage("https://minseong-file.s3.ap-northeast-2.amazonaws.com/member/%EB%82%A8%EC%9E%90%EB%94%94%ED%8F%B4%ED%8A%B8%EC%9D%B4%EB%AF%B8%EC%A7%80%EC%83%98%ED%94%8C%ED%8C%8C%EC%9D%BC.jpg")
						.password(passwordEncoder.encode("12341234")) // 비밀번호는 동일
						.name("TUTEE_USER"+i)
						.birthday(LocalDate.now())
						.phoneNumber("010-1234-567"+i+"")
						.role(Role.TUTEE)
						.address("서울특별시 보라매로 87")
						.detailAddress("산이빌딩 4층")
						.gender(Gender.MAN)
						.availableMoney(0L)
						.build();
				memberRepository.save(user);
			}
		}

	}

}
