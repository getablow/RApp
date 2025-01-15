package org.zerock.recipe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.recipe.domain.Member;
import org.zerock.recipe.domain.Recipe;
import org.zerock.recipe.domain.RefrigeratorItem;
import org.zerock.recipe.dto.*;
import org.zerock.recipe.mapper.RecipeMapper;
import org.zerock.recipe.repository.MemberRepository;
import org.zerock.recipe.repository.RecipeRepository;
import org.zerock.recipe.repository.RefrigeratorItemRepository;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Locale.filter;


@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class RecipeServiceImpl implements RecipeService{

    private final ModelMapper modelMapper;

    private final RecipeRepository recipeRepository;
    private final FavoriteService favoriteService;
    private final RefrigeratorItemRepository refrigeratorItemRepository;

    @Override
    public Map<String, Object> favoriteRecipe(String username, Long rid) {
        return favoriteService.favoriteRecipe(username, rid);
    }

    @Override
    public int getFavoriteCount(Long rid) {
        return favoriteService.getFavoriteCount(rid);
    }


    @Override
    public Long register(RecipeDTO recipeDTO) {

        log.info("Registering recipe. DTO isReveal: {}", recipeDTO.isReveal());
        Recipe recipe = RecipeMapper.dtoToEntity(recipeDTO);
        log.info("Created entity. Entity isReveal: {}", recipe.isReveal());
        Long rid = recipeRepository.save(recipe).getRid();
        log.info("Saved entity. Returned ID: {}", rid);

        Recipe savedRecipe = recipeRepository.findById(rid).orElseThrow();
        log.info("Saved recipe. Database isReveal: {}", savedRecipe.isReveal());
        return rid;

    }

    @Override
    public RecipeDTO readOne(String username,Long rid) {

        //recipe_image와 ingredients 까지 조인 처리되는 findByWithOthers()를 이용
        Optional<Recipe> result = recipeRepository.findByIdWithOthers(rid);

        Recipe recipe = result.orElseThrow();

        RecipeDTO recipeDTO = RecipeMapper.entityToDTO(recipe);

        recipe.riseViewCount();
        recipeRepository.save(recipe);

        boolean favoriteConfirm = favoriteService.getFavoriteConfirm(username, rid);
        int favoriteCount = favoriteService.getFavoriteCount(rid);
        int viewCount = recipe.getViewCount();

        recipeDTO.setFavoriteConfirm(favoriteConfirm);
        recipeDTO.setFavoriteCount(favoriteCount);
        recipeDTO.setViewCount(viewCount);

        // 유튜브 동영상 ID 추출 및 설정
        String videoUrl = recipeDTO.getVideoUrl();
        if (videoUrl != null && !videoUrl.isEmpty()) {
            String videoId = extractVideoId(videoUrl);
            recipeDTO.setVideoId(videoId);
        }

        return recipeDTO;
    }

    public String extractVideoId(String videoLink) {
        String videoId = null;
        String regex = "^(?:https?://)?(?:www\\.)?(?:youtube\\.com(?:/embed/|/v/|/watch\\?v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher =  pattern.matcher(videoLink);
        if(matcher.find()) {
            videoId = matcher.group(1);
        }
        return videoId;
    }

    @Override
    public void modify(RecipeDTO recipeDTO) {

        Optional<Recipe> result = recipeRepository.findById(recipeDTO.getRid());

        Recipe recipe = result.orElseThrow();

        recipe.change(recipeDTO.getTitle(), recipeDTO.getContent(), recipeDTO.getVideoUrl(), recipeDTO.isReveal());

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

    @Override
    public PageResponseDTO<RecipeListAllDTO> listWithAllByWriter(PageRequestDTO pageRequestDTO, String writer){
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();

        Pageable pageable = pageRequestDTO.getPageable("rid");

        Page<RecipeListAllDTO> result = recipeRepository.searchWithAllByWriter(types, keyword, writer, pageable);

        return PageResponseDTO.<RecipeListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<RecipeListAllDTO> listWithReveal(PageRequestDTO pageRequestDTO, Boolean reveal) {

        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("rid");

        Page<RecipeListAllDTO> result = recipeRepository.searchWithReveal(types, keyword, true, pageable);

        return PageResponseDTO.<RecipeListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    public List<RecipeDTO> getTopLikedRecipes() {
        Pageable pageable = PageRequest.of(0,10);
        List<Recipe> recipes = recipeRepository.findTopByOrderByFavoriteCountDesc(pageable);
        return recipes.stream()
                .map(RecipeMapper::entityToDTO)
                .collect(Collectors.toList());

    }

    public List<RecipeDTO> getTopViewedRecipes() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Recipe> recipes = recipeRepository.findTopByOrderByViewCount(pageable);
        return recipes.stream()
                .map(RecipeMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    public List<RecipeDTO> findRecipesByIngredients(String memberId) {
        // 사용자 냉장고 재료와 회원 정보 가져오기
        List<Object[]> refrigeratorItemsWithMember = refrigeratorItemRepository.findRefrigeratorItemsWithMember(memberId);

        // RefrigeratorItem과 Member 정보 분리하기
        List<String> ingredientNames = new ArrayList<>();
        for (Object[] result : refrigeratorItemsWithMember) {
            RefrigeratorItem item = (RefrigeratorItem) result[0];
            ingredientNames.add(item.getItemName());
        }

        // 레시피 엔티티 가져오기
        List<Recipe> recipes = recipeRepository.findRecipesByIngredientsAndWriter(ingredientNames, memberId);

        // 엔티티를 DTO로 변환
        return recipes.stream()
                .map(RecipeMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    public List<ActivityByHourDTO> getViewCountAndFavoriteCountByHour() {
        List<Object[]> results = recipeRepository.findViewCountAndFavoriteCountByHour();
        return results.stream()
                .filter(result -> result[0] != null && result[1] != null && result[2] != null)
                .map(result -> ActivityByHourDTO.builder()
                        .hour((int) result[0])
                        .viewCount((long) result[1])
                        .favoriteCount((long) result[2])
                        .build())
                .collect(Collectors.toList());
    }


}
