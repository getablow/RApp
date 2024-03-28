package org.zerock.recipe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.recipe.domain.Reply;
import org.zerock.recipe.dto.PageRequestDTO;
import org.zerock.recipe.dto.PageResponseDTO;
import org.zerock.recipe.dto.ReplyDTO;
import org.zerock.recipe.repository.ReplyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Log4j2
public class ReplyServiceImpl implements ReplyService{

    private final ReplyRepository replyRepository;

    private final ModelMapper modelMapper;

    @Override
    public Long register(ReplyDTO replyDTO) {

        Reply reply = modelMapper.map(replyDTO, Reply.class);

        Long rno = replyRepository.save(reply).getRno();

        return rno;
    }

    @Override
    public ReplyDTO read(Long rno) {

        Optional<Reply> replyOptional = replyRepository.findById(rno);

        Reply reply = replyOptional.orElseThrow();

        return modelMapper.map(reply, ReplyDTO.class);
    }

    @Override
    public void modify(ReplyDTO replyDTO){

        //ReplyRepository에서 Bno를 이용하여 Reply 객체를 검색
        Optional<Reply> replyOptional = replyRepository.findById(replyDTO.getRno());

        //Reply 객체가 존재하지 않을 경우 예외를 발생
        Reply reply = replyOptional.orElseThrow();

        //Reply 객체의 댓글 내용을 수정
        reply.changeText(replyDTO.getReplyText()); //only comments can be modified

        //수정된 Reply 객체를 저장
        replyRepository.save(reply);

        //댓글 내용을 수정하는 것 외에도, 댓글의 작성자, 작성일 등의 정보를 수정할 수 있도록 코드를 추가할 수 있다
    }

    @Override
    public void remove(Long rno){
        replyRepository.deleteById(rno);
    }


    @Override
    public PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() <= 0 ? 0 : pageRequestDTO.getPage() -1,
                pageRequestDTO.getSize(),
                Sort.by("rno").ascending());

        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);

        List<ReplyDTO> dtoList =
                result.getContent().stream().map(reply -> modelMapper.map(reply, ReplyDTO.class))
                        .collect(Collectors.toList());

        return PageResponseDTO.<ReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }

}
