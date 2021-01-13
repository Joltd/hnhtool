package com.evgenltd.hnhtool.harvester.core.repository;

import com.evgenltd.hnhtool.harvester.core.entity.Account;
import com.evgenltd.hnhtools.common.ApplicationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    default Account findOne(Long id) {
        return findById(id).orElseThrow(() -> new ApplicationException("Account with id [%s] not found", id));
    }

    Optional<Account> findAccountByUsername(final String username);

}
