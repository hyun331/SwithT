package com.tweety.SwithT.member.service;

import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.member.domain.Role;
import com.tweety.SwithT.member.dto.CustomOAuth2User;
import com.tweety.SwithT.member.dto.GoogleResponse;
import com.tweety.SwithT.member.dto.MemberSaveReqDto;
import com.tweety.SwithT.member.dto.OAuth2Response;
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

    @Autowired
    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("여기까지옴??????????");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("넘어오는 유저 정보 찍어보기 (커스텀유저서비스단) -> "+oAuth2User);
        //어느 플랫폼에서 넘어오는 값인지 확인하기 위한 변수, 아래 메서드체이닝을 통해서 구글인지 카카오인지 값을 얻을 수 잇음.
        String registractionId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("서비스단입니다!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        OAuth2Response oAuth2Response = null;
        if (registractionId.equals("google")){
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String provider = oAuth2Response.getProvider(); // 공급 플랫폼
        String providerId = oAuth2Response.getProviderId(); //공급 플랫폼 id

//        Member existData = memberRepository.findByEmail(oAuth2Response.getEmail())
//                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        Member member;
        // 한번도 소셜 회원가입을 하지 않은 경우
//        if (existData == null) {

            member = Member.builder()
                    .provider(provider)
                    .privderId(providerId)
                    .name(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .role(Role.TUTEE)
                    .build();

            memberRepository.save(member);

            MemberSaveReqDto memberSaveReqDto = MemberSaveReqDto.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .name(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .role(Role.TUTEE)
                    .build();

            return new CustomOAuth2User(memberSaveReqDto);

//        } else { // 이미 소셜 회원가입을 한 경우
//            existData.setEmail(oAuth2Response.getEmail());
//            existData.setName(oAuth2Response.getName());
//
//            memberRepository.save(existData);
//
//            MemberSaveReqDto memberSaveReqDto = MemberSaveReqDto.builder()
//                    .provider(provider)
//                    .providerId(providerId)
//                    .name(oAuth2Response.getName())
//                    .email(oAuth2Response.getEmail())
//                    .role(existData.getRole())
//                    .build();
//
//
//            return new CustomOAuth2User(memberSaveReqDto);
//        }

    }



}
