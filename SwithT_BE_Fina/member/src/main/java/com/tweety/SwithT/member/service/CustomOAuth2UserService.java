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
import org.springframework.security.oauth2.core.OAuth2Error;
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
        try {
            // Super method to load the user information from the OAuth2 provider
            OAuth2User oAuth2User = super.loadUser(userRequest);

            // Debugging: Log the OAuth2 attributes
            System.out.println("OAuth2 User Attributes: " + oAuth2User.getAttributes());

            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            OAuth2Response oAuth2Response = null;

            // Identify the provider and create the appropriate response object
            if (registrationId.equals("google")) {
                oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            } else if (registrationId.equals("kakao")) {
                oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
            } else {
                throw new IllegalArgumentException("Unsupported provider: " + registrationId);
            }

            // Extract the information from the OAuth2 response
            String provider = oAuth2Response.getProvider();
            String providerId = oAuth2Response.getProviderId();
            String socialName = oAuth2Response.getName();
            String socialEmail = oAuth2Response.getEmail();

            // Debugging: Log extracted information
            System.out.println("Provider: " + provider);
            System.out.println("Provider ID: " + providerId);
            System.out.println("Name: " + socialName);
            System.out.println("Email: " + socialEmail);

            Member member = memberRepository.findByEmail(socialEmail)
                    .map(existingMember -> updateExistingMember(existingMember, socialEmail))
                    .orElse(createSocialMember(provider, providerId, socialName, socialEmail));
            memberRepository.save(member);

            return new CustomOAuth2User(member);

        } catch (OAuth2AuthenticationException ex) {
            // Log the exception details
            System.err.println("OAuth2AuthenticationException occurred: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            // Catch any other exceptions and log details for debugging
            System.err.println("An error occurred while loading the user: " + ex.getMessage());
            ex.printStackTrace();
            throw new OAuth2AuthenticationException(new OAuth2Error("token_error", "Failed to load user or obtain token", null), ex);
        }
    }

    private Member updateExistingMember(Member existingMember, String socialEmail) {
        System.out.println("Existing member found, updating email: " + existingMember.getEmail());
        existingMember.setEmail(socialEmail);
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