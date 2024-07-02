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

import java.util.HashMap;
import java.util.Map;

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
    public Map<String, Object> favoriteRecipe(String mid, Long rid) {
        Member member = memberRepository.findById(mid)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Recipe recipe = recipeRepository.findById(rid)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));


        boolean exists = favoriteRepository.existsByMemberAndRecipe(member, recipe);
        boolean favoriteStatus = false;

        if (exists) {
            //이미 좋아요 있으면 삭제
            favoriteRepository.deleteByMemberAndRecipe(member, recipe);
            recipe.removeFavorite();
            log.info("Unliking recipe: {} by user: {}", rid, mid);
        } else {
            //좋아요 없으면 추가
            Favorite favorite = Favorite.builder()
                    .member(member)
                    .recipe(recipe)
                    .build();

            favoriteRepository.save(favorite);
            recipe.addFavorite();
            favoriteStatus = true;
            log.info("Liking recipe: {} by user: {}", rid, mid);
        }

        recipeRepository.save(recipe);

        int favoriteCount = favoriteRepository.countByRecipe(recipe);
        Map<String, Object> response = new HashMap<>();
        response.put("favoriteCount", favoriteCount);
        response.put("favoriteStatus", favoriteStatus);

        return response;

    }

    @Override
    public boolean getFavoriteConfirm(String mid, Long rid) {
        Member member = memberRepository.findById(mid)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Recipe recipe = recipeRepository.findById(rid)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        return favoriteRepository.existsByMemberAndRecipe(member, recipe);
    }


    @Override
    public int getFavoriteCount(Long rid) {
        Recipe recipe = recipeRepository.findById(rid)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        return favoriteRepository.countByRecipe(recipe);
    }

}
