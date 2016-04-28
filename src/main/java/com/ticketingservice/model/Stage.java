package com.ticketingservice.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class Stage implements Serializable {

    private static final long serialVersionUID = 1L;

    private int stageId;

    private String name;

    private Set<Level> levels;

    public int getStageId() {
        return stageId;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Level> getLevels() {
        if (levels == null) {
            this.levels = new TreeSet<>(Level.getComparatorbyLevelId());
        }
        return levels;
    }

    public void setLevels(Set<Level> levels) {
        this.levels = levels;
    }

    public Set<Level> getLevels(Optional<Integer> minLevel, Optional<Integer> maxLevel) {
        Set<Level> levels = this.getLevels();
       if (minLevel.isPresent() || maxLevel.isPresent()) {
            levels = new TreeSet<>(Level.getComparatorbyLevelId());
            for (Level level : this.getLevels()) {
                if ((minLevel.isPresent() && minLevel.get() <= level.getLevelId())
                        || (maxLevel.isPresent() && maxLevel.get() >= level.getLevelId())) {
                    levels.add(level);
                }
            }
        }
        return levels;
    }
}
