package pl.umk.wmii.msr.contributions.extractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import pl.umk.wmii.msr.contributions.model.Classifiable;
import pl.umk.wmii.msr.contributions.model.TopicModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple topic model to topic list converter
 */
@Service("topicModelToTopicListConverter")
public class TopicModelToTopicListConverter<T extends Classifiable> implements Converter<TopicModel<T>, List<List<String>>> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(TopicModelToTopicListConverter.class);
    private static final int TOP_WORDS = 10;

    /* (non-Javadoc)
     * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
     */
    @Override
    public List<List<String>> convert(TopicModel<T> source) {
        Object[][] topicsObjects = source.getParallelTopicModel()
                .getTopWords(TOP_WORDS);

        List<List<String>> result = new ArrayList<>();

        for (int i = 0; i < topicsObjects.length; i++) {
            List<String> wordsList = new ArrayList<>();
            try {
                for (Object wordObject : topicsObjects[i]) {
                    wordsList.add((String) wordObject);
                }
            } catch (IndexOutOfBoundsException ex) {
                LOGGER.error(ex.toString(), ex);
            }
            result.add(wordsList);
        }

        return result;
    }

}
