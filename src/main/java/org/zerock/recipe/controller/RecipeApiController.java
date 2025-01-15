package org.zerock.recipe.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.recipe.domain.Recipe;
import org.zerock.recipe.dto.RecipeDTO;
import org.zerock.recipe.service.RecipeService;
import org.zerock.recipe.service.RefrigeratorItemService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.zerock.recipe.domain.QRecipe.recipe;

@RestController
@RequestMapping("/api/recipes")
@Log4j2
@RequiredArgsConstructor
public class RecipeApiController {

    private final RecipeService recipeService;



    @Operation(summary = "Get recipe recommendations", description = "Returns a list of recommended recipes for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/recommend")
    public ResponseEntity<?> recommendRecipes(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            //유저정보출력
            log.info("Authenticated user: {}", userDetails.getUsername());

            // 로그인한 사용자의 냉장고 재료를 이용하여 레시피 추천
            List<RecipeDTO> recommendedRecipes = recipeService.findRecipesByIngredients(userDetails.getUsername());

            return ResponseEntity.ok(recommendedRecipes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error recommending recipes: " + e.getMessage());
        }
    }



}

