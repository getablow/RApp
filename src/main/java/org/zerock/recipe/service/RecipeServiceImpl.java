package org.zerock.recipe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.recipe.domain.Recipe;
import org.zerock.recipe.domain.RecipeIngredient;
import org.zerock.recipe.dto.*;
import org.zerock.recipe.repository.RecipeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class RecipeServiceImpl implements RecipeService{

    private final ModelMapper modelMapper;

    private final RecipeRepository recipeRepository;


    @Override
    public Long register(RecipeDTO recipeDTO) {

        log.info("Registering recipe. DTO isPrivate: {}", recipeDTO.isPrivate());
        Recipe recipe = dtoToEntity(recipeDTO);
        log.info("Created entity. Entity isPrivate: {}", recipe.isPrivate());
        Long rid = recipeRepository.save(recipe).getRid();
        log.info("Saved entity. Returned ID: {}", rid);

        Recipe savedRecipe = recipeRepository.findById(rid).orElseThrow();
        log.info("Saved recipe. Database isPrivate: {}", savedRecipe.isPrivate());
        return rid;

    }

    @Override
    public RecipeDTO readOne(Long rid) {

        //recipe_image와 ingredients 까지 조인 처리되는 findByWithOthers()를 이용
        Optional<Recipe> result = recipeRepository.findByIdWithOthers(rid);

        Recipe recipe = result.orElseThrow();

        RecipeDTO recipeDTO = entityToDTO(recipe);

        return recipeDTO;
    }

    @Override
    public void modify(RecipeDTO recipeDTO) {

        Optional<Recipe> result = recipeRepository.findById(recipeDTO.getRid());

        Recipe recipe = result.orElseThrow();

        recipe.change(recipeDTO.getTitle(), recipeDTO.getContent(), recipeDTO.getVideoUrl(), recipeDTO.isPrivate());

        //첨부파일의 처리
        recipe.clearImages();

        if(recipeDTO.getFileNames() != null){
            for(String fileName : recipeDTO.getFileNames()) {
                String[] arr = fileName.split("_");
                recipe.addImage(arr[0], arr[1]);
            }
        }

        recipe.clearIngredients();

        if(recipeDTO.getIngredients() != null){
            for(RecipeIngredientDTO ingredient : recipeDTO.getIngredients()) {
                recipe.addIngredients(ingredient.getName(), ingredient.getAmount());
            }
        }

        recipeRepository.save(recipe);

    }

    @Override
    public void remove(Long bno) {

        recipeRepository.deleteById(bno);

    }


    @Override
    public PageResponseDTO<RecipeDTO> list(PageRequestDTO pageRequestDTO) {

        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("rid");

        Page<Recipe> result = recipeRepository.searchAll(types, keyword, pageable);

        List<RecipeDTO> dtoList = result.getContent().stream()
                .map(recipe -> modelMapper.map(recipe,RecipeDTO.class)).collect(Collectors.toList());


        return PageResponseDTO.<RecipeDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();

    }

    //댓글 개수까지 불러오는 list 메소드이다
    @Override
    public PageResponseDTO<RecipeListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("rid");

        Page<RecipeListReplyCountDTO> result = recipeRepository.searchWithReplyCount(types, keyword, pageable);

        return PageResponseDTO.<RecipeListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }


    @Override
    public PageResponseDTO<RecipeListAllDTO> listWithAll(PageRequestDTO pageRequestDTO){
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("rid");

        Page<RecipeListAllDTO> result = recipeRepository.searchWithAll(types, keyword, pageable);

        return PageResponseDTO.<RecipeListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

}
