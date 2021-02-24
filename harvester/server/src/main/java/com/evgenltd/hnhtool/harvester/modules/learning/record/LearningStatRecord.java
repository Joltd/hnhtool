package com.evgenltd.hnhtool.harvester.modules.learning.record;

import com.evgenltd.hnhtool.harvester.modules.learning.entity.LearningStat;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.LocalDateTime;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record LearningStatRecord(
        Long id,
        LocalDateTime date,
        String agent,
        Long taskId,
        Integer learningPoints,
        Integer experiencePoints,
        Integer mentalWeights
) {

    public static LearningStatRecord of(final LearningStat learningStat) {
        return new LearningStatRecord(
                learningStat.getId(),
                learningStat.getDate(),
                learningStat.getLearning().getAgent().getName(),
                learningStat.getTask().getId(),
                learningStat.getLearningPoints(),
                learningStat.getExperiencePoints(),
                learningStat.getMentalWeights()
        );
    }

}
