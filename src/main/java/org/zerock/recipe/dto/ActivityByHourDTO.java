package org.zerock.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityByHourDTO {
    private int hour;
    private long viewCount;
    private long favoriteCount;
}
