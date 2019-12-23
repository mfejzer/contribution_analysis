package pl.umk.wmii.msr.contributions.classifier;

import pl.umk.wmii.msr.contributions.extractor.InstanceTransformer;
import pl.umk.wmii.msr.contributions.model.Issue;
import pl.umk.wmii.msr.contributions.model.TopicModel;

/**
 * Enables classification on Issues
 */
public class IssueClassifier extends GenericClassifier<Issue> {

    public IssueClassifier(TopicModel<Issue> topicModel, InstanceTransformer instanceTransformer) {
        super(topicModel, instanceTransformer);
    }
}
