package com.evgenltd.hnhtool.harvester.modules.learning.record;

import com.evgenltd.hnhtool.harvester.core.record.JobRecord;
import com.evgenltd.hnhtool.harvester.modules.learning.entity.Learning;

public class LearningRecord extends JobRecord {

    private Long agent;
    private Long area;

    public Long getAgent() {
        return agent;
    }
    public void setAgent(final Long agent) {
        this.agent = agent;
    }

    public Long getArea() {
        return area;
    }
    public void setArea(final Long area) {
        this.area = area;
    }

    public static LearningRecord of(final Learning learning) {
        final LearningRecord learningRecord = new LearningRecord();
        JobRecord.fillRecord(learning, learningRecord);
        learningRecord.setAgent(learning.getAgent().getId());
        learningRecord.setArea(learning.getArea().getId());
        return learningRecord;
    }

}
