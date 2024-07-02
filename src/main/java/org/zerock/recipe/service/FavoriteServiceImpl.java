package org.zerock.recipe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.recipe.domain.Favorite;
import org.zerock.recipe.domain.Member;
import org.zerock.recipe.domain.Recipe;
import org.zerock.recipe.repository.FavoriteRepository;
import org.zerock.recipe.repository.MemberRepository;
import org.zerock.recipe.repository.RecipeRepository;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private final RecipeRepository recipeRepository;

    @Override
    @Transactional
    public int favoriteRecipe(String memberId, Long recipeId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        if (favoriteRepository.existsByMemberAndRecipe(member, recipe)) {
            throw new RuntimeException("You have already liked this recipe");
        }

        Favorite favorite = Favorite.builder()
                .member(member)
                .recipe(recipe)
                .build();

        log.info("Liking recipe: {} by member: {}", recipeId, memberId);

        favoriteRepository.save(favorite);
        recipe.addFavorite();
        recipeRepository.save(recipe);

        return favoriteRepository.countByRecipe(recipe);

    }


    @Override
    public int getFavoriteCount(Long rid) {
        Recipe recipe = recipeRepository.findById(rid)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        return favoriteRepository.countByRecipe(recipe);
    }

}
