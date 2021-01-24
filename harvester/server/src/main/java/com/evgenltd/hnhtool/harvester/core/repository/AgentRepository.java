package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.Agent;
import com.evgenltd.hnhtools.common.ApplicationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

    default Agent findOne(Long id) {
        return findById(id).orElseThrow(() -> new ApplicationException("Agent with id [%s] not found", id));
    }

    Optional<Agent> findAccountByUsername(final String username);

}
