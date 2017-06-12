package org.pszt.evo.core.listeners;

import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Population;

public interface CompletionListener<T extends Gene<?, T>, C extends Comparable<? super C>> {
    void onFinish(final Population<T, C> population);
}
