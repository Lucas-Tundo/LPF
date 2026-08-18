package com.lpf.adapter.out.persistence;

import com.lpf.adapter.out.persistence.jpa.ProfileJpaRepository;
import com.lpf.application.port.out.ProfileRepositoryPort;
import com.lpf.domain.model.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ProfileRepositoryAdapter implements ProfileRepositoryPort {

    private final ProfileJpaRepository jpa;

    public ProfileRepositoryAdapter(ProfileJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Profile> findById(UUID id) {
        return jpa.findById(id).map(PersistenceMapper::toDomain);
    }

    @Override
    public Profile save(Profile profile) {
        return PersistenceMapper.toDomain(jpa.save(PersistenceMapper.toEntity(profile)));
    }
}
