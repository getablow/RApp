package org.zerock.recipe.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.recipe.domain.Refrigerator;

import java.util.Optional;

public interface RefrigeratorRepository extends JpaRepository<Refrigerator, Long> {

    @EntityGraph(attributePaths = {"member"})
    @Query("SELECT r FROM Refrigerator r WHERE r.member.mid = :memberId")
    Optional<Refrigerator> findByMemberId(@Param("memberId") String memberId);
}
