package pl.umk.wmii.msr.contributions.correlation;

import com.google.common.base.Function;
import pl.umk.wmii.msr.contributions.extractor.TopicExtractor;
import pl.umk.wmii.msr.contributions.model.Classifiable;
import pl.umk.wmii.msr.contributions.model.CorrelationPair;
import pl.umk.wmii.msr.contributions.model.TopicCorrelation;

import java.util.List;
import java.util.Map;

/**
 * Implementations should be able to calculate correlations between topics
 */
public interface Discoverer {
    /**
     * Calculates correlations between two lists of Classifiable entities,
     * entities of type T must have extractable correlation to entities of type S
     *
     * @param entitiesWithCorrelation
     * @param entitiesCorrelated
     * @param correlationFinderFunction
     * @param topicExtractorParameters  parameters to train topic extractor
     * @param <T>
     * @param <S>
     * @return map containing correlations between topics as keys and number of those correlations as values
     */
    <T extends Classifiable, S extends Classifiable> Map<TopicCorrelation, Integer> calculateCorrelationBetweenTopics(
            List<T> entitiesWithCorrelation,
            List<S> entitiesCorrelated,
            Function<T, Iterable<CorrelationPair<T, S>>> correlationFinderFunction,
            TopicExtractor.TopicExtractorParameters topicExtractorParameters);
}
