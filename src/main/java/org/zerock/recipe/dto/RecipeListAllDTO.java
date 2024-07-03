package org.zerock.recipe.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.recipe.domain.RecipeIngredient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeListAllDTO {

    private Long rid;

    private String title;

    private String writer;

    private LocalDate regDate;

    private Long replyCount;

    private List<RecipeImageDTO> recipeImages;

    private List<RecipeIngredientDTO> recipeIngredients;

    private boolean reveal;

    private int favoriteCount;
    private int viewCount;


}
