package com.evgenltd.hnhtool.harvester.common.service;

import com.evgenltd.hnhtool.harvester.Application;
import com.evgenltd.hnhtool.harvester.common.entity.KnownObject;
import com.evgenltd.hnhtool.harvester.common.entity.Resource;
import com.evgenltd.hnhtool.harvester.common.repository.KnownObjectRepository;
import com.evgenltd.hnhtool.harvester.common.repository.ResourceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 14-04-2019 18:03</p>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class OrmTest {

    @Autowired
    private KnownObjectRepository knownObjectRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Test
    public void testResourceLinkage() {

        final Resource resource = new Resource();
        resource.setId(1001L);
//        resource.setName("Test");
//        final Resource saved = resourceRepository.save(resource);

        final KnownObject knownObject = new KnownObject();
        knownObject.setResource(resource);

        knownObjectRepository.save(knownObject);

    }

}
