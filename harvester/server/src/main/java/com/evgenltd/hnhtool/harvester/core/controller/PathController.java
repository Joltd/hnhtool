package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.entity.Path;
import com.evgenltd.hnhtool.harvester.core.repository.PathRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 09-05-2020 20:02</p>
 */
@RestController
@RequestMapping("/path")
public class PathController {

    private final PathRepository pathRepository;

    public PathController(final PathRepository pathRepository) {
        this.pathRepository = pathRepository;
    }

    @GetMapping
    public List<Path> list() {
        return pathRepository.findAll();
    }

    @PostMapping
    public void update(@RequestBody final List<Path> paths) {
        final Set<Long> existed = pathRepository.findAll()
                .stream()
                .map(Path::getId)
                .collect(Collectors.toSet());

        for (final Path path : paths) {
            pathRepository.save(path);
            existed.remove(path.getId());
        }

        for (final Long id : existed) {
            pathRepository.deleteById(id);
        }
    }

}
