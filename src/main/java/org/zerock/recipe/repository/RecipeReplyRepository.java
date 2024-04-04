package org.zerock.recipe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.recipe.domain.RecipeReply;

public interface RecipeReplyRepository extends JpaRepository<RecipeReply, Long> {

    @Query("select r from RecipeReply r where r.recipe.rid = :rid")
    Page<RecipeReply> listOfRecipe(@Param("rid")Long rid, Pageable pageable); //Param 안붙이니깐 오류남 jdk 버전문제...?

    void deleteByRecipe_Rid(Long Rid);
}
