package org.zerock.recipe.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.recipe.domain.Recipe;
import org.zerock.recipe.repository.search.RecipeSearch;


import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeSearch {

    @EntityGraph(attributePaths = {"imageSet","ingredientSet"}) //loading together
    @Query("SELECT b FROM Recipe b WHERE b.rid = :rid")
    Optional<Recipe> findByIdWithOthers(Long rid);


    @Query("SELECT r FROM Recipe r ORDER BY r.favoriteCount DESC")
    List<Recipe> findTopByOrderByFavoriteCountDesc(Pageable pageable);

    @Query("SELECT r FROM Recipe r ORDER BY r.viewCount DESC")
    List<Recipe> findTopByOrderByViewCount(Pageable pageable);
}
