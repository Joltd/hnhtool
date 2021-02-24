package com.evgenltd.hnhtool.harvester.core.record;

import com.evgenltd.hnhtool.harvester.core.entity.Job;

public class JobRecord {

    private Long id;
    private String name;
    private String type;
    private boolean enabled;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }
    public void setType(final String type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public static JobRecord of(final Job job) {
        return fillRecord(job, new JobRecord());
    }

    public static JobRecord fillRecord(final Job job, final JobRecord jobRecord) {
        jobRecord.setId(job.getId());
        jobRecord.setName(job.getName());
        jobRecord.setType(job.getType());
        jobRecord.setEnabled(job.isEnabled());
        return jobRecord;
    }

}
