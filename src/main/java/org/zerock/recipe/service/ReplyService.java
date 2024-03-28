package org.zerock.recipe.service;

import org.zerock.recipe.dto.PageRequestDTO;
import org.zerock.recipe.dto.PageResponseDTO;
import org.zerock.recipe.dto.ReplyDTO;

public interface ReplyService {

    Long register(ReplyDTO replyDTO);

    ReplyDTO read(Long rno);

    void modify(ReplyDTO replyDTO);

    void remove(Long rno);

    //processing paging comments
    PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO);
}
