package com.tweety.SwithT.member.service;

import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.member.dto.CustomOAuth2User;
import com.tweety.SwithT.member.dto.GoogleResponse;
import com.tweety.SwithT.member.dto.KakaoResponse;
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

    private final MemberRepository memberRepository;

    @Autowired
    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("!!!!!!!!!!!!!LOADUSER!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(oAuth2User.getAttributes());
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            throw new IllegalArgumentException("Unsupported provider: " + registrationId);
        }

        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();
        String socialName = oAuth2Response.getName();
        String socialEmail = oAuth2Response.getEmail();

        Member member = memberRepository.findByEmail(socialEmail)
                .map(existingMember -> updateExistingMember(existingMember, socialName))
                .orElse(createSocialMember(provider, providerId, socialName, socialEmail));
        memberRepository.save(member);

        return new CustomOAuth2User(member);
    }

    private Member updateExistingMember(Member existingMember, String socialName) {
        existingMember.setName(socialName);
        return existingMember;
    }

    private Member createSocialMember(String provider, String providerId, String name, String email) {
        return Member.builder()
                .provider(provider)
                .providerId(providerId)
                .name(name)
                .email(email)
                .build();
    }
}
