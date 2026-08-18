package com.lpf.adapter.out.persistence.jpa;

import com.lpf.adapter.out.persistence.entity.LineItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LineItemJpaRepository extends JpaRepository<LineItemEntity, UUID> {
    List<LineItemEntity> findByUserIdOrderBySortOrderAsc(UUID userId);

    @Query("select max(i.sortOrder) from LineItemEntity i where i.userId = :userId and i.categoryId = :categoryId")
    Optional<Integer> findMaxSortOrder(@Param("userId") UUID userId, @Param("categoryId") UUID categoryId);
}
