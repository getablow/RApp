package org.zerock.recipe.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.zerock.recipe.controller.RecipeController;
import org.zerock.recipe.domain.Recipe;

import java.util.Arrays;
import java.util.List;


import static org.mockito.Mockito.when;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc
@EnableJpaRepositories(basePackages = "org.zerock.recipe.repository")
public class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private RefrigeratorItemService refrigeratorItemService;

    /*@Test
    @WithMockUser
    void testRecommendRecipes() throws Exception {

        String memberId = "testUser";
        List<Recipe> recipes = Arrays.asList(
                Recipe.builder().title("Recipe 1").build(),
                Recipe.builder().title("Recipe 2").build()
        );
        when(recipeService.findRecipesByIngredients(memberId)).thenReturn(recipes);

        mockMvc.perform(get("/api/recipes/recommend")
                        .with(user(memberId)))
                .andExpect(jsonPath("$[0].title").value("Recipe 1"))
                .andExpect(jsonPath("$[1].title").value("Recipe 2"));
    }*/

    @Test
    @WithMockUser
    void testRecommendRecipesError() throws Exception {
        String memberId = "testUser";
        when(recipeService.findRecipesByIngredients(memberId)).thenThrow(new RuntimeException("Test error"));

        mockMvc.perform(get("/api/recipes/recommend")
                        .with(user(memberId)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Error recommending recipes")));
    }

}
