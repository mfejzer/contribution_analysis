package pl.umk.wmii.msr.contributions.model;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;

/**
 * Transport object for ParallelTopicModel and its InstanceList
 */
public class TopicModel<T extends Classifiable> {
    private final ParallelTopicModel parallelTopicModel;
    private final InstanceList instanceList;

    public TopicModel(ParallelTopicModel parallelTopicModel, InstanceList instanceList) {
        this.parallelTopicModel = parallelTopicModel;
        this.instanceList = instanceList;
    }

    public ParallelTopicModel getParallelTopicModel() {
        return parallelTopicModel;
    }

    public InstanceList getInstanceList() {
        return instanceList;
    }
}
