package org.zerock.recipe.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    private Recipe recipe;

    @Override
    public int compareTo(RecipeImage other){
        return this.ord - other.ord;
    }

    public void changeRecipe(Recipe recipe){
        this.recipe = recipe;
    }
}
