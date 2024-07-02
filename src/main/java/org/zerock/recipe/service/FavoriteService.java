package org.zerock.recipe.service;


import java.util.Map;

public interface FavoriteService {

    Map<String, Object> favoriteRecipe(String mid, Long rid);

    boolean getFavoriteConfirm(String mid, Long rid);

    int getFavoriteCount(Long rid);


}
