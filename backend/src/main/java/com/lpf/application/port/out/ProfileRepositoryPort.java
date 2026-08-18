package com.lpf.application.port.out;

import com.lpf.domain.model.Profile;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepositoryPort {
    Optional<Profile> findById(UUID id);

    Profile save(Profile profile);
}
