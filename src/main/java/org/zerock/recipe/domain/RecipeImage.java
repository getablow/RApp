package org.zerock.recipe.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "recipe")
public class RecipeImage implements Comparable<RecipeImage>{

    @Id
    private String uuid;

    private String fileName;

    private int ord;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_rid")
    private Recipe recipe;

    @Override
    public int compareTo(RecipeImage other){
        return this.ord - other.ord;
    }

    public void changeRecipe(Recipe recipe){
        this.recipe = recipe;
    }
}
