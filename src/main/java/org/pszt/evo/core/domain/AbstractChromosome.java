package org.pszt.evo.core.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

@ToString
@RequiredArgsConstructor
public abstract class AbstractChromosome<T extends Gene<?, T>> implements Chromosome<T>, RandomAccess {

    @Getter
    protected transient List<T> genes;

    protected AbstractChromosome(@NonNull final List<? extends T> genes) {
        if (genes.isEmpty()) {
            throw new IllegalArgumentException("Genes sequence cannot be empty");
        }

        this.genes = (List<T>)genes;
    }

    @Override
    public T getGeneAt(int index) {
        return genes.get(index);
    }

    @Override
    public int length() {
        return genes.size();
    }

    @Override
    public Iterator<T> iterator() {
        return genes.iterator();
    }
}
