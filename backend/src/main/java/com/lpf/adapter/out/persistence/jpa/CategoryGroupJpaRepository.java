package com.lpf.adapter.out.persistence.jpa;

import com.lpf.adapter.out.persistence.entity.CategoryGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryGroupJpaRepository extends JpaRepository<CategoryGroupEntity, UUID> {
    List<CategoryGroupEntity> findByUserIdOrderBySortOrderAsc(UUID userId);

    boolean existsByUserId(UUID userId);
}
