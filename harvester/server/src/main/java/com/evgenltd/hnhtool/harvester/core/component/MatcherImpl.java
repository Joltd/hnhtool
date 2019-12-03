package com.evgenltd.hnhtool.harvester.core.component;

import com.evgenltd.hnhtools.entity.IntPoint;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p></p>
 * <br/>
 * <p>Project: hnhtool-root</p>
 * <p>Author:  lebed</p>
 * <p>Created: 04-12-2019 00:07</p>
 */
class MatcherImpl {

    <L, R, LW extends MatcherImpl.Wrapper<L>, RW extends MatcherImpl.Wrapper<R>> Result<L, R> match(
            final List<L> left,
            final List<R> right,
            final Function<L, LW> buildLeftWrapper,
            final Function<R, RW> buildRightWrapper
    ) {
        final List<LW> leftWrappers = left.stream()
                .map(buildLeftWrapper)
                .collect(Collectors.toList());
        final List<RW> rightWrappers = right.stream()
                .map(buildRightWrapper)
                .collect(Collectors.toList());

        final List<Entry<LW,RW>> resultEntries = match(leftWrappers, rightWrappers);
        final Result<L, R> result = new Result<>();

        for (final Entry<LW, RW> entry : resultEntries) {
            final LW leftWrapper = entry.getLeft();
            final RW rightWrapper = entry.getRight();

            if (leftWrapper != null && rightWrapper != null) {
                result.matches.add(new Entry<>(leftWrapper.getValue(), rightWrapper.getValue()));
                continue;
            }

            if (leftWrapper == null) {
                result.rightNotMatched.add(rightWrapper.getValue());
            } else {
                result.leftNotMatched.add(leftWrapper.getValue());
            }
        }

        return result;
    }

    private <L, R, LW extends MatcherImpl.Wrapper<L>, RW extends MatcherImpl.Wrapper<R>> List<Entry<LW, RW>> match(
            final List<LW> left,
            final List<RW> right
    ) {
        final Map<String, List<LW>> leftIndex = left.stream()
                .collect(Collectors.groupingBy(Wrapper::getKey, Collectors.toList()));
        final Map<String, List<RW>> rightIndex = right.stream()
                .collect(Collectors.groupingBy(Wrapper::getKey, Collectors.toList()));

        final List<Entry<LW,RW>> result = new ArrayList<>();

        leftIndex.forEach((key, leftVariants) -> {

            Collections.sort(leftVariants);
            final LW leftVariant = leftVariants.remove(0);
            leftVariants.forEach(l -> result.add(new Entry<>(l, null)));

            final List<RW> rightVariants = rightIndex.remove(key);
            if (rightVariants == null) {
                result.add(new Entry<>(leftVariant, null));
                return;
            }

            Collections.sort(rightVariants);
            final RW rightVariant = rightVariants.remove(0);
            rightVariants.forEach(r -> result.add(new Entry<>(null, r)));

            result.add(new Entry<>(leftVariant, rightVariant));

        });

        rightIndex.values()
                .stream()
                .flatMap(Collection::stream)
                .forEach(r -> result.add(new Entry<>(null, r)));

        return result;
    }

    public static final class Result<L,R> {
        private List<L> leftNotMatched = new ArrayList<>();
        private List<R> rightNotMatched = new ArrayList<>();
        private List<Entry<L,R>> matches = new ArrayList<>();

        public List<L> getLeftNotMatched() {
            return leftNotMatched;
        }

        public List<R> getRightNotMatched() {
            return rightNotMatched;
        }

        public List<Entry<L, R>> getMatches() {
            return matches;
        }
    }

    public static final class Entry<L, R> {
        private L left;
        private R right;

        Entry(final L left, final R right) {
            this.left = left;
            this.right = right;
        }

        public L getLeft() {
            return left;
        }

        public R getRight() {
            return right;
        }
    }

    static abstract class Wrapper<T> implements Comparable<Wrapper<T>> {
        private T value;

        Wrapper(final T value) {
            this.value = value;
        }

        T getValue() {
            return value;
        }

        abstract String getResource();

        abstract IntPoint getPosition();

        String getKey() {
            return getResource() + " " + getPosition();
        }

        @Override
        public int compareTo(final MatcherImpl.Wrapper<T> o) {
            return 0;
        }
    }


}
