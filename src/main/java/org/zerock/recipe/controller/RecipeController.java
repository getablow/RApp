package org.zerock.recipe.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.recipe.dto.*;
import org.zerock.recipe.service.FavoriteService;
import org.zerock.recipe.service.RecipeService;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/recipe")
@Log4j2
@RequiredArgsConstructor
public class RecipeController {

    @Value("${org.zerock.upload.path}") //import 시 springframework로 시작하는 value
    private String uploadPath;

    private final RecipeService recipeService;
    //private final FavoriteService favoriteService; //임시로 해놓음. 지우고 favoriteService는 RecipeService에 전달받아 사용되어야만 한다.


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/communityPage")
    public void communityPage(PageRequestDTO pageRequestDTO, Model model){


        PageResponseDTO<RecipeListAllDTO> responseDTO = recipeService.listWithReveal(pageRequestDTO, true);

        log.info(responseDTO);

        model.addAttribute("responseDTO", responseDTO);


    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/personalPage")
    public void personalPage(PageRequestDTO pageRequestDTO, Model model, @AuthenticationPrincipal User user){

        PageResponseDTO<RecipeListAllDTO> responseDTO = recipeService.listWithAllByWriter(pageRequestDTO, user.getUsername());

        log.info("Login User Name = " + user.getUsername());

        log.info(responseDTO);

        model.addAttribute("responseDTO", responseDTO);

    }


    @PreAuthorize("hasRole('USER')") //사전에 권한 체크 postauthorize-사후
    @GetMapping("/register")
    public void registerGET(){

    }



    @PostMapping("/register")
    public String registerPost(@Valid RecipeDTO recipeDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){

        log.info("Recipe registration attempt");

        log.info("Received RecipeDTO: {}", recipeDTO);

        if(bindingResult.hasErrors()) {
            log.warn("Recipe registration failed due to validation errors");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/recipe/register";
        }

        try {
            Long rid = recipeService.register(recipeDTO);
            log.info("recipe registered successfully with Id: {}", rid);
            redirectAttributes.addAttribute("rid",rid);
            return "redirect:/recipe/personalPage";
        } catch (Exception e) {
            log.error("Error occurred while registering recipe", e);
            redirectAttributes.addFlashAttribute("error", "An error occurred while registering the recipe");
            return "redirect:/recipe/register";
        }

    }


    @PreAuthorize("isAuthenticated()") //로그인 사용자 제한
    @GetMapping({"/read", "/modify"})
    public void read(Long rid, PageRequestDTO pageRequestDTO, Model model, @SessionAttribute("referer") String referer, @AuthenticationPrincipal User user){

        pageRequestDTO.setOrigin(referer != null? referer : "personal");

        RecipeDTO recipeDTO = recipeService.readOne(user.getUsername(), rid);

        log.info(recipeDTO);

        model.addAttribute("dto", recipeDTO);
        model.addAttribute("pageRequestDTO", pageRequestDTO);

    }


    @PreAuthorize("principal.username == #recipeDTO.writer")
    @PostMapping("/modify")
    public String modify( PageRequestDTO pageRequestDTO,
                          @Valid RecipeDTO recipeDTO,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes){

        log.info("recipe modify post......." + recipeDTO);

        log.info("Received RecipeDTO: {}", recipeDTO);
        log.info("isReveal value: {}", recipeDTO.isReveal());

        if(bindingResult.hasErrors()) {
            log.info("has errors.......");

            String link = pageRequestDTO.getLink();

            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors() );

            redirectAttributes.addAttribute("rid", recipeDTO.getRid());

            return "redirect:/recipe/modify?"+link;
        }

        recipeService.modify(recipeDTO);

        redirectAttributes.addFlashAttribute("result", "modified");

        redirectAttributes.addAttribute("rid", recipeDTO.getRid());

        return "redirect:/recipe/read";
    }

    @PreAuthorize("principal.username == #recipeDTO.writer")
    @PostMapping("/remove")
    public String remove(RecipeDTO recipeDTO, RedirectAttributes redirectAttributes){

        Long rid = recipeDTO.getRid();
        log.info("remove post.. " + rid);

        recipeService.remove(rid);

        //if the post has been deleted from the db, file is deleted
        log.info(recipeDTO.getFileNames());
        List<String> fileNames = recipeDTO.getFileNames();

        if(fileNames != null && fileNames.size() > 0){
            removeFiles(fileNames);
        }

        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/recipe/list";

    }

    public void removeFiles(List<String> files){

        for(String fileName:files) {
            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
            String resourceName = resource.getFilename();

            try{
                String contentType = Files.probeContentType(resource.getFile().toPath());

                resource.getFile().delete();

                //if there's a thumbnail
                if(contentType.startsWith("image")){
                    File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                    thumbnailFile.delete();
                }
            } catch(Exception e){
                log.error(e.getMessage());
            }
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/refrigerator")
    public void refrigerator(PageRequestDTO pageRequestDTO, Model model){

        PageResponseDTO<RecipeListAllDTO> responseDTO = recipeService.listWithAll(pageRequestDTO);

        log.info(responseDTO);

        model.addAttribute("responseDTO", responseDTO);

    }


    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{rid}/favorite")
    public ResponseEntity<?> FavoriteRecipe(@PathVariable Long rid, @AuthenticationPrincipal User user) {
        log.info("User {} is attempting to like recipe {}", user.getUsername(), rid);
        try {
            Map<String, Object> response = recipeService.favoriteRecipe(user.getUsername(), rid);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while favoriting recipe", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An error occurred while favoriting the recipe"));
        }
    }
    //좋아요 눌렀는지 확인용

    @GetMapping("/{rid}/likeCount")
    public ResponseEntity<?> getFavoriteCount(@PathVariable Long rid) {
        try {
            int likeCount = recipeService.getFavoriteCount(rid);
            Map<String, Object> response = new HashMap<>();
            response.put("favoriteCount", likeCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while getting favorite count", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred while getting the favorite count");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



}
