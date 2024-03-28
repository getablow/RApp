package org.zerock.recipe.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.recipe.domain.Board;
import org.zerock.recipe.dto.BoardListAllDTO;
import org.zerock.recipe.dto.BoardListReplyCountDTO;

public interface BoardSearch {

    Page<Board> search1(Pageable pageable);

    Page<Board> searchAll(String[] types, String keyword, Pageable pageable);

    Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable);

    Page<BoardListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable);
}
