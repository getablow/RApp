package org.zerock.recipe.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.zerock.recipe.domain.RecipeReply;
import org.zerock.recipe.dto.PageRequestDTO;
import org.zerock.recipe.dto.PageResponseDTO;

import org.zerock.recipe.dto.RecipeReplyDTO;
import org.zerock.recipe.repository.RecipeReplyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class RecipeReplyServiceImpl implements RecipeReplyService{

    private final RecipeReplyRepository recipeReplyRepository;

    private final ModelMapper modelMapper;

    @Override
    public Long register(RecipeReplyDTO recipeReplyDTO) {

        RecipeReply recipeReply = modelMapper.map(recipeReplyDTO, RecipeReply.class);

        Long rno = recipeReplyRepository.save(recipeReply).getRno();

        return rno;
    }

    @Override
    public RecipeReplyDTO read(Long rno) {

        Optional<RecipeReply> replyOptional = recipeReplyRepository.findById(rno);

        RecipeReply reply = replyOptional.orElseThrow();

        return modelMapper.map(reply, RecipeReplyDTO.class);
    }

    @Override
    public void modify(RecipeReplyDTO recipeReplyDTO){

        //recipeReplyRepository에서 rid를 이용하여 Reply 객체를 검색
        Optional<RecipeReply> replyOptional = recipeReplyRepository.findById(recipeReplyDTO.getRno());

        //Reply 객체가 존재하지 않을 경우 예외를 발생
        RecipeReply recipeReply = replyOptional.orElseThrow();

        //Reply 객체의 댓글 내용을 수정
        recipeReply.changeText(recipeReplyDTO.getReplyText()); //only comments can be modified

        //수정된 Reply 객체를 저장
        recipeReplyRepository.save(recipeReply);

        //댓글 내용을 수정하는 것 외에도, 댓글의 작성자, 작성일 등의 정보를 수정할 수 있도록 코드를 추가할 수 있다
    }

    @Override
    public void remove(Long rno){
        recipeReplyRepository.deleteById(rno);
    }


    @Override
    public PageResponseDTO<RecipeReplyDTO> getListOfRecipe(Long rid, PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() <= 0 ? 0 : pageRequestDTO.getPage() -1,
                pageRequestDTO.getSize(),
                Sort.by("rno").ascending());

        Page<RecipeReply> result = recipeReplyRepository.listOfRecipe(rid, pageable);

        List<RecipeReplyDTO> dtoList =
                result.getContent().stream().map(reply -> modelMapper.map(reply, RecipeReplyDTO.class))
                        .collect(Collectors.toList());

        return PageResponseDTO.<RecipeReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }

}
