package org.pszt.evo.core.listeners;

import com.opencsv.CSVWriter;
import lombok.Setter;
import org.pszt.evo.core.domain.Gene;
import org.pszt.evo.core.domain.Phenotype;
import org.pszt.evo.core.domain.Population;

public class CsvWriterEvolutionListner<T extends Gene<?, T>, C extends Comparable<? super C>> implements EvolutionListener<T, C> {

    @Setter
    private CSVWriter csvWriter;

    @Override
    public void onBeforeEvolution(int iteration, Population<T, C> population) {

    }

    @Override
    public void onAfterEvolution(int iteration, Population<T, C> population) {
        final Phenotype<T, C> fittest = population.getFittest();

        csvWriter.writeNext(new String[] {String.valueOf(iteration), fittest.getFitness().toString(), fittest.getChromosome().toString()});
    }
}
