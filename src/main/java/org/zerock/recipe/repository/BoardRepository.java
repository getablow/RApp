package org.zerock.recipe.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.recipe.domain.Board;
import org.zerock.recipe.repository.search.BoardSearch;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {

    @EntityGraph(attributePaths = {"imageSet"}) //loading together
    @Query("select b from Board b where b.bno = :bno")
    Optional<Board> findByIdWithImages(Long bno);

}
