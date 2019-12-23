package pl.umk.wmii.msr.contributions.classifier;

import pl.umk.wmii.msr.contributions.extractor.InstanceTransformer;
import pl.umk.wmii.msr.contributions.model.Commit;
import pl.umk.wmii.msr.contributions.model.TopicModel;

/**
 * Enables classification on Commits
 */
public class CommitClassifier extends GenericClassifier<Commit> {

    public CommitClassifier(TopicModel<Commit> topicModel, InstanceTransformer instanceTransformer) {
        super(topicModel, instanceTransformer);
    }
}
