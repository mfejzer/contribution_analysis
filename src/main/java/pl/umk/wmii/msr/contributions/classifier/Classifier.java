package pl.umk.wmii.msr.contributions.classifier;

import pl.umk.wmii.msr.contributions.model.Classifiable;
import pl.umk.wmii.msr.contributions.model.Topic;

/**
 * Implementations should be able to classify entity and return its topic
 *
 * @param <T> type of entity to classify
 */
public interface Classifier<T extends Classifiable> {

    /**
     * Implementation should return most probable topic for text obtained from entity
     *
     * @param entity with text
     * @return Topic, with word frequencies
     */
    Topic apply(T entity);
}
