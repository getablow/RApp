package org.zerock.recipe.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.recipe.domain.RefrigeratorItem;
import org.zerock.recipe.repository.search.RefrigeratorItemSearch;

import java.util.List;
import java.util.Optional;

public interface RefrigeratorItemRepository extends JpaRepository<RefrigeratorItem, Long>, RefrigeratorItemSearch {

    @EntityGraph(attributePaths = {"refrigerator", "refrigerator.member"})
    @Query("SELECT b FROM RefrigeratorItem b WHERE b.id = :id")
    Optional<RefrigeratorItem> findByIdWithOthers(Long id);

    @EntityGraph(attributePaths = {"refrigerator", "refrigerator.member"})
    @Query("SELECT ri FROM RefrigeratorItem ri WHERE ri.refrigerator.member.mid = :memberId")
    List<RefrigeratorItem> findByMemberId(@Param("memberId") String memberId);

    @EntityGraph(attributePaths = {"refrigerator", "refrigerator.member"})
    @Query("SELECT ri FROM RefrigeratorItem ri WHERE ri.id = :id OR ri.refrigerator.member.mid = :memberId")
    List<RefrigeratorItem> findByIdOrMemberId(@Param("id") Long id, @Param("memberId") String memberId);

    @Query("SELECT ri, r.member "
            + "FROM RefrigeratorItem ri "
            + "JOIN ri.refrigerator r "
            + "WHERE r.member.mid = :memberId")
    List<Object[]> findRefrigeratorItemsWithMember(@Param("memberId") String memberId);

}
