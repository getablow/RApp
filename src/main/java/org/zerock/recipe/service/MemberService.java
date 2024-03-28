package org.zerock.recipe.service;

import org.zerock.recipe.dto.MemberJoinDTO;

public interface MemberService {

    static class MidExistException extends Exception {

    }

    void join(MemberJoinDTO memberJoinDTO) throws MidExistException;



}
