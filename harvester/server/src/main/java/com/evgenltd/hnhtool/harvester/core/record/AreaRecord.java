package com.evgenltd.hnhtool.harvester.core.record;

import com.evgenltd.hnhtools.entity.IntPoint;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final record AreaRecord(Long id, Long spaceId, IntPoint from, IntPoint to) {
}
