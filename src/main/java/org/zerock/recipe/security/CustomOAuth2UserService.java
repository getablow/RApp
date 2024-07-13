package org.zerock.recipe.security;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.recipe.domain.Member;
import org.zerock.recipe.domain.MemberRole;
import org.zerock.recipe.domain.Refrigerator;
import org.zerock.recipe.repository.MemberRepository;
import org.zerock.recipe.repository.RefrigeratorRepository;
import org.zerock.recipe.security.dto.MemberSecurityDTO;
import org.zerock.recipe.service.MemberService;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefrigeratorRepository refrigeratorRepository;
    private final MemberService memberService;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{

//        log.info("userRequest..........");
//        log.info(userRequest);

//        log.info("oauth2 user...........................");

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();

//        log.info("NAME: " + clientName);
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> paramMap = oAuth2User.getAttributes();

        String email = null;

        switch (clientName){
            case "kakao":
                email = getKakaoEmail(paramMap);
                break;
        }

//        log.info("======================================");
//        log.info(email);
//        log.info("======================================");


        return generateDTO(email, paramMap);

    }

    private MemberSecurityDTO generateDTO(String email, Map<String, Object> params){

        Optional<Member> result = memberRepository.findByEmail(email);

        String generatedPassword = RandomStringUtils.randomAlphanumeric(10);

        //if there is no user of that email in db
        if(result.isEmpty()) {
            //add member
            Member member = Member.builder()
                    .mid(email)
                    .mpw(passwordEncoder.encode(generatedPassword))
                    .email(email)
                    .social(true)
                    .build();
            member.addRole(MemberRole.USER);
            member = memberRepository.save(member);

            try {
                Refrigerator refrigerator = memberService.createRefrigerator(member);
                member.setRefrigerator(refrigerator);
                member = memberRepository.save(member);
            } catch (Exception e) {
                log.error("Error creating refrigerator for member: {}" , member.getMid(), e);
            }



            //configuration and return MemberSecurityDTO
            /*MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(
                    email, generatedPassword, email, false, true, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            memberSecurityDTO.setProps(params);*/

            MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(
                    member.getMid(),
                    generatedPassword,
                    member.getEmail(),
                    member.isDel(),
                    member.isSocial(),
                    Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))
            );
            memberSecurityDTO.setProps(params);

            return memberSecurityDTO;

        } else {
            Member member = result.get();
            MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(
                    member.getMid(),
                    member.getMpw(),
                    member.getEmail(),
                    member.isDel(),
                    member.isSocial(),
                    member.getRoleSet()
                            .stream().map(memberRole -> new SimpleGrantedAuthority("ROLE_" + memberRole.name())).collect(Collectors.toList())
            );

            return memberSecurityDTO;
        }
    }


    private String getKakaoEmail(Map<String, Object> paramMap) {

//        log.info("KAKAO----------------------------------------");

        Object value = paramMap.get("kakao_account");

//        log.info(value);

        LinkedHashMap accountMap = (LinkedHashMap) value;

        String email = (String)accountMap.get("email");

//        log.info("email..." + email);

        return email;

    }
}
