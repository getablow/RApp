package org.zerock.recipe.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.zerock.recipe.domain.Member;
import org.zerock.recipe.repository.MemberRepository;
import org.zerock.recipe.security.dto.MemberSecurityDTO;

import java.io.IOException;
import java.util.Optional;


@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("------------------------------------------------------------");
        log.info("CustomLoginSuccessHandler onAuthenticationSuccess............");
        log.info(authentication.getPrincipal());

        MemberSecurityDTO memberSecurityDTO = (MemberSecurityDTO) authentication.getPrincipal();

        Optional<Member> optionalMember = memberRepository.findByEmail(memberSecurityDTO.getEmail());

        if (optionalMember.isEmpty()) {
            response.sendRedirect("/error");
            return;
        }

        Member member = optionalMember.get();
        String encodePw = memberSecurityDTO.getMpw();

        String initialPassword = RandomStringUtils.randomAlphanumeric(10);

        if(memberSecurityDTO.isSocial() && passwordEncoder.matches(initialPassword, encodePw)){
            log.info("Should Change Password");
            log.info("Redirect to Member Modify");
            response.sendRedirect("/member/modify");
            return;
        } else {
            response.sendRedirect("/recipe/personalPage");
        }

    }


}
