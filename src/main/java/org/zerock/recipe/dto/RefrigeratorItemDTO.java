package org.zerock.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefrigeratorItemDTO {

    private Long id;
    private String itemName;
    private String quantity;
    private LocalDate expirationDate;
    private LocalDate addedDate;
    private String memberId;
}
