package org.zerock.recipe.service;

import org.zerock.recipe.domain.Recipe;
import org.zerock.recipe.domain.RecipeIngredient;
import org.zerock.recipe.dto.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface RecipeService {

    Long register(RecipeDTO recipeDTO);

    RecipeDTO readOne(Long rid);

    void modify(RecipeDTO recipeDTO);

    void remove(Long rid);

    PageResponseDTO<RecipeDTO> list(PageRequestDTO pageRequestDTO);

    //댓글 숫자까지 처리
    PageResponseDTO<RecipeListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO);

    //게시글의 이미지와 댓글의 숫자까지 처리
    PageResponseDTO<RecipeListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);

    //modelmapper 사용하지않고 메소드만들자
    default Recipe dtoToEntity(RecipeDTO recipeDTO){

        Recipe recipe = Recipe.builder()
                .rid(recipeDTO.getRid())
                .title(recipeDTO.getTitle())
                .content(recipeDTO.getContent())
                .videoUrl(recipeDTO.getVideoUrl())
                .writer(recipeDTO.getWriter())
                .reveal(recipeDTO.isReveal())
                .build();

        if(recipeDTO.getFileNames() != null){
            recipeDTO.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_");
                recipe.addImage(arr[0], arr[1]);
            });
        }

        if(recipeDTO.getIngredients() != null) {
            for (RecipeIngredientDTO ingredientDTO : recipeDTO.getIngredients()) {
                recipe.addIngredients(ingredientDTO.getName(), ingredientDTO.getAmount());
            }
        }

        return recipe;

    }

    default RecipeDTO entityToDTO(Recipe recipe){

        RecipeDTO recipeDTO = RecipeDTO.builder()
                .rid(recipe.getRid())
                .title(recipe.getTitle())
                .content(recipe.getContent())
                .writer(recipe.getWriter())
                .videoUrl(recipe.getVideoUrl())
                .reveal(recipe.isReveal())
                .regDate(recipe.getRegDate())
                .modDate(recipe.getModDate())
                .build();

        List<String> fileNames = recipe.getImageSet().stream().sorted()
                .map(recipeImage -> recipeImage.getUuid() + "_" + recipeImage.getFileName()).collect(Collectors.toList());

        recipeDTO.setFileNames(fileNames);



        List<RecipeIngredientDTO> ingredientDTOS = new ArrayList<>();
        for (RecipeIngredient material : recipe.getIngredientSet()) {
            RecipeIngredientDTO ingredientDTO = new RecipeIngredientDTO();
            ingredientDTO.setName(material.getName());
            ingredientDTO.setAmount(material.getAmount());
            ingredientDTOS.add(ingredientDTO);
        }
        recipeDTO.setIngredients(ingredientDTOS);



        return recipeDTO;
    }


}
