package org.zerock.recipe.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "RecipeReply", indexes = {@Index(name="idx_reply_recipe_rid", columnList = "recipe_rid")})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "recipe") //exclude제거시 recipe 객체실행해버림
public class RecipeReply extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @ManyToOne(fetch = FetchType.LAZY) //다대일관계 lazy<->eager(즉시로딩)
    private Recipe recipe;

    private String replyText;

    private String replier;

    public void changeText(String text){
        this.replyText = text;
    }

}
