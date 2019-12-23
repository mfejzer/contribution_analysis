package pl.umk.wmii.msr.contributions.extractor;

import pl.umk.wmii.msr.contributions.model.Classifiable;
import pl.umk.wmii.msr.contributions.model.Topic;
import pl.umk.wmii.msr.contributions.model.TopicModel;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Simple document topic extractor interface
 */
public interface TopicExtractor {
    /**
     * Extracts list of all topics
     *
     * @param inputResource location to resource holding input data, it might be
     *                      for example path to directory with documents
     * @param params        algorithm parameters
     * @return List of all topics. Each topic has an index and a list of top
     * words describing it.
     * @see TopicExtractorParameters
     */
    List<List<String>> extractTopicsFromURL(URL inputResource,
                                            TopicExtractorParameters params);

    /**
     * Extracts topic model from URL resource
     *
     * @param inputResource location to resource holding input data, it might be
     *                      for example path to directory with documents
     * @param params        algorithm parameters
     * @param <T>           type of entity
     * @return TopicModel for classification
     * @see TopicExtractorParameters
     */
    <T extends Classifiable> TopicModel<T> extractTopicsModelFromURL(
            URL inputResource, TopicExtractorParameters params);

    /**
     * Method trains model on list of Classifiable entities
     *
     * @param entities training set
     * @param params   for model
     * @param <T>      type of training entity
     * @return TopicModel for classification
     */
    <T extends Classifiable> TopicModel<T> trainTopicModel(
            List<T> entities, TopicExtractorParameters params);

    /**
     * Method trains model on list of Classifiable entities,
     * then creates map from each entity to its topic
     *
     * @param entities training set
     * @param params   for model
     * @param <T>      type of training entity
     * @return map entity to its topic
     */
    <T extends Classifiable> Map<T, Topic> trainAndGenerateEntitiesToTopicsMap(
            List<T> entities, TopicExtractorParameters params);

    /**
     * Method uses model to create map from each entity to its topic
     *
     * @param entities   classified set
     * @param topicModel trained model for classification
     * @param <T>        type of classified entity
     * @return map entity to its topic
     */
    <T extends Classifiable> Map<T, Topic> generateEntitiesToTopicsMap(
            List<T> entities, TopicModel<T> topicModel);

    /**
     * Extractor parameters
     */
    class TopicExtractorParameters {
        private final double alpha;
        private final double beta;
        private final int iterations;
        private final int maxTopics;
        private final int threads;

        public TopicExtractorParameters(int maxTopics, int iterations,
                                        double alpha, double beta, int threads) {
            super();
            this.maxTopics = maxTopics;
            this.iterations = iterations;
            this.alpha = alpha;
            this.beta = beta;
            this.threads = threads;
        }

        public double getAlpha() {
            return alpha;
        }

        public double getBeta() {
            return beta;
        }

        public int getIterations() {
            return iterations;
        }

        public int getMaxTopics() {
            return maxTopics;
        }

        public int getThreads() {
            return threads;
        }

    }
}
