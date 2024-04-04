package org.zerock.recipe.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.recipe.dto.PageRequestDTO;
import org.zerock.recipe.dto.PageResponseDTO;
import org.zerock.recipe.dto.RecipeReplyDTO;
import org.zerock.recipe.service.RecipeReplyService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/recipeReplies")
@Log4j2
@RequiredArgsConstructor //for dependent infusion
public class RecipeReplyController {

    private final RecipeReplyService recipeReplyService;

    @Operation(summary = "Replies POST", description = "POST 방식으로 댓글 등록")
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Long> register(@Valid @RequestBody RecipeReplyDTO recipeReplyDTO, BindingResult bindingResult) throws BindException {

        log.info(recipeReplyDTO);

        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }

        Map<String, Long> resultMap = new HashMap<>();

        Long rno = recipeReplyService.register(recipeReplyDTO);

        resultMap.put("rno",rno);

        return resultMap;
    }

    @Operation(summary = "Replies of Recipe", description = "GET 방식으로 특정 게시물의 댓글 목록")
    @GetMapping(value = "/list/{rid}")
    public PageResponseDTO<RecipeReplyDTO> getList(@PathVariable("rid") Long rid, PageRequestDTO pageRequestDTO){

        PageResponseDTO<RecipeReplyDTO> responseDTO = recipeReplyService.getListOfRecipe(rid, pageRequestDTO);

        return responseDTO;
    }

    @Operation(summary = "Read Reply", description = "GET 방식으로 특정 댓글 조회")
    @GetMapping(value="/{rno}")
    public RecipeReplyDTO getReplyDTO( @PathVariable("rno") Long rno ){

        RecipeReplyDTO recipeReplyDTO = recipeReplyService.read(rno);

        return recipeReplyDTO;
    }

    @Operation(summary = "Delete Reply", description = "DELETE 방식으로 특정 댓글 삭제")
    @DeleteMapping("/{rno}")
    public Map<String, Long> remove( @PathVariable("rno") Long rno ){

        recipeReplyService.remove(rno);

        Map<String, Long> resultMap = new HashMap<>();

        resultMap.put("rno", rno);

        return resultMap;
    }

    @Operation(summary = "Modify Reply", description = "PUT 방식으로 특정 댓글 수정")
    @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Long> modify( @PathVariable("rno") Long rno, @RequestBody RecipeReplyDTO recipeReplyDTO){

        recipeReplyDTO.setRno(rno);

        recipeReplyService.modify(recipeReplyDTO);

        Map<String, Long> resultMap = new HashMap<>();

        resultMap.put("rno", rno);

        return resultMap;
    }
}
