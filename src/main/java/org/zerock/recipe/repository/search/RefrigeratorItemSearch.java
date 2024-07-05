package org.zerock.recipe.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.recipe.dto.RefrigeratorItemDTO;

public interface RefrigeratorItemSearch {

    Page<RefrigeratorItemDTO> searchWithAll(String keyword, Pageable pageable, Long refrigeratorId);
}
