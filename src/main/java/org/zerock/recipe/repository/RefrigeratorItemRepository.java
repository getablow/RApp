package org.zerock.recipe.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.recipe.domain.Recipe;
import org.zerock.recipe.domain.RefrigeratorItem;
import org.zerock.recipe.repository.search.RecipeSearch;
import org.zerock.recipe.repository.search.RefrigeratorItemSearch;

import java.util.List;
import java.util.Optional;

public interface RefrigeratorItemRepository extends JpaRepository<RefrigeratorItem, Long>, RefrigeratorItemSearch {

    @EntityGraph(attributePaths = {"refrigerator", "refrigerator.member"})
    @Query("SELECT b FROM RefrigeratorItem b WHERE b.id = :id")
    Optional<RefrigeratorItem> findByIdWithOthers(Long id);
}
