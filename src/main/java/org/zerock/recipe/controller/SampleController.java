package org.zerock.recipe.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.recipe.dto.RecipeDTO;
import org.zerock.recipe.dto.RecipeIngredientDTO;
import org.zerock.recipe.dto.upload.UploadResultDTO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@Log4j2
public class SampleController {

    @Operation(summary = "ingredient")
    @PostMapping("/saveingredient")
    public String saveIng(RecipeDTO recipeDTO) {

        log.info(recipeDTO);

        if(recipeDTO.getIngredients() != null) {
            recipeDTO.getIngredients().forEach(ingredients -> {

                log.info(ingredients.getName());

            });
        }

        return null;
    }

    @Operation(summary = "hello")
    @GetMapping("/hello")
    public void hello(Model model) {

        log.info("hello................");

        model.addAttribute("msg", "HELLO WORLD");
    }

    @Operation(summary = "upload")
    @GetMapping("/upload")
    public void upload(MultipartFile multipartFile) {

        log.info("multipart................");

    }

    @Operation(summary = "ex1")
    @GetMapping("/ex/ex1")
    public void ex1(Model model) {

        List<String> list = Arrays.asList("AAA", "BBB", "CCC", "DDD");

        model.addAttribute("list", list);

    }

    class SampleDTO {
        private String p1, p2, p3;

        public String getP1() {
            return p1;
        }

        public String getP2() {
            return p2;
        }

        public String getP3() {
            return p3;
        }
    }

    @Operation(summary = "ex2")
    @GetMapping("/ex/ex2")
    public void ex2(Model model) {

        log.info("ex/ex2................");

        List<String> strList = IntStream.range(1, 10)
                .mapToObj(i -> "Data" + i)
                .collect(Collectors.toList());

        model.addAttribute("list", strList);

        Map<String, String> map = new HashMap<>();
        map.put("A", "AAAA");
        map.put("B", "BBBB");

        model.addAttribute("map", map);

        SampleDTO sampleDTO = new SampleDTO();
        sampleDTO.p1 = "Value -- p1";
        sampleDTO.p2 = "Value -- p2";
        sampleDTO.p3 = "Value -- p3";

        model.addAttribute("dto", sampleDTO);
    }

    @GetMapping("/ex/ex3")
    public void ex3(Model model) {

        model.addAttribute("arr", new String[]{"AAA", "BBB", "CCC"});

    }

    @GetMapping("/ex/ex4")
    public void ex4(Model model){

    }
}