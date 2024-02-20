package org.zerock.b01.dto;

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
