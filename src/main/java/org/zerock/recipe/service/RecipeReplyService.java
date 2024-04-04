package org.zerock.recipe.service;

import org.zerock.recipe.dto.PageRequestDTO;
import org.zerock.recipe.dto.PageResponseDTO;
import org.zerock.recipe.dto.RecipeReplyDTO;


public interface RecipeReplyService {

    Long register(RecipeReplyDTO recipeReplyDTO);

    RecipeReplyDTO read(Long rno);

    void modify(RecipeReplyDTO recipeReplyDTO);

    void remove(Long rno);

    //processing paging comments
    PageResponseDTO<RecipeReplyDTO> getListOfRecipe(Long rid, PageRequestDTO pageRequestDTO);

}
