package com.evgenltd.hnhtool.harvester.security.service;

import com.evgenltd.hnhtool.harvester.security.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);

}
