package com.lpf.adapter.out.persistence.jpa;

import com.lpf.adapter.out.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {
    List<CategoryEntity> findByUserIdOrderBySortOrderAsc(UUID userId);

    @Query("select max(c.sortOrder) from CategoryEntity c where c.userId = :userId and c.groupId = :groupId")
    Optional<Integer> findMaxSortOrder(@Param("userId") UUID userId, @Param("groupId") UUID groupId);
}
