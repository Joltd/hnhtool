package com.evgenltd.hnhtool.harvester.core.component.matcher;

import java.util.ArrayList;
import java.util.List;

public final class MatchingResult<L, R> {
    private final List<L> leftNotMatched = new ArrayList<>();
    private final List<R> rightNotMatched = new ArrayList<>();
    private final List<MatchingEntry<L, R>> matches = new ArrayList<>();

    public List<L> getLeftNotMatched() {
        return leftNotMatched;
    }

    public List<R> getRightNotMatched() {
        return rightNotMatched;
    }

    public List<MatchingEntry<L, R>> getMatches() {
        return matches;
    }
}
