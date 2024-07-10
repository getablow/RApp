package org.zerock.recipe.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.zerock.recipe.domain.Member;
import org.zerock.recipe.domain.Recipe;
import org.zerock.recipe.domain.RecipeIngredient;
import org.zerock.recipe.domain.RefrigeratorItem;
import org.zerock.recipe.dto.*;
import org.zerock.recipe.repository.RecipeRepository;
import org.zerock.recipe.repository.RefrigeratorItemRepository;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;


@Log4j2
@ExtendWith(MockitoExtension.class)
public class RecipeServiceTests {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RefrigeratorItemRepository refrigeratorItemRepository;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    @Test
    void testRecommendRecipes() {
        // Given
        String memberId = "testMember";

        List<Object[]> refrigeratorItemsWithMember = Arrays.asList(
                new Object[] {
                        RefrigeratorItem.builder().itemName("Tomato").build(),
                        Member.builder().mid("testMember").build()
                },
                new Object[] {
                        RefrigeratorItem.builder().itemName("Cheese").build(),
                        Member.builder().mid("testMember").build()
                }
        );

        Recipe recipe1 = Recipe.builder()
                .rid(1L)
                .title("Tomato Cheese Salad")
                .content("Mix tomato and cheese")
                .writer("chef1")
                .reveal(true)
                .build();
        recipe1.addIngredients("Tomato", "1개");
        recipe1.addIngredients("Cheese", "50g");

        Recipe recipe2 = Recipe.builder()
                .rid(2L)
                .title("Cheese Pizza")
                .content("Make pizza with cheese")
                .writer("chef2")
                .reveal(true)
                .build();
        recipe2.addIngredients("Cheese", "100g");
        recipe2.addIngredients("Flour", "200g");

        List<Recipe> expectedRecipes = Arrays.asList(recipe1, recipe2);


        when(refrigeratorItemRepository.findRefrigeratorItemsWithMember(memberId)).thenReturn(refrigeratorItemsWithMember);
        when(recipeRepository.findRecipesByIngredients(any())).thenReturn(expectedRecipes);

        // When
        //List<RecipeDTO> result = recipeService.findRecipesByIngredients(memberId);

        // Then
        //assertEquals(2, result.size());

        //RecipeDTO resultDTO1 = recipeService.entityToDTO(result.get(0));
        //RecipeDTO resultDTO2 = recipeService.entityToDTO(result.get(1));

        /*assertEquals("Tomato Cheese Salad", resultDTO1.getTitle());
        assertEquals("Cheese Pizza", resultDTO2.getTitle());

        assertEquals(2, resultDTO1.getIngredients().size());
        assertTrue(resultDTO1.getIngredients().stream().anyMatch(i -> i.getName().equals("Tomato") && i.getAmount().equals("1개")));
        assertTrue(resultDTO1.getIngredients().stream().anyMatch(i -> i.getName().equals("Cheese") && i.getAmount().equals("50g")));

        assertEquals(2, resultDTO2.getIngredients().size());
        assertTrue(resultDTO2.getIngredients().stream().anyMatch(i -> i.getName().equals("Cheese") && i.getAmount().equals("100g")));
        assertTrue(resultDTO2.getIngredients().stream().anyMatch(i -> i.getName().equals("Flour") && i.getAmount().equals("200g")));*/

        verify(refrigeratorItemRepository).findRefrigeratorItemsWithMember(memberId);
        verify(recipeRepository).findRecipesByIngredients(Arrays.asList("Tomato", "Cheese"));
    }





    @Test
    public void testRegister() {

        log.info(recipeService.getClass().getName());

        RecipeDTO recipeDTO = RecipeDTO.builder()
                .title("Sample Title...")
                .content("Sample Content...")
                .writer("member1")
                .reveal(true)
                .build();

        recipeDTO.setIngredients(
                Arrays.asList(
                        RecipeIngredientDTO.builder().name("돔마도").amount("1개").build(),
                        RecipeIngredientDTO.builder().name("사과").amount("너무비쌈").build()
                        )
        );

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
