package org.zerock.recipe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.recipe.domain.Member;
import org.zerock.recipe.domain.MemberRole;
import org.zerock.recipe.domain.Refrigerator;
import org.zerock.recipe.dto.MemberJoinDTO;
import org.zerock.recipe.repository.MemberRepository;
import org.zerock.recipe.repository.RefrigeratorRepository;

import static org.zerock.recipe.domain.QMember.member;


@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefrigeratorRepository refrigeratorRepository;

    @Transactional
    @Override
    public void join(MemberJoinDTO memberJoinDTO) throws MidExistException{

        String mid = memberJoinDTO.getMid();

        if (memberRepository.existsById(mid)) {
            throw new MidExistException("Member ID already exists: " + mid);
        }

        boolean exist = memberRepository.existsById(mid);

        try {

            Member member = modelMapper.map(memberJoinDTO, Member.class);
            member.changePassword(passwordEncoder.encode(memberJoinDTO.getMpw()));
            member.addRole(MemberRole.USER);

            log.info("Creating new member: {}", member.getMid());
            memberRepository.save(member);

            Refrigerator refrigerator = createRefrigerator(member);
            refrigeratorRepository.save(refrigerator);

            member.setRefrigerator(refrigerator);
            memberRepository.save(member);

            log.info("Member joined successfully: {}", member.getMid());
            log.info("Refrigerator created for member: {}", member.getMid());

        } catch (Exception e) {
            log.error("Error during member registration: ", e);
            throw new RuntimeException("Failed to register member", e);
        }
    }

    private Refrigerator createRefrigerator(Member member) {
        return Refrigerator.builder()
                .member(member)
                .name(createRefrigeratorName(member.getMid()))
                .build();
    }

    private String createRefrigeratorName(String memberId) {
        return memberId + "'s Refrigerator";
    }


}
