package pl.umk.wmii.msr.contributions.aggergator;

import com.google.common.base.Function;
import com.google.common.collect.*;
import org.springframework.stereotype.Service;
import pl.umk.wmii.msr.contributions.model.Classifiable;
import pl.umk.wmii.msr.contributions.model.Topic;

import java.util.*;

@Service("Aggregator")
public class AggregatorImpl implements Aggregator {

    @Override
    public <T extends Classifiable, Group> Map<Group, Map<Topic, Integer>> aggregate(List<T> aggregable,
                                                                                     Function<T, Group> grouper,
                                                                                     Map<T, Topic> topicMap) {

        ImmutableMap<T, Group> aggregableToGroups = Maps.toMap(aggregable,
                grouper);
        return aggregate(aggregable, aggregableToGroups, topicMap);
    }

    @Override
    public <T extends Classifiable, Group> Map<Group, Map<Topic, Integer>> aggregate(List<T> aggregable,
                                                                                     Map<T, Group> aggregableToGroups,
                                                                                     Map<T, Topic> topicMap) {
        Map<Group, Map<Topic, Integer>> topicsGroupedByS = new TreeMap<>();

        for (Group group : aggregableToGroups.values()) {
            Map<Topic, Integer> topicCounter = initializeTopicCounter(topicMap);
            topicsGroupedByS.put(group, topicCounter);
        }

        for (Map.Entry<T, Group> entry : aggregableToGroups.entrySet()) {
            Group group = entry.getValue();
            Topic topic = topicMap.get(entry.getKey());

            Map<Topic, Integer> topicCounter = null;
            if (topicsGroupedByS.containsKey(group)) {
                topicCounter = topicsGroupedByS.get(group);
                increaseCounterForTopic(topic, topicCounter);
            } else {
                topicCounter = new HashMap<>();
                topicCounter.put(topic, 1);
            }
            topicsGroupedByS.put(group, topicCounter);
        }

        return topicsGroupedByS;
    }

    private void increaseCounterForTopic(Topic topic, Map<Topic, Integer> topicCounter) {
        if (topicCounter.containsKey(topic)) {
            Integer counter = topicCounter.get(topic);
            counter++;
            topicCounter.put(topic, counter);
        } else {
            topicCounter.put(topic, 1);
        }
    }

    private <T extends Classifiable> Map<Topic, Integer> initializeTopicCounter(Map<T, Topic> topicMap) {
        Map<Topic, Integer> topicCounter = new HashMap<>();
        for (Topic topic : topicMap.values()) {
            topicCounter.put(topic, 0);
        }
        return topicCounter;
    }

    @Override
    public <T extends Classifiable, Group> Map<Group, Map<Topic, Integer>> aggregateWithoutMostSignificant(List<T> aggregable,
                                                                                                           Function<T, Group> grouper,
                                                                                                           Map<T, Topic> topicMap,
                                                                                                           int omittedNumber) {

        Map<Group, Map<Topic, Integer>> result =
                aggregate(aggregable, grouper, topicMap);

        Map<Topic, Integer> topicsBySignificance = initializeTopicCounter
                (topicMap);
        for (Map.Entry<T, Topic> entry : topicMap.entrySet()) {
            Topic topic = entry.getValue();
            increaseCounterForTopic(topic, topicsBySignificance);
        }
        ListMultimap<Integer, Topic> significanceToTopics =
                Multimaps.invertFrom(Multimaps.forMap(topicsBySignificance),
                        ArrayListMultimap.<Integer, Topic>create());
        Set<Topic> significantTopicsToBeRemoved =
                getSignificantTopics(significanceToTopics, omittedNumber);

        for (Topic significantTopic : significantTopicsToBeRemoved) {
            for (Map.Entry<Group, Map<Topic, Integer>> entry : result
                    .entrySet()) {
                entry.getValue().remove(significantTopic);
            }
        }

        return result;
    }

    private Set<Topic> getSignificantTopics(Multimap<Integer, Topic> significanceToTopics, int omittedNumber) {
        Set<Topic> significantTopicsToBeRemoved = new HashSet<>();

        TreeSet<Integer> ts = new TreeSet(significanceToTopics.keySet());
        Iterator<Integer> iterator = ts.descendingIterator();
        for (int i = 0; i < omittedNumber; i++) {
            if (iterator.hasNext()) {
                for (Topic topic : significanceToTopics.get(iterator.next())) {
                    significantTopicsToBeRemoved.add(topic);
                }
            }
        }
        return significantTopicsToBeRemoved;
    }
}
