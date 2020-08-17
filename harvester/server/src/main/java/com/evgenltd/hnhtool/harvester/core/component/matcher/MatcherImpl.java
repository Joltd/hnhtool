package com.evgenltd.hnhtool.harvester.core.component.matcher;

import com.evgenltd.hnhtools.entity.IntPoint;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

final class MatcherImpl {

    private final List<Matcher.Flag> flags = new ArrayList<>();

    <L, R, LW extends MatcherImpl.Wrapper<L>, RW extends MatcherImpl.Wrapper<R>> MatchingResult<L, R> match(
            final List<L> left,
            final List<R> right,
            final Function<L, LW> buildLeftWrapper,
            final Function<R, RW> buildRightWrapper,
            final Matcher.Flag... flags
    ) {
        Collections.addAll(this.flags, flags);

        final List<LW> leftWrappers = left.stream()
                .map(buildLeftWrapper)
                .collect(Collectors.toList());
        final List<RW> rightWrappers = right.stream()
                .map(buildRightWrapper)
                .collect(Collectors.toList());

        final List<MatchingEntry<LW,RW>> resultEntries = match(leftWrappers, rightWrappers);
        final MatchingResult<L, R> result = new MatchingResult<>();

        for (final MatchingEntry<LW, RW> entry : resultEntries) {
            final LW leftWrapper = entry.getLeft();
            final RW rightWrapper = entry.getRight();

            if (leftWrapper != null && rightWrapper != null) {
                result.getMatches().add(new MatchingEntry<>(leftWrapper.getValue(), rightWrapper.getValue()));
                continue;
            }

            if (leftWrapper == null) {
                result.getRightNotMatched().add(rightWrapper.getValue());
            } else {
                result.getLeftNotMatched().add(leftWrapper.getValue());
            }
        }

        return result;
    }

    private <L, R, LW extends MatcherImpl.Wrapper<L>, RW extends MatcherImpl.Wrapper<R>> List<MatchingEntry<LW, RW>> match(
            final List<LW> left,
            final List<RW> right
    ) {
        final Map<String, List<LW>> leftIndex = left.stream()
                .collect(Collectors.groupingBy(this::getKey, Collectors.toList()));
        final Map<String, List<RW>> rightIndex = right.stream()
                .collect(Collectors.groupingBy(this::getKey, Collectors.toList()));

        final List<MatchingEntry<LW,RW>> result = new ArrayList<>();

        leftIndex.forEach((key, leftVariants) -> {

            Collections.sort(leftVariants);
            final LW leftVariant = leftVariants.remove(0);
            leftVariants.forEach(l -> result.add(new MatchingEntry<>(l, null)));

            final List<RW> rightVariants = rightIndex.remove(key);
            if (rightVariants == null) {
                result.add(new MatchingEntry<>(leftVariant, null));
                return;
            }

            Collections.sort(rightVariants);
            final RW rightVariant = rightVariants.remove(0);
            rightVariants.forEach(r -> result.add(new MatchingEntry<>(null, r)));

            result.add(new MatchingEntry<>(leftVariant, rightVariant));

        });

        rightIndex.values()
                .stream()
                .flatMap(Collection::stream)
                .forEach(r -> result.add(new MatchingEntry<>(null, r)));

        return result;
    }

    private <T> String getKey(final Wrapper<T> wrapper) {
        final StringBuilder sb = new StringBuilder();
        sb.append(wrapper.getResource());
        if (!this.flags.contains(Matcher.Flag.SKIP_POSITION)) {
            sb.append(" ").append(wrapper.getPosition());
        }
        return sb.toString();
    }

    static abstract class Wrapper<T> implements Comparable<Wrapper<T>> {
        private final T value;

        Wrapper(final T value) {
            this.value = value;
        }

        T getValue() {
            return value;
        }

        abstract String getResource();

        abstract IntPoint getPosition();

        @Override
        public int compareTo(@NotNull final MatcherImpl.Wrapper<T> o) {
            return 0;
        }
    }

}
