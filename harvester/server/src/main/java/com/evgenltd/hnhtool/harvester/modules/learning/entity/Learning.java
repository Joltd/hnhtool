package com.evgenltd.hnhtool.harvester.modules.learning.entity;

import com.evgenltd.hnhtool.harvester.core.entity.Agent;
import com.evgenltd.hnhtool.harvester.core.entity.Area;
import com.evgenltd.hnhtool.harvester.core.entity.Job;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "learnings")
public class Learning extends Job {

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;

    @ManyToOne
    @JoinColumn(name = "area_id")
    private Area area;

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
