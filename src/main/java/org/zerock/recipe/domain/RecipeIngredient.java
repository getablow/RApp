package org.zerock.recipe.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "recipe")
public class RecipeIngredient{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 50)
    private String amount;

    @ManyToOne
    private Recipe recipe;

    public void changeRecipe(Recipe recipe){
        this.recipe = recipe;
    }


}
