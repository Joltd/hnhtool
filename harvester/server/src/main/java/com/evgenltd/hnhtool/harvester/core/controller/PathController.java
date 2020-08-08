package com.evgenltd.hnhtool.harvester.core.controller;

import com.evgenltd.hnhtool.harvester.core.entity.Path;
import com.evgenltd.hnhtool.harvester.core.entity.Space;
import com.evgenltd.hnhtool.harvester.core.repository.PathRepository;
import com.evgenltd.hnhtool.harvester.core.repository.SpaceRepository;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/path")
public class PathController {

    private final PathRepository pathRepository;
    private final SpaceRepository spaceRepository;

    public PathController(
            final PathRepository pathRepository,
            final SpaceRepository spaceRepository
    ) {
        this.pathRepository = pathRepository;
        this.spaceRepository = spaceRepository;
    }

    @GetMapping
    public Response<List<PathRecord>> list(@RequestParam("space") final Long spaceId) {
        final List<PathRecord> result = pathRepository.findBySpaceId(spaceId)
                .stream()
                .map(this::toPathRecord)
                .collect(Collectors.toList());
        return new Response<>(result);
    }

    @PostMapping
    public Response<List<PathRecord>> update(@RequestParam("space") final Long spaceId, @RequestBody final List<PathRecord> pathRecords) {

        final Optional<Space> spaceHolder = spaceRepository.findById(spaceId);
        if (spaceHolder.isEmpty()) {
            return new Response<>("Space [%s] not found", spaceId);
        }

        final Space space = spaceHolder.get();

        final Set<Long> forDelete = pathRepository.findBySpaceId(spaceId)
                .stream()
                .map(Path::getId)
                .collect(Collectors.toSet());

        final List<PathRecord> saved = pathRecords.stream()
                .map(pathRecord -> toPath(pathRecord, space))
                .map(pathRepository::save)
                .peek(path -> forDelete.remove(path.getId()))
                .map(this::toPathRecord)
                .collect(Collectors.toList());

        for (final Long id : forDelete) {
            pathRepository.deleteById(id);
        }

        return new Response<>(saved);
    }

    private PathRecord toPathRecord(final Path path) {
        return new PathRecord(
                path.getId(),
                path.getFrom().getX(),
                path.getFrom().getY(),
                path.getTo().getX(),
                path.getTo().getY()
        );
    }

    private Path toPath(final PathRecord pathRecord, final Space space) {
        final Path path = new Path();
        path.setId(pathRecord.id());
        path.setSpace(space);
        path.setFrom(new IntPoint(pathRecord.fromX(), pathRecord.fromY()));
        path.setTo(new IntPoint(pathRecord.toX(), pathRecord.toY()));
        return path;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    record PathRecord(Long id, int fromX, int fromY, int toX, int toY) {}

}
