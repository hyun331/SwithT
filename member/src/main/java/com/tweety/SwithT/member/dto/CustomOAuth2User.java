package com.tweety.SwithT.member.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final MemberSaveReqDto memberSaveReqDto;

    public CustomOAuth2User(MemberSaveReqDto memberSaveReqDto) {
        this.memberSaveReqDto = memberSaveReqDto;
    }

    @Override
    public Map<String, Object> getAttributes() {
        // 구글과 네이버의 데이터 반환이 동일하지 않기 떄문에 사용하지 않겟다. 여기에 OAuth2 를 통한 데이터가 담기는 것
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // getAuthorities는 Role 값을 담고있는 메서드
        Collection<GrantedAuthority> collection = new ArrayList<>();

        // new를 통해서 GrantedAuthority()를 선택하면 자동으로 메서드가 생성된다.
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return memberSaveReqDto.getRole().toString(); // 추후 문제될시 살펴보기
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return memberSaveReqDto.getName();
    }

    public String getEmail(){

        return memberSaveReqDto.getEmail();
    }

    public String getProvider(){
        return memberSaveReqDto.getProvider();
    }

    public String getProviderId(){
        return memberSaveReqDto.getProviderId();
    }

}
