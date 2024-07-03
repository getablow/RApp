package org.zerock.recipe.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.el.parser.BooleanNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.zerock.recipe.domain.*;
import org.zerock.recipe.dto.RecipeImageDTO;
import org.zerock.recipe.dto.RecipeIngredientDTO;
import org.zerock.recipe.dto.RecipeListAllDTO;
import org.zerock.recipe.dto.RecipeListReplyCountDTO;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeSearchImpl extends QuerydslRepositorySupport implements RecipeSearch{

    public RecipeSearchImpl(){
        super(Recipe.class);
    }

    private JPQLQuery<Tuple> getRecipeJPQLQuery(BooleanBuilder booleanBuilder, String writer, Boolean reveal, Pageable pageable) {
        QRecipe recipe = QRecipe.recipe;
        QRecipeReply recipeReply = QRecipeReply.recipeReply;

        JPQLQuery<Recipe> recipeJPQLQuery = from(recipe);
        recipeJPQLQuery.leftJoin(recipeReply).on(recipeReply.recipe.eq(recipe));

        if (writer != null) {
            recipeJPQLQuery.where(recipe.writer.eq(writer));
        }

        if (reveal != null) {
            recipeJPQLQuery.where(recipe.reveal.eq(true));
        }

        if (booleanBuilder != null) {
            recipeJPQLQuery.where(booleanBuilder);
        }


        recipeJPQLQuery.groupBy(recipe);

        getQuerydsl().applyPagination(pageable, recipeJPQLQuery);

        return recipeJPQLQuery.select(recipe, recipeReply.countDistinct());
    }

    private BooleanBuilder getBooleanBuilder(String[] types, String keyword) {
        QRecipe recipe = QRecipe.recipe;

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if((types != null && types.length > 0) && keyword != null) {
            for (String type : types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(recipe.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(recipe.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(recipe.writer.contains(keyword));
                        break;
                }
            }
        }
        return booleanBuilder;
    }

    private Page<RecipeListAllDTO> getRecipeListAllDTOPage(JPQLQuery<Tuple> tupleJPQLQuery, Pageable pageable) {
        List<Tuple> tupleList = tupleJPQLQuery.fetch();
        List<RecipeListAllDTO> dtoList = tupleList.stream().map(this::createRecipeAllDTO).collect(Collectors.toList());
        long totalCount = tupleJPQLQuery.fetchCount();
        return new PageImpl<>(dtoList, pageable, totalCount);
    }

    private RecipeListAllDTO createRecipeAllDTO(Tuple tuple) {
        Recipe recipe = (Recipe) tuple.get(QRecipe.recipe);
        long replyCount = tuple.get(1, Long.class);

        RecipeListAllDTO dto = RecipeListAllDTO.builder()
                .rid(recipe.getRid())
                .title(recipe.getTitle())
                .writer(recipe.getWriter())
                .regDate(recipe.getRegDate())
                .viewCount(recipe.getViewCount())
                .favoriteCount(recipe.getFavoriteCount())
                .replyCount(replyCount)
                .build();

        //RecipeImage를 RecipeImageDTO 처리할 부분
        List<RecipeImageDTO> imageDTOS = recipe.getImageSet().stream().sorted()
                .map(recipeImage -> RecipeImageDTO.builder()
                        .uuid(recipeImage.getUuid())
                        .fileName(recipeImage.getFileName())
                        .ord(recipeImage.getOrd())
                        .build()
                ).collect(Collectors.toList());

        dto.setRecipeImages(imageDTOS); //처리된 RecipeImageDTO들을 추가

        //IngredientDTO 처리
        List<RecipeIngredientDTO> ingredientDTOS = recipe.getIngredientSet().stream()
                .sorted(Comparator.comparing(RecipeIngredient::getIno)) // ino를 기준으로 정렬
                .map(recipeIngredient -> RecipeIngredientDTO.builder()
                        .ino(recipeIngredient.getIno())
                        .name(recipeIngredient.getName())
                        .amount(recipeIngredient.getAmount())
                        .build())
                .collect(Collectors.toList());

        dto.setRecipeIngredients(ingredientDTOS);

        return dto;
    }

    @Override
    public Page<Recipe> search1(Pageable pageable) {

        QRecipe recipe = QRecipe.recipe;

        JPQLQuery<Recipe> query = from(recipe);

        BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

        booleanBuilder.or(recipe.title.contains("11")); // title like ...

        booleanBuilder.or(recipe.content.contains("11")); // content like ....

        query.where(booleanBuilder);
        query.where(recipe.rid.gt(0L));


        //paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<Recipe> list = query.fetch();

        long count = query.fetchCount();


        return null;

    }

    @Override
    public Page<Recipe> searchAll(String[] types, String keyword, Pageable pageable) {

        QRecipe recipe = QRecipe.recipe;
        JPQLQuery<Recipe> query = from(recipe);

        if( (types != null && types.length > 0) && keyword != null ){ //검색 조건과 키워드가 있다면

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for(String type: types){

                switch (type){
                    case "t":
                        booleanBuilder.or(recipe.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(recipe.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(recipe.writer.contains(keyword));
                        break;
                }
            }//end for
            query.where(booleanBuilder);
        }//end if

        //bno > 0
        query.where(recipe.rid.gt(0L));

        //paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<Recipe> list = query.fetch();

        long count = query.fetchCount();

        return new PageImpl<>(list, pageable, count);

    }



    @Override
    public Page<RecipeListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable){

        QRecipe recipe = QRecipe.recipe;
        QRecipeReply recipeReply = QRecipeReply.recipeReply;

        JPQLQuery<Recipe> query = from(recipe);
        query.leftJoin(recipeReply).on(recipeReply.recipe.eq(recipe)); //join condition

        query.groupBy(recipe);

        if( (types != null && types.length > 0) && keyword != null ){
            BooleanBuilder booleanBuilder = new BooleanBuilder(); //(

            for(String type: types){
                switch (type) {
                    case "t":
                        booleanBuilder.or(recipe.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(recipe.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(recipe.writer.contains(keyword));
                        break;
                }

            }//end for
            query.where(booleanBuilder);
        }//end if

        //bno > 0
        query.where(recipe.rid.gt(0L));

        JPQLQuery<RecipeListReplyCountDTO> dtoQuery = query.select(Projections.bean(RecipeListReplyCountDTO.class,
                recipe.rid,
                recipe.title,
                recipe.writer,
                recipe.regDate,
                recipeReply.count().as("replyCount")
        ));

        this.getQuerydsl().applyPagination(pageable, dtoQuery);

        List<RecipeListReplyCountDTO> dtoList = dtoQuery.fetch();

        long count = dtoQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, count);
    }

    @Override
    public Page<RecipeListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {
        BooleanBuilder booleanBuilder = getBooleanBuilder(types, keyword);
        JPQLQuery<Tuple> tupleJPQLQuery = getRecipeJPQLQuery(booleanBuilder, null, null,pageable);
        return getRecipeListAllDTOPage(tupleJPQLQuery, pageable);
    }

    /*public Page<RecipeListAllDTO> searchWithAll1(String[] types, String keyword, Pageable pageable){

        QRecipe recipe = QRecipe.recipe;
        QRecipeReply recipeReply = QRecipeReply.recipeReply;

        JPQLQuery<Recipe> recipeJPQLQuery = from(recipe);
        recipeJPQLQuery.leftJoin(recipeReply).on(recipeReply.recipe.eq(recipe)); //left join

        if( (types != null && types.length > 0) && keyword != null ){
            BooleanBuilder booleanBuilder = new BooleanBuilder(); //(

            for(String type: types){

                switch(type){
                    case "t":
                        booleanBuilder.or(recipe.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(recipe.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(recipe.writer.contains(keyword));
                        break;
                }
            }
            recipeJPQLQuery.where(booleanBuilder);
        }

        recipeJPQLQuery.groupBy(recipe);

        getQuerydsl().applyPagination(pageable, recipeJPQLQuery); //paging

        JPQLQuery<Tuple> tupleJPQLQuery = recipeJPQLQuery.select(recipe, recipeReply.countDistinct());

        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        List<RecipeListAllDTO> dtoList = tupleList.stream().map(tuple -> {

            Recipe recipe1 = (Recipe) tuple.get(recipe);
            long replyCount = tuple.get(1, Long.class);

            RecipeListAllDTO dto = RecipeListAllDTO.builder()
                    .rid(recipe1.getRid())
                    .title(recipe1.getTitle())
                    .writer(recipe1.getWriter())
                    .regDate(recipe1.getRegDate())
                    .replyCount(replyCount)
                    .build();

            //RecipeImage를 RecipeImageDTO 처리할 부분
            List<RecipeImageDTO> imageDTOS = recipe1.getImageSet().stream().sorted()
                    .map(recipeImage -> RecipeImageDTO.builder()
                            .uuid(recipeImage.getUuid())
                            .fileName(recipeImage.getFileName())
                            .ord(recipeImage.getOrd())
                            .build()
                    ).collect(Collectors.toList());

            dto.setRecipeImages(imageDTOS); //처리된 RecipeImageDTO들을 추가

            //IngredientDTO 처리
            List<RecipeIngredientDTO> ingredientDTOS = recipe1.getIngredientSet().stream()
                    .sorted(Comparator.comparing(RecipeIngredient::getIno)) // ino를 기준으로 정렬
                    .map(recipeIngredient -> RecipeIngredientDTO.builder()
                            .ino(recipeIngredient.getIno())
                            .name(recipeIngredient.getName())
                            .amount(recipeIngredient.getAmount())
                            .build())
                    .collect(Collectors.toList());

            dto.setRecipeIngredients(ingredientDTOS);

            return dto;

        }).collect(Collectors.toList());

        long totalCount = recipeJPQLQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }*/


    @Override
    public Page<RecipeListAllDTO> searchWithAllByWriter(String[] types, String keyword, String writer, Pageable pageable) {

        BooleanBuilder booleanBuilder = getBooleanBuilder(types, keyword);
        JPQLQuery<Tuple> tupleJPQLQuery = getRecipeJPQLQuery(booleanBuilder, writer, null,pageable);
        return getRecipeListAllDTOPage(tupleJPQLQuery, pageable);

    }

    @Override
    public Page<RecipeListAllDTO> searchWithReveal(String[] types, String keyword, Boolean reveal, Pageable pageable){

        BooleanBuilder booleanBuilder = getBooleanBuilder(types, keyword);
        JPQLQuery<Tuple> tupleJPQLQuery = getRecipeJPQLQuery(booleanBuilder, null, reveal, pageable);
        return getRecipeListAllDTOPage(tupleJPQLQuery, pageable);
    }

    /*QRecipe recipe = QRecipe.recipe;
        QRecipeReply recipeReply = QRecipeReply.recipeReply;

        JPQLQuery<Recipe> recipeJPQLQuery = from(recipe);
        recipeJPQLQuery.leftJoin(recipeReply).on(recipeReply.recipe.eq(recipe)); //left join
        recipeJPQLQuery.where(recipe.writer.eq(writer));

        if( (types != null && types.length > 0) && keyword != null ){
            BooleanBuilder booleanBuilder = new BooleanBuilder(); //(

            for(String type: types){

                switch(type){
                    case "t":
                        booleanBuilder.or(recipe.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(recipe.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(recipe.writer.contains(keyword));
                        break;
                }
            }
            recipeJPQLQuery.where(booleanBuilder);
        }

        recipeJPQLQuery.groupBy(recipe);

        getQuerydsl().applyPagination(pageable, recipeJPQLQuery); //paging

        JPQLQuery<Tuple> tupleJPQLQuery = recipeJPQLQuery.select(recipe, recipeReply.countDistinct());

        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        List<RecipeListAllDTO> dtoList = tupleList.stream().map(tuple -> {

            Recipe recipe1 = (Recipe) tuple.get(recipe);
            long replyCount = tuple.get(1, Long.class);

            RecipeListAllDTO dto = RecipeListAllDTO.builder()
                    .rid(recipe1.getRid())
                    .title(recipe1.getTitle())
                    .writer(recipe1.getWriter())
                    .regDate(recipe1.getRegDate())
                    .replyCount(replyCount)
                    .build();

            //RecipeImage를 RecipeImageDTO 처리할 부분
            List<RecipeImageDTO> imageDTOS = recipe1.getImageSet().stream().sorted()
                    .map(recipeImage -> RecipeImageDTO.builder()
                            .uuid(recipeImage.getUuid())
                            .fileName(recipeImage.getFileName())
                            .ord(recipeImage.getOrd())
                            .build()
                    ).collect(Collectors.toList());

            dto.setRecipeImages(imageDTOS); //처리된 RecipeImageDTO들을 추가

            //IngredientDTO 처리
            List<RecipeIngredientDTO> ingredientDTOS = recipe1.getIngredientSet().stream()
                    .sorted(Comparator.comparing(RecipeIngredient::getIno)) // ino를 기준으로 정렬
                    .map(recipeIngredient -> RecipeIngredientDTO.builder()
                            .ino(recipeIngredient.getIno())
                            .name(recipeIngredient.getName())
                            .amount(recipeIngredient.getAmount())
                            .build())
                    .collect(Collectors.toList());

            dto.setRecipeIngredients(ingredientDTOS);

            return dto;

        }).collect(Collectors.toList());

        long totalCount = recipeJPQLQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, totalCount);*/


    public Page<RecipeListAllDTO> searchWithReveal(String[] types, String keyword, Pageable pageable){
        QRecipe recipe = QRecipe.recipe;
        QRecipeReply recipeReply = QRecipeReply.recipeReply;

        JPQLQuery<Recipe> recipeJPQLQuery = from(recipe);
        recipeJPQLQuery.leftJoin(recipeReply).on(recipeReply.recipe.eq(recipe)); //left join


        if( (types != null && types.length > 0) && keyword != null ){
            BooleanBuilder booleanBuilder = new BooleanBuilder(); //(

            for(String type: types){

                switch(type){
                    case "t":
                        booleanBuilder.or(recipe.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(recipe.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(recipe.writer.contains(keyword));
                        break;
                }
            }
            recipeJPQLQuery.where(booleanBuilder);
        }

        recipeJPQLQuery.groupBy(recipe);

        getQuerydsl().applyPagination(pageable, recipeJPQLQuery); //paging

        JPQLQuery<Tuple> tupleJPQLQuery = recipeJPQLQuery.select(recipe, recipeReply.countDistinct());

        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        List<RecipeListAllDTO> dtoList = tupleList.stream().map(tuple -> {

            Recipe recipe1 = (Recipe) tuple.get(recipe);
            long replyCount = tuple.get(1, Long.class);

            RecipeListAllDTO dto = RecipeListAllDTO.builder()
                    .rid(recipe1.getRid())
                    .title(recipe1.getTitle())
                    .writer(recipe1.getWriter())
                    .regDate(recipe1.getRegDate())
                    .replyCount(replyCount)
                    .build();

            //RecipeImage를 RecipeImageDTO 처리할 부분
            List<RecipeImageDTO> imageDTOS = recipe1.getImageSet().stream().sorted()
                    .map(recipeImage -> RecipeImageDTO.builder()
                            .uuid(recipeImage.getUuid())
                            .fileName(recipeImage.getFileName())
                            .ord(recipeImage.getOrd())
                            .build()
                    ).collect(Collectors.toList());

            dto.setRecipeImages(imageDTOS); //처리된 RecipeImageDTO들을 추가

            //IngredientDTO 처리
            List<RecipeIngredientDTO> ingredientDTOS = recipe1.getIngredientSet().stream()
                    .sorted(Comparator.comparing(RecipeIngredient::getIno)) // ino를 기준으로 정렬
                    .map(recipeIngredient -> RecipeIngredientDTO.builder()
                            .ino(recipeIngredient.getIno())
                            .name(recipeIngredient.getName())
                            .amount(recipeIngredient.getAmount())
                            .build())
                    .collect(Collectors.toList());

            dto.setRecipeIngredients(ingredientDTOS);

            return dto;

        }).collect(Collectors.toList());

        long totalCount = recipeJPQLQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }
}
