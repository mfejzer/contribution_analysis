package pl.umk.wmii.msr.contributions.correlation;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.umk.wmii.msr.contributions.extractor.TopicExtractor;
import pl.umk.wmii.msr.contributions.model.Classifiable;
import pl.umk.wmii.msr.contributions.model.CorrelationPair;
import pl.umk.wmii.msr.contributions.model.Topic;
import pl.umk.wmii.msr.contributions.model.TopicCorrelation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Computes map of correlations, should be generic to compute correlations
 * between every Classifiable
 */
@Service("discoverer")
public class DiscovererImpl implements Discoverer {
    private TopicExtractor topicExtractor;

    @Override
    public <T extends Classifiable, S extends Classifiable> Map<TopicCorrelation, Integer> calculateCorrelationBetweenTopics(
            List<T> entitiesWithCorrelation,
            List<S> entitiesCorrelated,
            Function<T, Iterable<CorrelationPair<T, S>>> correlationFinderFunction,
            TopicExtractor.TopicExtractorParameters topicExtractorParameters) {

        Map<T, Topic> tToTopics = topicExtractor
                .trainAndGenerateEntitiesToTopicsMap(entitiesWithCorrelation,
                        topicExtractorParameters);
        Map<S, Topic> sToTopics = topicExtractor
                .trainAndGenerateEntitiesToTopicsMap(entitiesCorrelated,
                        topicExtractorParameters);

        List<CorrelationPair<T, S>> commitIssueCorrelationList = FluentIterable
                .from(entitiesWithCorrelation)
                .transformAndConcat(correlationFinderFunction).toList();

        Map<TopicCorrelation, Integer> topicCorrelationCounter = new HashMap<>();
        for (CorrelationPair<T, S> correlation : commitIssueCorrelationList) {
            T t = correlation.get_1();
            S s = correlation.get_2();

            if (tToTopics.containsKey(t) && sToTopics.containsKey(s)) {
                Topic commitTopic = tToTopics.get(t);
                Topic issueTopic = sToTopics.get(s);
                TopicCorrelation topicCorrelation = new TopicCorrelation(
                        commitTopic, issueTopic);

                if (topicCorrelationCounter.containsKey(topicCorrelation)) {
                    Integer counter = topicCorrelationCounter
                            .get(topicCorrelation);
                    counter++;
                    topicCorrelationCounter.put(topicCorrelation, counter);
                } else {
                    topicCorrelationCounter.put(topicCorrelation, 1);
                }
            }
        }

        return topicCorrelationCounter;
    }

    @Autowired
    public void setTopicExtractor(TopicExtractor topicExtractor) {
        this.topicExtractor = topicExtractor;
    }
}
