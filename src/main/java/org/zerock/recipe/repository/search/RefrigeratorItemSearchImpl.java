package org.zerock.recipe.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.recipe.domain.*;
import org.zerock.recipe.dto.RefrigeratorItemDTO;


import java.util.List;
import java.util.stream.Collectors;


public class RefrigeratorItemSearchImpl extends QuerydslRepositorySupport implements RefrigeratorItemSearch{
    public RefrigeratorItemSearchImpl() {
        super(RefrigeratorItem.class);
    }

    private JPQLQuery<RefrigeratorItem> getRefrigeratorItemJPQLQuery(BooleanBuilder booleanBuilder, Pageable pageable, Long refrigeratorId) {
        QRefrigeratorItem refrigeratorItem = QRefrigeratorItem.refrigeratorItem;

        JPQLQuery<RefrigeratorItem> refrigeratorItemJPQLQuery = from(refrigeratorItem)
                .select(refrigeratorItem);

        if (refrigeratorId != null) {
            refrigeratorItemJPQLQuery.where(refrigeratorItem.refrigerator.id.eq(refrigeratorId));
        }

        if (booleanBuilder != null) {
            refrigeratorItemJPQLQuery.where(booleanBuilder);
        }

        getQuerydsl().applyPagination(pageable, refrigeratorItemJPQLQuery);

        return refrigeratorItemJPQLQuery;
    }

    private BooleanBuilder getBooleanBuilder(String keyword) {

        QRefrigeratorItem refrigeratorItem = QRefrigeratorItem.refrigeratorItem;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (keyword != null && !keyword.trim().isEmpty()) {
            booleanBuilder.and(refrigeratorItem.itemName.containsIgnoreCase(keyword.trim()));
        }
        return booleanBuilder;
    }

    private Page<RefrigeratorItemDTO> getRefrigeratorItemDTOPage(JPQLQuery<RefrigeratorItem> query, Pageable pageable) {
        List<RefrigeratorItem> itemList = query.fetch();
        List<RefrigeratorItemDTO> dtoList = itemList.stream()
                .map(this::createRefrigeratorItemDTO)
                .collect(Collectors.toList());
        long totalCount = query.fetchCount();
        return new PageImpl<>(dtoList, pageable, totalCount);
    }

    private RefrigeratorItemDTO createRefrigeratorItemDTO(RefrigeratorItem item) {

        RefrigeratorItemDTO dto = RefrigeratorItemDTO.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .quantity(item.getQuantity())
                .expirationDate(item.getExpirationDate())
                .addedDate(item.getAddedDate())
                .build();

        return dto;
    }


    @Override
    public Page<RefrigeratorItemDTO> searchWithAll(String keyword, Pageable pageable, Long refrigeratorId) {
        BooleanBuilder booleanBuilder = getBooleanBuilder(keyword);
        JPQLQuery<RefrigeratorItem> refrigeratorItemJPQLQuery = getRefrigeratorItemJPQLQuery(booleanBuilder, pageable, refrigeratorId);
        return getRefrigeratorItemDTOPage(refrigeratorItemJPQLQuery, pageable);
    }


}
