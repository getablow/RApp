package org.zerock.recipe.service;


import org.zerock.recipe.domain.RefrigeratorItem;
import org.zerock.recipe.dto.PageRequestDTO;
import org.zerock.recipe.dto.PageResponseDTO;
import org.zerock.recipe.dto.RefrigeratorItemDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface RefrigeratorItemService {

    PageResponseDTO<RefrigeratorItemDTO> getAllItemsByMemberId(PageRequestDTO pageRequestDTO, String memberId);

    RefrigeratorItemDTO addItem(RefrigeratorItemDTO itemDTO, String memberId);

    void delItem(Long id, String memberId);

    //modelmapper 사용하지않고 메소드만들자
    default RefrigeratorItem dtoToEntity(RefrigeratorItemDTO itemDTO){

        RefrigeratorItem refrigeratorItem = RefrigeratorItem.builder()
                .id(itemDTO.getId())
                .itemName(itemDTO.getItemName())
                .quantity(itemDTO.getQuantity())
                .expirationDate(itemDTO.getExpirationDate())
                .build();

        return refrigeratorItem;

    }

    default RefrigeratorItemDTO entityToDto(RefrigeratorItem item){

        RefrigeratorItemDTO refrigeratorItemDTO = RefrigeratorItemDTO.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .quantity(item.getQuantity())
                .expirationDate(item.getExpirationDate())
                .build();

        return refrigeratorItemDTO;
    }
}
