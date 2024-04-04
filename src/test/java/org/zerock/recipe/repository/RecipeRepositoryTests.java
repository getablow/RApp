package org.zerock.recipe.repository;


import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.recipe.domain.Recipe;
import org.zerock.recipe.domain.RecipeImage;
import org.zerock.recipe.domain.RecipeIngredient;
import org.zerock.recipe.dto.PageResponseDTO;
import org.zerock.recipe.dto.RecipeListAllDTO;
import org.zerock.recipe.dto.RecipeListReplyCountDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class RecipeRepositoryTests {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeReplyRepository recipeReplyRepository;

    @Test
    public void testInsert() {
        IntStream.rangeClosed(1,50).forEach(i -> {
            Recipe recipe = Recipe.builder()
                    .title("title..." +i)
                    .content("content..." + i)
                    .writer("user"+ (i % 10))
                    .videoUrl("url...")
                    .build();

            Recipe result = recipeRepository.save(recipe);
            log.info("RID: " + result.getRid());
        });
    }

    @Test
    public void testSelect() {
        Long rid = 10L;

        Optional<Recipe> result = recipeRepository.findById(rid);

        Recipe recipe = result.orElseThrow();

        log.info(recipe);

    }

    @Test
    public void testUpdate() {

        Long rid = 2L;

        Optional<Recipe> result = recipeRepository.findById(rid);

        Recipe recipe = result.orElseThrow();

        //recipe.change("update..title", "update content", "update url");

        recipeRepository.save(recipe);

    }

    @Test
    public void testDelete() {
        Long rid = 52L;

        recipeRepository.deleteById(rid);
    }

    @Test
    public void testPaging() {

        //1 page order by bno desc
        Pageable pageable = PageRequest.of(0,10, Sort.by("rid").descending());

        Page<Recipe> result = recipeRepository.findAll(pageable);

        log.info("total count: "+result.getTotalElements());
        log.info( "total pages:" +result.getTotalPages());
        log.info("page number: "+result.getNumber());
        log.info("page size: "+result.getSize());

        List<Recipe> todoList = result.getContent();

        todoList.forEach(recipe -> log.info(recipe));


    }

    @Test
    public void testSearch1() {

        //2 page order by bno desc
        Pageable pageable = PageRequest.of(1,10, Sort.by("rid").descending());

        recipeRepository.search1(pageable);

    }

    @Test
    public void testSearchAll() {

        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Recipe> result = recipeRepository.searchAll(types, keyword, pageable);

    }

    @Test
    public void testSearchAll2() {

        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10, Sort.by("rid").descending());

        Page<Recipe> result = recipeRepository.searchAll(types, keyword, pageable);

        //total pages
        log.info(result.getTotalPages());

        //pag size
        log.info(result.getSize());

        //pageNumber
        log.info(result.getNumber());

        //prev next
        log.info(result.hasPrevious() +": " + result.hasNext());

        result.getContent().forEach(recipe -> log.info(recipe));
    }

    @Test
    public void testSearchReplyCount() {

        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0, 10, Sort.by("rid").descending());

        Page<RecipeListReplyCountDTO> result = recipeRepository.searchWithReplyCount(types, keyword, pageable);

        //total pages
        log.info(result.getTotalPages());
        //page size
        log.info(result.getSize());
        //pageNumber
        log.info(result.getNumber());
        //prev next
        log.info(result.hasPrevious() + ": " + result.hasNext());

        result.getContent().forEach(recipe -> log.info(recipe));
    }

    @Test
    public void testInsertWithImages(){

        Recipe recipe = Recipe.builder()
                .title("Image Test")
                .content("첨부파일테스트")
                .writer("tester")
                .build();

        for(int i = 0; i < 3; i ++){ //insert 3times
            recipe.addImage(UUID.randomUUID().toString(), "testfile"+i+".jpg");
        }

        recipeRepository.save(recipe);
    }


    @Test
    public void testInsertWithIngredients(){

        Recipe recipe = Recipe.builder()
                .title("재료추가테스트")
                .content("재료추가되엇니")
                .videoUrl("www.naver.com")
                .writer("tester")
                .build();

        recipe.addImage(UUID.randomUUID().toString(), "onionfile"+10+".jpg");
        recipe.addIngredients("양파", "1 개");

        recipeRepository.save(recipe);

    }

    @Test
    //@Transactional
    public void testReadWithImages(){

        Optional<Recipe> result = recipeRepository.findByIdWithOthers(57L);

        Recipe recipe = result.orElseThrow();

        log.info(recipe);
        log.info("--------------------");
        for(RecipeImage recipeImage : recipe.getImageSet()){
            log.info(recipeImage);
        }
        for(RecipeIngredient ingredient: recipe.getIngredientSet()){
            log.info(ingredient);
        }
    }

    @Transactional
    @Commit
    @Test
    public void testModifyImages(){

        Optional<Recipe> result = recipeRepository.findByIdWithOthers(2L);

        Recipe recipe = result.orElseThrow();

        recipe.clearImages();

        for(int i = 0; i < 2; i++){
            recipe.addImage(UUID.randomUUID().toString(), "updatefile"+i+".jpg");
        }

        recipeRepository.save(recipe);
    }

    @Test
    @Transactional
    @Commit
    public void testRemoveAll(){
        Long rid = 1L;

        recipeReplyRepository.deleteByRecipe_Rid(rid);

        recipeRepository.deleteById(rid);

    }

    @Test
    public void testInsertAll(){
        for(int i=1; i <= 100; i++){
            Recipe recipe = Recipe.builder()
                    .title("Title.."+i)
                    .content("Content.."+i)
                    .writer("writer.."+i)
                    .build();

            for(int j = 0;j <3; j++){
                if(i % 5 == 0){ continue; }
                recipe.addImage(UUID.randomUUID().toString(), i+"file"+j+".jpg");
            }
            recipeRepository.save(recipe);
        }
    }


    @Transactional
    @Test
    public void testSearchImageReplyCount(){//most used DB -> board.java에서 batchsize 사용하면됨

        Pageable pageable = PageRequest.of(0, 10, Sort.by("rid").descending());

        //boardRepository.searchWithAll(null, null, pageable);

        Page<RecipeListAllDTO> result = recipeRepository.searchWithAll(null, null, pageable);

        log.info("---------------------------");
        log.info(result.getTotalElements());

        result.getContent().forEach(recipeListAllDTO -> log.info(recipeListAllDTO));
    }




}
