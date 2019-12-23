package pl.umk.wmii.msr.contributions.correlation;

import com.google.common.base.Function;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.umk.wmii.msr.contributions.config.AppConfig;
import pl.umk.wmii.msr.contributions.extractor.TopicExtractor;
import pl.umk.wmii.msr.contributions.model.CorrelationPair;
import pl.umk.wmii.msr.contributions.model.Dummy;
import pl.umk.wmii.msr.contributions.model.TopicCorrelation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class DiscovererTest {

    private Discoverer discoverer;

    @Autowired
    public void setDiscoverer(Discoverer discoverer) {
        this.discoverer = discoverer;
    }

    @Test
    public void shouldCalculateCorrelationBetweenTopics() throws Exception {
        //given
        final List<Dummy> firstDummies = new ArrayList<>();
        firstDummies.add(new Dummy("Bwana Bwana", null, "", null));
        firstDummies.add(new Dummy("EEE EEE", null, "", null));

        final List<Dummy> secondDummies = new ArrayList<>();
        secondDummies.add(new Dummy("Prr Prr", null, "", null));
        secondDummies.add(new Dummy("YYY YYY", null, "", null));

        Function<Dummy, Iterable<CorrelationPair<Dummy, Dummy>>> correlationFinder =
                new Function<Dummy, Iterable<CorrelationPair<Dummy, Dummy>>>() {
                    @Override
                    public Iterable<CorrelationPair<Dummy, Dummy>> apply(Dummy dummy) {
                        List<CorrelationPair<Dummy, Dummy>> dummies = new ArrayList<>();
                        if (dummy.equals(firstDummies.get(0))) {
                            dummies.add(new CorrelationPair<Dummy, Dummy>(dummy, secondDummies.get(0)));
                        } else {
                            dummies.add(new CorrelationPair<Dummy, Dummy>(dummy, secondDummies.get(1)));
                        }
                        return dummies;
                    }
                };

        TopicExtractor.TopicExtractorParameters params = new TopicExtractor.TopicExtractorParameters(2, 20, 1.0, 0.01, 1);

        //when
        Map<TopicCorrelation, Integer> topicCorrelationMap =
                discoverer.calculateCorrelationBetweenTopics(firstDummies, secondDummies, correlationFinder, params);


        //then
        assertFalse(topicCorrelationMap.isEmpty());
        for (TopicCorrelation topicCorrelation : topicCorrelationMap.keySet()) {
            if (topicCorrelation.getFirst().getWordFrequencies().get(0).equals("bwana")) {
                assertEquals("prr", topicCorrelation.getSecond().getWordFrequencies().get(0));
            }
            if (topicCorrelation.getFirst().getWordFrequencies().get(0).equals("eee")) {
                assertEquals("yyy", topicCorrelation.getSecond().getWordFrequencies().get(0));
            }
        }
        assertEquals(2, topicCorrelationMap.values().size());

    }

}
