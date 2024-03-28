package org.zerock.recipe.dto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardListAllDTO {

    private Long bno;

    private String title;

    private String writer;

    private LocalDateTime regDate;

    private Long replyCount;

    private List<BoardImageDTO> boardImages;
}
