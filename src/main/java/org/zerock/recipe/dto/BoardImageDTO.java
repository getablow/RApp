package org.zerock.recipe.dto;

import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardImageDTO {

    private String uuid;
    private String fileName;
    private int ord;


}
