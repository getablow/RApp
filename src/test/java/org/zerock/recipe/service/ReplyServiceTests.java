package org.zerock.recipe.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.recipe.dto.ReplyDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Log4j2
public class ReplyServiceTests {

    @Autowired
    private ReplyService replyService;



    @Test
    public void testRegister() {

        ReplyDTO replyDTO = ReplyDTO.builder()
                .replyText("replyService Test...")
                .replier("user0")
                .bno(100L)
                .build();

        log.info(replyService.register(replyDTO));
    }


}
