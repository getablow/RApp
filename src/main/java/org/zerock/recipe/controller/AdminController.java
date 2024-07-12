package org.zerock.recipe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zerock.recipe.domain.Recipe;
import org.zerock.recipe.dto.ActivityByHourDTO;
import org.zerock.recipe.dto.RecipeDTO;
import org.zerock.recipe.service.RecipeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@Log4j2
@RequiredArgsConstructor
public class AdminController {

    private final RecipeService recipeService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/home")
    public void adminHome() {

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/statistics")
    public void statisticsGET(Model model){
        List<RecipeDTO> topLikedRecipes = recipeService.getTopLikedRecipes();
        model.addAttribute("recipes", topLikedRecipes);
    }




    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/inquiry")
    public void inquiryGET(){

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/managementUser")
    public void managementUserGET(){

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/managementPost")
    public void managementPostGET(){

    }


    /**
     * 좋아요 수 기준 상위 10개 레시피 통계 화면
     * temporarily
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/statistics/topLikedRecipes")
    public String getTopLikedRecipes(Model model) {
        List<RecipeDTO> topLikedRecipes = recipeService.getTopLikedRecipes();
        model.addAttribute("recipes", topLikedRecipes);
        return "admin/topLikedRecipes";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/statistics/topViewedRecipes")
    public String getViewedRecipes(Model model) {
        List<RecipeDTO> topViewedRecipes = recipeService.getTopViewedRecipes();
        model.addAttribute("recipes", topViewedRecipes);
        return "admin/topViewedRecipes";
    }


    /**
     * 좋아요수, 조회수 기반 시간대별 활성도
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/statistics/activity-by-hour")
    public String getActivityByHour(Model model) {
        List<ActivityByHourDTO> activityByHour = recipeService.getViewCountAndFavoriteCountByHour();
        model.addAttribute("activityByHour", activityByHour);
        return "admin/activity_by_hour";
    }
}
