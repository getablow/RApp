package org.zerock.recipe.mapper;

import jakarta.persistence.EntityNotFoundException;
import org.zerock.recipe.domain.Member;
import org.zerock.recipe.domain.Recipe;
import org.zerock.recipe.dto.MemberDTO;
import org.zerock.recipe.dto.RecipeDTO;
import org.zerock.recipe.dto.RecipeIngredientDTO;
import org.zerock.recipe.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;



public class RecipeMapper {
    public static Recipe dtoToEntity(RecipeDTO recipeDTO){

        Recipe recipe = Recipe.builder()
                .rid(recipeDTO.getRid())
                .title(recipeDTO.getTitle())
                .content(recipeDTO.getContent())
                .videoUrl(recipeDTO.getVideoUrl())
                .writer(recipeDTO.getWriter())
                .reveal(recipeDTO.isReveal())
                .build();

        if (recipeDTO.getMemberId() != null) {
            Member member = Member.builder()
                    .mid(recipeDTO.getMemberId())
                    .build();
            recipe.setMember(member);
        }

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

    public static RecipeDTO entityToDTO(Recipe recipe){

        RecipeDTO recipeDTO = RecipeDTO.builder()
                .rid(recipe.getRid())
                .title(recipe.getTitle())
                .content(recipe.getContent())
                .writer(recipe.getWriter())
                .videoUrl(recipe.getVideoUrl())
                .reveal(recipe.isReveal())
                .regDate(recipe.getRegDate())
                .modDate(recipe.getModDate())
                .viewCount(recipe.getViewCount())
                .favoriteCount(recipe.getFavoriteCount())
                .favoriteConfirm(false)
                .memberId(recipe.getMember() != null ? recipe.getMember().getMid() : null)
                .build();


        List<String> fileNames = recipe.getImageSet().stream()
                .sorted()
                .map(recipeImage -> recipeImage.getUuid() + "_" + recipeImage.getFileName())
                .collect(Collectors.toList());

        recipeDTO.setFileNames(fileNames);



        List<RecipeIngredientDTO> ingredientDTOS = recipe.getIngredientSet().stream()
                .map(material -> RecipeIngredientDTO.builder()
                        .name(material.getName())
                        .amount(material.getAmount())
                        .build())
                .collect(Collectors.toList());

        /*List<RecipeIngredientDTO> ingredientDTOS = new ArrayList<>();
        for (RecipeIngredient material : recipe.getIngredientSet()) {
            RecipeIngredientDTO ingredientDTO = new RecipeIngredientDTO();
            ingredientDTO.setName(material.getName());
            ingredientDTO.setAmount(material.getAmount());
            ingredientDTOS.add(ingredientDTO);
        }*/


        recipeDTO.setIngredients(ingredientDTOS);

        return recipeDTO;
    }
}
