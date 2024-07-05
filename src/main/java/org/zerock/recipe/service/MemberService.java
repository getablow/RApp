package org.zerock.recipe.service;

import org.zerock.recipe.domain.Member;
import org.zerock.recipe.domain.Refrigerator;
import org.zerock.recipe.dto.MemberJoinDTO;

public interface MemberService {

    static class MidExistException extends RuntimeException {
        public MidExistException(String message) {
            super(message);
        }
    }

    void join(MemberJoinDTO memberJoinDTO) throws MidExistException;


    /*Refrigerator createRefrigerator(Member member);
    String createRefrigeratorName(String memberId);*/



}
