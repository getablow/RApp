package org.zerock.recipe.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT r FROM Recipe r " +
            "JOIN RecipeIngredient ri ON r.rid = ri.recipe.rid " +
            "WHERE ri.name IN :ingredientNames " +
            "GROUP BY r.rid " +
            "HAVING COUNT(ri) > 0 " +
            "ORDER BY COUNT(ri) DESC")
    List<Recipe> findRecipesByIngredients(@Param("ingredientNames") List<String> ingredientNames);

    // 로그인한 사용자의 레시피를 필터링하는 메서드 추가
    @Query("SELECT r FROM Recipe r " +
            "JOIN RecipeIngredient ri ON r.rid = ri.recipe.rid " +
            "WHERE ri.name IN :ingredientNames AND r.writer = :writer " +
            "GROUP BY r.rid " +
            "HAVING COUNT(ri) > 0 " +
            "ORDER BY COUNT(ri) DESC")
    List<Recipe> findRecipesByIngredientsAndWriter(@Param("ingredientNames") List<String> ingredientNames, @Param("writer") String writer);

}
