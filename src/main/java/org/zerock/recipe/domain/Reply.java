package org.zerock.recipe.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Reply", indexes = {@Index(name="idx_reply_board_bno", columnList = "board_bno")})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString (exclude = "board") //exclude제거시 board 객체실행해버림
public class Reply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @ManyToOne(fetch = FetchType.LAZY) //다대일관계 lazy<->eager(즉시로딩)
    private Board board;

    private String replyText;

    private String replier;

    public void changeText(String text){
        this.replyText = text;
    }
}
