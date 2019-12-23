package pl.umk.wmii.msr.contributions.extractor;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.FileIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;
import pl.umk.wmii.msr.contributions.classifier.GenericClassifier;
import pl.umk.wmii.msr.contributions.model.Classifiable;
import pl.umk.wmii.msr.contributions.model.Topic;
import pl.umk.wmii.msr.contributions.model.TopicModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service("mallet")
public class MalletTopicExtractor implements TopicExtractor {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(MalletTopicExtractor.class);

    private Converter<TopicModel<? extends Classifiable>, List<List<String>>> converter;
    private InstanceTransformer instanceTransformer;

    @Override
    public List<List<String>> extractTopicsFromURL(URL inputResource,
                                                   TopicExtractorParameters params) {
        try {
            return buildModel(
                    new File(inputResource.toURI()).toPath(),
                    params);
        } catch (URISyntaxException | IOException e) {
            LOGGER.error(e.toString(), e);
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public <T extends Classifiable> TopicModel<T> extractTopicsModelFromURL(
            URL inputResource, TopicExtractorParameters params) {
        try {
            InstanceList instances = buildDirectoryInstanceList(new File(
                    inputResource.toURI()).toPath().toFile());
            ParallelTopicModel model = prepareModel(params);
            model.addInstances(instances);
            model.estimate();
            return new TopicModel<>(model, instances);
        } catch (URISyntaxException | IOException e) {
            LOGGER.error(e.toString(), e);
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public <T extends Classifiable> TopicModel<T> trainTopicModel(
            List<T> entities, TopicExtractorParameters params) {
        InstanceList instanceList = instanceTransformer
                .prepareInstanceList(entities);
        ParallelTopicModel model = prepareModel(params);
        model.addInstances(instanceList);
        try {
            model.estimate();
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        TopicModel<T> topicModel = new TopicModel<>(model, instanceList);
        return topicModel;
    }

    @Override
    public <T extends Classifiable> Map<T, Topic> trainAndGenerateEntitiesToTopicsMap(
            List<T> entities, TopicExtractorParameters params) {
        TopicModel<T> topicModel = this.trainTopicModel(entities, params);
        return generateEntitiesToTopicsMap(entities, topicModel);
    }

    @Override
    public <T extends Classifiable> Map<T, Topic> generateEntitiesToTopicsMap(List<T> entities, TopicModel<T> topicModel) {
        GenericClassifier<T> classifier = new GenericClassifier<>(topicModel, instanceTransformer);

        Map<T, Topic> entitiesToTopics = new HashMap<>();
        for (T entity : entities) {
            entitiesToTopics.put(entity, classifier.apply(entity));
        }
        return entitiesToTopics;
    }

    @Autowired
    public void setConverter(
            Converter<TopicModel<? extends Classifiable>, List<List<String>>> converter) {
        this.converter = converter;
    }

    @Autowired
    public void setInstanceTransformer(
            InstanceTransformer instanceTransformer) {
        this.instanceTransformer = instanceTransformer;
    }

    private InstanceList buildDirectoryInstanceList(File dir) {
        InstanceList instanceList = new InstanceList(buildPipe());
        instanceList.addThruPipe(new FileIterator(dir, new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".txt");
            }
        }));
        return instanceList;
    }

    private List<List<String>> buildModel(Path dir,
                                          TopicExtractorParameters params) throws IOException {
        InstanceList instances = buildDirectoryInstanceList(dir
                .toFile());
        ParallelTopicModel model = prepareModel(params);
        model.addInstances(instances);
        model.estimate();

        List<List<String>> result = converter.convert(new TopicModel<>(
                model, instances));

        return result;
    }

    private Pipe buildPipe() {
        List<Pipe> pipes = new ArrayList<>();
        pipes.add(new Input2CharSequence("UTF-8"));
        pipes.add(new CharSequence2TokenSequence(Pattern
                .compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
        pipes.add(new TokenSequenceLowercase());
        // pipes.add(new CharSequenceLowercase());
        // pipes.add(new CharSequence2TokenSequence(Pattern
        // .compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
        pipes.add(new TokenSequenceRemoveStopwords(false, false));
        pipes.add(new TokenSequence2FeatureSequence());

        return new SerialPipes(pipes);
    }

    private ParallelTopicModel prepareModel(TopicExtractorParameters params) {
        ParallelTopicModel model = new ParallelTopicModel(
                params.getMaxTopics(), params.getAlpha(),
                params.getBeta());
        model.setNumIterations(params.getIterations());
        model.setNumThreads(params.getThreads());
        return model;
    }
}
