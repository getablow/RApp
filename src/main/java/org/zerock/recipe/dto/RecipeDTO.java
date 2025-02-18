package org.zerock.recipe.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zerock.recipe.domain.Member;
import org.zerock.recipe.domain.RecipeIngredient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDTO {

    private Long rid;

    private boolean reveal;

    @NotEmpty
    @Size(min = 3, max = 100, message = "Title must be at least 3 characters")
    private String title;

    @NotEmpty
    private String content;

    @NotEmpty
    private String writer;

    private String videoUrl;
    private String videoId;

    private int viewCount;

    private String memberId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate regDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate modDate;

    //files name
    private List<String> fileNames;
    //Recipe에서 Set<BoardImages>타입으로 변환되어야만합니다

    private List<RecipeIngredientDTO> ingredients;

    private int favoriteCount;
    private boolean favoriteConfirm;




}
