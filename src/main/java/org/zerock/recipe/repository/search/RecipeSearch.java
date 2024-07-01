package org.zerock.recipe.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.recipe.domain.Recipe;
import org.zerock.recipe.dto.RecipeListAllDTO;
import org.zerock.recipe.dto.RecipeListReplyCountDTO;

public interface RecipeSearch {

    Page<Recipe> search1(Pageable pageable);

    Page<Recipe> searchAll(String[] types, String keyword, Pageable pageable);

    Page<RecipeListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable);

    Page<RecipeListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable);

    Page<RecipeListAllDTO> searchWithReveal(String[] types, String keyword, Boolean reveal, Pageable pageable);

    Page<RecipeListAllDTO> searchWithAllByWriter(String[] types, String keyword, String writer, Pageable pageable);

}
