package pl.umk.wmii.msr.contributions.aggergator;

import com.google.common.base.Function;
import pl.umk.wmii.msr.contributions.model.Classifiable;
import pl.umk.wmii.msr.contributions.model.Topic;

import java.util.List;
import java.util.Map;

/**
 * Aggregates lists by grouper function or map
 */
public interface Aggregator {

    /**
     * Aggregates provided list via grouper function
     *
     * @param aggregable entities to aggregate
     * @param grouper    function from aggregable to its group
     * @param topicMap   map from aggregable to its topics
     * @param <T>
     * @param <Group>
     * @return Map which keys are groups and values maps from topics to
     * topics counters in group
     */
    <T extends Classifiable, Group> Map<Group,
            Map<Topic, Integer>> aggregate(List<T> aggregable,
                                           Function<T, Group> grouper,
                                           Map<T, Topic> topicMap);

    /**
     * Aggregates provided list via aggregableToGroups map
     *
     * @param aggregable         entities to aggregate
     * @param aggregableToGroups map from aggregable to its group
     * @param topicMap           map from aggregable to its topics
     * @param <T>
     * @param <Group>
     * @return Map which keys are groups and values maps from topics to
     * topics counters in group
     */
    <T extends Classifiable, Group> Map<Group,
            Map<Topic, Integer>> aggregate(List<T> aggregable,
                                           Map<T, Group> aggregableToGroups,
                                           Map<T, Topic> topicMap);

    /**
     * Invokes aggregate, then removes most significant topics (most
     * frequently occurring in all groups).
     *
     * @param aggregable entities to aggregate
     * @param grouper    function from aggregable to its group
     * @param topicMap   map from aggregable to its topics
     * @param omitted    number of Topics to remove
     * @param <T>
     * @param <Group>
     * @return Map which keys are groups and values maps from topics to
     * topics counters in group
     */
    <T extends Classifiable, Group> Map<Group, Map<Topic, Integer>>
    aggregateWithoutMostSignificant(List<T> aggregable,
                                    Function<T, Group> grouper,
                                    Map<T, Topic> topicMap,
                                    int omitted);
}
