package com.evgenltd.hnhtool.harvester.core.service;

import com.evgenltd.hnhtool.harvester.core.entity.Resource;
import com.evgenltd.hnhtool.harvester.core.repository.ResourceRepository;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-12-2019 23:11</p>
 */
@Service
@Transactional
public class ResourceService {

    @Value("${hafen.resource.host}")
    private String hafenResourceHost;

    private ResourceRepository resourceRepository;
    private RestTemplate restTemplate;

    public ResourceService(final ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
//        final HttpComponentsClientHttpRequestFactory requestFactory = new ResourceLoader().buildRequestFactory();
//        this.restTemplate = new RestTemplate(requestFactory);
    }

    public Resource findByName(final String name) {
        return resourceRepository.findByName(name)
                .orElseGet(() -> storeUnknownResource(name));
    }

    public <T> Map<String, Resource> loadResourceIndexByNames(final List<T> visuals, final Function<T,String> resourceGetter) {

        final Set<String> resources = visuals.stream()
                .map(resourceGetter)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        final Map<String, Resource> index = resourceRepository.findAllByNameIn(resources)
                .stream()
                .collect(Collectors.toMap(Resource::getName, resource -> resource));

        for (final String resource : resources) {
            if (index.containsKey(resource)) {
                continue;
            }

            final Resource newResource = storeUnknownResource(resource);
            index.put(resource, newResource);
        }

        return index;

    }

    public List<String> loadResourceOfGroup(final String name) {
        final Resource resource = findByName(name);
        if (resource.getGroup() == null) {
            return Collections.singletonList(name);
        }

        return resource.getGroup()
                .getResources()
                .stream()
                .map(Resource::getName)
                .collect(Collectors.toList());
    }

    private Resource storeUnknownResource(final String name) {
        final Resource resource = new Resource();
        resource.setName(name);
        resource.setUnknown(true);
//        final byte[] content = loadResourceContent(name);
//        if (content != null) {
//            final ResourceContent resourceContent = new ResourceContent();
//            resourceContent.setData(content);
//            resource.setContent(resourceContent);
//        }
        return resourceRepository.save(resource);
    }

    @Nullable
    private byte[] loadResourceContent(final String name) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        final HttpEntity<String> entity = new HttpEntity<>(headers);
        final ResponseEntity<byte[]> response = restTemplate.exchange(
                hafenResourceHost + name + ".res",
                HttpMethod.GET,
                entity,
                byte[].class
        );
        if (Objects.equals(response.getStatusCode(), HttpStatus.OK)) {
            return response.getBody();
        }

        return null;
    }

}