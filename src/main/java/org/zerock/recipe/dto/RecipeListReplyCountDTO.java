package org.zerock.recipe.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class RecipeListReplyCountDTO {

    private Long rid;

    private String title;

    private String writer;

    private LocalDate regDate;

    private Long replyCount;

}
