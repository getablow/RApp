package org.zerock.recipe.repository;


import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.zerock.recipe.domain.Recipe;
import org.zerock.recipe.domain.RecipeReply;



@SpringBootTest
@Log4j2
public class ReplyRepositoryTests {


    @Autowired
    private RecipeReplyRepository recipeReplyRepository;

    @Test
    public void testInsert(){

        Long rid = 1L;

        Recipe recipe = Recipe.builder().rid(rid).build();

        RecipeReply recipeReply = RecipeReply.builder()
                .recipe(recipe)
                .replyText("recipeReplyRepository insert test......")
                .replier("member1")
                .build();

        recipeReplyRepository.save(recipeReply);
    }

    //@Transactional
    @Test
    public void testBoardReplies(){

        Long rid = 1L;

        Pageable pageable = PageRequest.of(0, 10, Sort.by("rno").descending());

        Page<RecipeReply> result = recipeReplyRepository.listOfRecipe(rid, pageable);

        result.getContent().forEach(reply -> { log.info(reply); } );
    }


}
