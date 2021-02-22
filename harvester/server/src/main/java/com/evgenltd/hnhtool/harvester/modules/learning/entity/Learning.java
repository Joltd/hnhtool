package com.evgenltd.hnhtool.harvester.modules.learning.entity;

import com.evgenltd.hnhtool.harvester.core.entity.Agent;
import com.evgenltd.hnhtool.harvester.core.entity.Area;
import com.evgenltd.hnhtool.harvester.core.entity.Job;

import javax.persistence.*;

@Entity
@Table(name = "learnings")
public class Learning {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;

    @ManyToOne
    @JoinColumn(name = "area_id")
    private Area area;

    public Long getId() {
        return id;
    }
    public void setId(final Long id) {
        this.id = id;
    }

    public Job getJob() {
        return job;
    }
    public void setJob(final Job job) {
        this.job = job;
    }

    public Agent getAgent() {
        return agent;
    }
    public void setAgent(final Agent agent) {
        this.agent = agent;
    }

    public Area getArea() {
        return area;
    }
    public void setArea(final Area area) {
        this.area = area;
    }

}
