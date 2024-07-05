package org.zerock.recipe.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.recipe.domain.Member;
import org.zerock.recipe.domain.Refrigerator;
import org.zerock.recipe.domain.RefrigeratorItem;
import org.zerock.recipe.dto.PageRequestDTO;
import org.zerock.recipe.dto.PageResponseDTO;
import org.zerock.recipe.dto.RecipeListAllDTO;
import org.zerock.recipe.dto.RefrigeratorItemDTO;
import org.zerock.recipe.repository.MemberRepository;
import org.zerock.recipe.repository.RefrigeratorItemRepository;
import org.zerock.recipe.repository.RefrigeratorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class RefrigeratorItemServiceImpl implements RefrigeratorItemService {

    private final RefrigeratorRepository refrigeratorRepository;
    private final RefrigeratorItemRepository refrigeratorItemRepository;
    private final MemberRepository memberRepository;

    @Override
    public PageResponseDTO<RefrigeratorItemDTO> getAllItemsByMemberId(PageRequestDTO pageRequestDTO, String memberId) {

        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageableAsc("expirationDate");

        Long refrigeratorId = getRefrigeratorIdByMemberId(memberId);

        Page<RefrigeratorItemDTO> result = refrigeratorItemRepository.searchWithAll(keyword, pageable, refrigeratorId);

        return PageResponseDTO.<RefrigeratorItemDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }


    private Long getRefrigeratorIdByMemberId(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Refrigerator refrigerator = member.getRefrigerator();
        if (refrigerator == null) {
            throw new RuntimeException("RefrigeratorRepository not found for member");
        }
        return refrigerator.getId();
    }



    @Override
    public RefrigeratorItemDTO addItem(RefrigeratorItemDTO itemDTO, String memberId) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 회원의 냉장고 조회
        Refrigerator refrigerator = refrigeratorRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Refrigerator not found for member"));

        // 3. RefrigeratorItem 엔티티 생성
        RefrigeratorItem newItem = RefrigeratorItem.builder()
                .itemName(itemDTO.getItemName())
                .quantity(itemDTO.getQuantity())
                .expirationDate(itemDTO.getExpirationDate())
                .refrigerator(refrigerator)
                .build();

        // 4. RefrigeratorItem 저장
        RefrigeratorItem savedItem = refrigeratorItemRepository.save(newItem);

        // 5. 저장된 엔티티를 DTO로 변환하여 반환
        return entityToDto(savedItem);
    }



}
