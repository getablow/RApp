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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.recipe.domain.Recipe;
import org.zerock.recipe.dto.*;
import org.zerock.recipe.service.RecipeService;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Controller
@RequestMapping("/recipe")
@Log4j2
@RequiredArgsConstructor
public class RecipeController {

    @Value("${org.zerock.upload.path}") //import 시 springframework로 시작하는 value
    private String uploadPath;

    private final RecipeService recipeService;


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/communityPage")
    public void communityPage(PageRequestDTO pageRequestDTO, Model model){

        PageResponseDTO<RecipeListAllDTO> responseDTO = recipeService.listWithAll(pageRequestDTO);

        log.info(responseDTO);

        model.addAttribute("responseDTO", responseDTO);

    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/personalPage")
    public void personalPage(PageRequestDTO pageRequestDTO, Model model){

        PageResponseDTO<RecipeListAllDTO> responseDTO = recipeService.listWithAll(pageRequestDTO);

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
            return "/recipe/register";
            //redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors() );
            //return "redirect:/recipe/register";
        }

        try {
            Long rid = recipeService.register(recipeDTO);
            log.info("recipe registered successfully with Id: {}", rid);
        } catch (Exception e) {
            log.error("Error occurred while registering recipe", e);
            redirectAttributes.addFlashAttribute("error", "An error occurred while registering the recipe");
        }

        //log.info(recipeDTO);

        //Long rid = recipeService.register(recipeDTO);

        //redirectAttributes.addFlashAttribute("result", rid);

        return "redirect:/recipe/list";


    }


    @PreAuthorize("isAuthenticated()") //로그인 사용자 제한
    @GetMapping({"/read", "/modify"})
    public void read(Long rid, PageRequestDTO pageRequestDTO, Model model, @SessionAttribute("referer") String referer){

        //System.out.println("Referer in Controller: " + referer);
        pageRequestDTO.setOrigin(referer!=null? referer : "personal");

        RecipeDTO recipeDTO = recipeService.readOne(rid);

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

}
