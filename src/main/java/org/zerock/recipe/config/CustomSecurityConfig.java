package org.zerock.recipe.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.zerock.recipe.security.CustomUserDetailsService;
import org.zerock.recipe.security.handler.Custom403Handler;
import org.zerock.recipe.security.handler.CustomLoginSuccessHandler;
import org.zerock.recipe.security.handler.CustomSocialLoginSuccessHandler;


import javax.sql.DataSource;


@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class CustomSecurityConfig {

    private final DataSource dataSource;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("-----------configure-------------");

        http.authorizeHttpRequests(authorize -> {
            authorize.requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/user/**").hasRole("USER")
                    .requestMatchers("/**").permitAll();
        });

        http.formLogin(form -> {
            form.loginPage("/member/login");
            //form.defaultSuccessUrl("/recipe/list");
            form.successHandler(loginSuccessHandler());
        });

        http.oauth2Login( httpSecurityOAuth2LoginConfigurer -> {
            httpSecurityOAuth2LoginConfigurer.loginPage("/member/login");
            httpSecurityOAuth2LoginConfigurer.successHandler(authenticationSuccessHandler());
        });

        http.logout(customizer -> {
            customizer.logoutUrl("/member/login/logout");
            customizer.logoutSuccessUrl("/member/login");
            customizer.deleteCookies("JSESSIONID","remember-me");
        });

        http.csrf(AbstractHttpConfigurer::disable);

        http.rememberMe(httpSecurityRememberMeConfigurer -> {

            httpSecurityRememberMeConfigurer.key("12345678")
                    .tokenRepository(persistentTokenRepository())
                    .userDetailsService(userDetailsService)
                    .tokenValiditySeconds(60*60*24*30);

        });

        http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {

            httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler());

        });





        return http.build();

    }



    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new Custom403Handler();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){

        log.info("------------web configure--------------");

        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()); //정적자원 시큐리티해제

    }


    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler(){
        return new CustomLoginSuccessHandler(passwordEncoder());
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomSocialLoginSuccessHandler(passwordEncoder());
    }

}
