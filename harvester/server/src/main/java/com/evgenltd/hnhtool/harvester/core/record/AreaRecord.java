package com.evgenltd.hnhtool.harvester.core.record;

import com.evgenltd.hnhtool.harvester.core.entity.Area;
import com.evgenltd.hnhtools.entity.IntPoint;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final record AreaRecord(
        Long id,
        String name,
        Long spaceId,
        IntPoint from,
        IntPoint to
) {

    public static AreaRecord of(final Area area) {
        return new AreaRecord(
                area.getId(),
                area.getName(),
                area.getSpace().getId(),
                area.getFrom(),
                area.getTo()
        );
    }

}
