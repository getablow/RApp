package org.zerock.recipe.service;


public interface FavoriteService {

    int favoriteRecipe(String mid, Long rid);

    int getFavoriteCount(Long rid);


}
