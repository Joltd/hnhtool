package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.Preferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreferencesRepository extends JpaRepository<Preferences, Long> {

    default Optional<Preferences> find() {
        return findAll()
                .stream()
                .findFirst();
    }

}
