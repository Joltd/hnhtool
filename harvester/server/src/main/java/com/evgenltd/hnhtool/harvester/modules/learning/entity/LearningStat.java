package com.evgenltd.hnhtool.harvester.modules.learning.entity;

import com.evgenltd.hnhtool.harvester.core.entity.Task;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_stats")
public class LearningStat {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "learning_id")
    private Learning learning;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    private Integer learningPoints;

    private Integer experiencePoints;

    private Integer mentalWeights;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(final LocalDateTime date) {
        this.date = date;
    }

    public Learning getLearning() {
        return learning;
    }

    public void setLearning(final Learning learning) {
        this.learning = learning;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(final Task task) {
        this.task = task;
    }

    public Integer getLearningPoints() {
        return learningPoints;
    }

    public void setLearningPoints(final Integer learningPoints) {
        this.learningPoints = learningPoints;
    }

    public Integer getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(final Integer experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    public Integer getMentalWeights() {
        return mentalWeights;
    }

    public void setMentalWeights(final Integer mentalWeights) {
        this.mentalWeights = mentalWeights;
    }
}
