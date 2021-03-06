package org.pszt.tsp.domain;

import lombok.*;
import org.pszt.evo.core.domain.Gene;

@EqualsAndHashCode
@ToString
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class City implements Gene<Integer, City> {
    private final Integer city;

    @Override
    public Integer getValue() {
        return city;
    }

    @Override
    public City newInstance(@NonNull final Integer value) {
        return new City(value);
    }

    @Override
    public City copy() {
        return newInstance(city);
    }

    public static City of(@NonNull final Integer value) {
        return new City(value);
    }
}
