package com.tweety.SwithT.member.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        // 카카오는 'id' 필드에 제공자 아이디를 담고 있음
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        // 'kakao_account' 안에 이메일 정보가 들어 있음
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        return kakaoAccount.get("email").toString();
    }

    @Override
    public String getName() {
        // 'kakao_account'의 'profile' 안에 이름 정보가 들어 있음
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return profile.get("nickname").toString();
    }
}
