package com.evgenltd.hnhtool.harvester.security.service;

import com.evgenltd.hnhtool.harvester.security.entity.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Project: hnhtool-root
 * Author:  Lebedev
 * Created: 26-03-2019 14:50
 */
public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);

}
