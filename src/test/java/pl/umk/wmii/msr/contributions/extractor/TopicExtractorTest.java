package pl.umk.wmii.msr.contributions.extractor;

import pl.umk.wmii.msr.contributions.config.AppConfig;
import pl.umk.wmii.msr.contributions.model.Classifiable;
import pl.umk.wmii.msr.contributions.model.TopicModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class TopicExtractorTest {
    private static final boolean PRINT = true;
    private TopicExtractor topicExtractor;

    @Autowired
    public void setTopicExtractor(TopicExtractor topicExtractor) {
        this.topicExtractor = topicExtractor;
    }

    @Test
    public void testExtractTopics() throws MalformedURLException {
        int topicsNo = 50;
        int iterations = 100;
        double alpha = 50;
        double beta = 0.01;
        int threadsNo = 8;
        TopicExtractor.TopicExtractorParameters params = new TopicExtractor.TopicExtractorParameters(
                topicsNo, iterations, alpha, beta, threadsNo);
        List<List<String>> topics = topicExtractor
                .extractTopicsFromURL(
                        FileSystems.getDefault()
                                .getPath("src", "test", "example")
                                .toUri().toURL(), params);
        assertNotNull(topics);
        assertFalse(topics.isEmpty());
        assertFalse(topics.get(0).isEmpty());
        assertTrue(topics.size() <= topicsNo);
        if (PRINT) {
            printTopics(topics);
        }
    }

    @Test
    public void testExtractTopicsModelFromURL()
            throws MalformedURLException {
        int topicsNo = 50;
        int iterations = 100;
        double alpha = 50;
        double beta = 0.01;
        int threadsNo = 8;
        TopicExtractor.TopicExtractorParameters params = new TopicExtractor.TopicExtractorParameters(
                topicsNo, iterations, alpha, beta, threadsNo);
        TopicModel<Classifiable> topics = topicExtractor
                .extractTopicsModelFromURL(FileSystems.getDefault()
                        .getPath("src", "test", "example").toUri()
                        .toURL(), params);
        assertNotNull(topics);
    }

    private void printTopics(List<List<String>> topics) {
        for (int i = 0; i < topics.size(); i++) {
            System.out.println();
            System.out.print(i + "\t");
            for (String word : topics.get(i)) {
                System.out.print(word + ", ");
            }
        }
    }
}
