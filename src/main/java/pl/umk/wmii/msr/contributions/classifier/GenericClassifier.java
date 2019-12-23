package pl.umk.wmii.msr.contributions.classifier;


import cc.mallet.pipe.Pipe;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;
import pl.umk.wmii.msr.contributions.extractor.InstanceTransformer;
import pl.umk.wmii.msr.contributions.model.Classifiable;
import pl.umk.wmii.msr.contributions.model.Topic;
import pl.umk.wmii.msr.contributions.model.TopicModel;
import pl.umk.wmii.msr.contributions.model.TopicWordFrequency;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Classifier enables classification of instances of T one by one, needs topic model and instance transformer
 *
 * @param <T> class of entities to be classified
 */
public class GenericClassifier<T extends Classifiable> implements Classifier<T> {

    public GenericClassifier(TopicModel<T> topicModel, InstanceTransformer instanceTransformer) {
        this.instanceTransformer = instanceTransformer;
        this.model = topicModel.getParallelTopicModel();
        this.inferencer = model.getInferencer();
        this.instanceTransformationPipe = topicModel.getInstanceList().getPipe();
        this.dataAlphabet = topicModel.getInstanceList().getDataAlphabet();
        topics = prepareTopicsList();
    }

    private Alphabet dataAlphabet;

    private ParallelTopicModel model;

    private TopicInferencer inferencer;

    private Pipe instanceTransformationPipe;

    private List<Topic> topics;

    private InstanceTransformer instanceTransformer;

    private List<Topic> prepareTopicsList() {
        ArrayList<Topic> result = new ArrayList<Topic>();
        for (int topic = 0; topic < model.getNumTopics(); topic++) {
            Iterator<IDSorter> iterator = model.getSortedWords().get(topic).iterator();
            List<TopicWordFrequency> currentTopicWordFrequencies = new ArrayList<TopicWordFrequency>();
            while (iterator.hasNext()) {
                IDSorter idCountPair = iterator.next();
                currentTopicWordFrequencies.add(new TopicWordFrequency(idCountPair.getWeight(),
                        dataAlphabet.lookupObject(idCountPair.getID())));
            }
            result.add(new Topic(currentTopicWordFrequencies));
        }
        return result;
    }

    @Override
    public Topic apply(T entity) {
        InstanceList testing = new InstanceList(instanceTransformationPipe);
        testing.addThruPipe(instanceTransformer.prepareInstance(entity));

        double[] topicProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
        int maxProbabilityIndex = 0;
        for (int i = 1; i < topicProbabilities.length; i++) {
            if (topicProbabilities[i] > topicProbabilities[maxProbabilityIndex]) {
                maxProbabilityIndex = i;
            }
        }
        return topics.get(maxProbabilityIndex);
    }
}
