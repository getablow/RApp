package org.zerock.b01.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.zerock.b01.security.CustomUserDetailsService;
//import org.zerock.b01.security.handler.Custom403Handler;
//import org.zerock.b01.security.handler.CustomSocialLoginSuccessHandler;

import static org.springframework.security.config.Customizer.withDefaults;


@Log4j2
@Configuration
@RequiredArgsConstructor
//@EnableMethodSecurity
public class CustomSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("-----------configure-------------");
        http
                .formLogin(withDefaults());
//        http
//                .formLogin(form -> { //로그인 화면에서 로그인 진행하는 설정......?
//
//            form.loginPage(withDefaults());
//
//        });


        return http.build();

        //return null;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){

        log.info("------------web configure--------------");

        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()); //정적자원 시큐리티해제

    }



}
