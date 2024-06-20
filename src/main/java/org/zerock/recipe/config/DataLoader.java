package org.zerock.recipe.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zerock.recipe.domain.Member;
import org.zerock.recipe.domain.MemberRole;
import org.zerock.recipe.repository.MemberRepository;

import java.util.Map;
import java.util.Optional;


@ConditionalOnProperty(name = "myapp.dataloader.enabled", havingValue = "false", matchIfMissing = false)
@Configuration
@RequiredArgsConstructor
@Log4j2
public class DataLoader implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_USERNAME}")
    private String adminName;

    @Value("${ADMIN_PASSWORD}")
    private String adminPw;


    @Override
    public void run(String... args) throws Exception {

        if (adminName == null || adminPw == null) {
            log.error("ADMIN_USER or ADMIN_PASSWORD environment variables are not set");
            throw new IllegalArgumentException("Required environment variables are not set");
        }

        log.info("DataLoader run method started");

        Optional<Member> existingAdmin = memberRepository.findByMid(adminName);

        if (existingAdmin.isPresent()){
            log.info("Admin account already exists, skipping creation");
        } else {
            Member admin = Member.builder()
                    .mid(adminName)
                    .mpw(passwordEncoder.encode(adminPw))
                    .email("admin@example.com")
                    .del(false)
                    .social(false)
                    .build();

            admin.addRole(MemberRole.ADMIN);
            memberRepository.save(admin);

            log.info("Admin account created");
        }



    }
}
