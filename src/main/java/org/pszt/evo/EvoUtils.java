package org.pszt.evo;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class EvoUtils {
    // lowerBound - inclusive
    // upperBound - exclusive
    public Pair<Integer, Integer> generatePairOfUniqueIndices(final int lowerBound, final int upperBound) {
        final List<Integer> randomUniqueIntegers = generateUniqueIndices(lowerBound, upperBound, 2);
        return Pair.of(randomUniqueIntegers.get(0), randomUniqueIntegers.get(1));
    }

    public List<Integer> generateUniqueIndices(final int lowerBound, final int upperBound, final int n) {
        return ThreadLocalRandom.current()
                .ints(lowerBound, upperBound)
                .distinct()
                .limit(n)
                .boxed()
                .collect(Collectors.toList());
    }

    public int extractMin(final Pair<Integer, Integer> pair) {
        return Math.min(pair.getLeft(), pair.getRight());
    }

    public int extractMax(final Pair<Integer, Integer> pair) {
        return Math.max(pair.getLeft(), pair.getRight());
    }
}
