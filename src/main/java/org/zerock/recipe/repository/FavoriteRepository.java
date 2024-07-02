package org.zerock.recipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.recipe.domain.Favorite;
import org.zerock.recipe.domain.Member;
import org.zerock.recipe.domain.Recipe;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByMemberAndRecipe(Member member, Recipe recipe);

    void deleteByMemberAndRecipe(Member member, Recipe recipe);

    int countByRecipe(Recipe recipe);
}
