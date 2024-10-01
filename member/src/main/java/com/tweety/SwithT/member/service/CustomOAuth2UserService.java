package com.tweety.SwithT.member.service;

import com.tweety.SwithT.member.domain.Role;
import com.tweety.SwithT.member.dto.*;
import com.tweety.SwithT.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    // DefaultOAuth2UserService 상속한 이 클래스에서 소셜 유저 정보를 획득하기 위한 메서드가 명시되어 있다.
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Autowired
    public CustomOAuth2UserService(MemberRepository memberRepository, MemberService memberService) {
        this.memberRepository = memberRepository;
        this.memberService = memberService;
    }


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("여기까지옴??????????");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("넘어오는 유저 정보 찍어보기 (커스텀유저서비스단) -> "+oAuth2User);
        //어느 플랫폼에서 넘어오는 값인지 확인하기 위한 변수, 아래 메서드체이닝을 통해서 구글인지 카카오인지 값을 얻을 수 잇음.
        String registractionId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("서비스단입니다!!!!!!!!!!!!!");

        OAuth2Response oAuth2Response = null;

        if (registractionId.equals("google")){
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else if (registractionId.equals("kakao")){
            //추후 카카오로 수정하기
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        System.out.println("카카오 로그인 테스트");
        String provider = oAuth2Response.getProvider(); // 소셜 플랫폼
        String providerId = oAuth2Response.getProviderId(); //소셜 플랫폼 id
        String socialName = oAuth2Response.getName(); // 소셜플랫폼 사용자 이름
        String socialEmail = oAuth2Response.getEmail(); // 소셜 플랫폼 이메일


        MemberSaveReqDto memberSaveReqDto = MemberSaveReqDto.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .name(socialName)
                    .email(socialEmail)
                    .role(Role.TUTEE)
                    .build();

        memberService.SocialMemberCreate(memberSaveReqDto);

        return new CustomOAuth2User(memberSaveReqDto);
    }

}
