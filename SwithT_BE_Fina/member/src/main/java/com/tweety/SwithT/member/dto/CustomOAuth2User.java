package com.tweety.SwithT.member.dto;

import com.tweety.SwithT.member.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final Member member;

    @Autowired
    public CustomOAuth2User(Member member) {
        this.member = member;
    }

    @Override
    public Map<String, Object> getAttributes() {
        // OAuth2User 정보를 담는 Map을 생성하여 반환
        return Map.of(
                "email", member.getEmail(),
                "provider", member.getProvider(),
                "providerId", member.getProviderId(),
                "name", member.getName(),
                "role", member.getRole()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // getAuthorities는 Role 값을 담고있는 메서드
        Collection<GrantedAuthority> collection = new ArrayList<>();

        // new를 통해서 GrantedAuthority()를 선택하면 자동으로 메서드가 생성된다.
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return member.getRole().toString(); // 추후 문제될시 살펴보기
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        return member.getName();
    }

    public String getEmail(){
        return member.getEmail();
    }

    public String getProvider(){
        return member.getProvider();
    }

    public String getProviderId(){
        return member.getProviderId();
    }

}
