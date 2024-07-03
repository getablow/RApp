package org.zerock.recipe.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.recipe.domain.Recipe;
import org.zerock.recipe.dto.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Log4j2
public class RecipeServiceTests {

    @Autowired
    private RecipeService recipeService;

    @Test
    public void testRegister() {

        log.info(recipeService.getClass().getName());

        RecipeDTO recipeDTO = RecipeDTO.builder()
                .title("Sample Title...")
                .content("Sample Content...")
                .writer("member1")
                .reveal(true)
                .build();

        /*recipeDTO.setIngredients(
                Arrays.asList(
                        RecipeIngredientDTO.builder().name("돔마도").amount("1개").build(),
                        RecipeIngredientDTO.builder().name("사과").amount("너무비쌈").build()
                        )
        );*/

        Long rid = recipeService.register(recipeDTO);

        log.info("rid: " + rid);
    }

    @Test
    public void testModify() {

        //변경에 필요한 데이터만
        RecipeDTO recipeDTO = RecipeDTO.builder()
                .rid(9L)
                .title("Updated....9")
                .content("Updated content 9...")
                .reveal(true)
                .build();

        /*recipeDTO.setFileNames(Arrays.asList(UUID.randomUUID()+"_uuu2.jpg"));
        recipeDTO.setIngredients(Arrays.asList(new RecipeIngredientDTO(null, "update2","1개"),
                new RecipeIngredientDTO(null, "update3","11개")
                ));*/

        recipeService.modify(recipeDTO);

    }
/*
    @Test
    public void testList() {

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("tcw")
                .keyword("1")
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

        log.info(responseDTO);

    }
*/
    @Test
    public void testRegisterWithImages(){

        log.info(recipeService.getClass().getName());

        RecipeDTO recipeDTO = RecipeDTO.builder()
                .title("Testing at board service tests...")
                .content("Sample Content...")
                .writer("member1")
                .videoUrl("abc")
                .build();

        recipeDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID() + "_testaaa.jpg",
                        UUID.randomUUID() + "_testbbb.jpg",
                        UUID.randomUUID() + "_testbbb.jpg"
                )
        );

        recipeDTO.setIngredients(
                Arrays.asList(
                        new RecipeIngredientDTO(null, "콜라비", "4개")
                )
        );


        Long rid = recipeService.register(recipeDTO);

        log.info("rid: " + rid);
    }

    @Test
    public void testReadAll(){

        Long rid = 57L;
        String username = "membertest";

        RecipeDTO recipeDTO = recipeService.readOne(username,rid);

        log.info(recipeDTO);

        for(String fileName : recipeDTO.getFileNames()){
            log.info(fileName);
        }




    }


    @Test
    public void testListWithAll(){

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();

        //
        // PageResponseDTO<RecipeListAllDTO> responseDTO = recipeService.listWithAll(pageRequestDTO);
        PageResponseDTO<RecipeListAllDTO> responseDTO = recipeService.listWithReveal(pageRequestDTO, true);


        List<RecipeListAllDTO> dtoList = responseDTO.getDtoList();

        dtoList.forEach(recipeListAllDTO -> {
            log.info(recipeListAllDTO.getRid() + ":" + recipeListAllDTO.getTitle());

            if(recipeListAllDTO.getRecipeImages() != null){
                for(RecipeImageDTO recipeImage : recipeListAllDTO.getRecipeImages()){
                    log.info(recipeImage);
                }
            }

            if(recipeListAllDTO.getRecipeIngredients() != null){
                for(RecipeIngredientDTO ingredient : recipeListAllDTO.getRecipeIngredients()){
                    log.info(ingredient);
                }
            }


            log.info("----------------------------------");
        });
    }



}
