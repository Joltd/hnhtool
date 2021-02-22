package com.evgenltd.hnhtool.harvester.core.record;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record PageData<T>(List<T> data, long total) {}
