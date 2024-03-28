package org.zerock.recipe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.recipe.domain.Reply;



public interface ReplyRepository extends JpaRepository<Reply, Long> {


//    @Query("select r from Reply r where r.board.bno = :bno")
//    Page<Reply> listOfBoard(Long bno, Pageable pageable);

    @Query("select r from Reply r where r.board.bno = :bno")
    Page<Reply> listOfBoard(@Param("bno")Long bno, Pageable pageable); //Param 안붙이니깐 오류남 jdk 버전문제...?

    void deleteByBoard_Bno(Long Bno);
}
