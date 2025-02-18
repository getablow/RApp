package org.zerock.recipe.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "recipe")
public class RecipeIngredient extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 50)
    private String amount;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_rid")
    private Recipe recipe;

    public void changeRecipe(Recipe recipe){
        this.recipe = recipe;
    }


}
