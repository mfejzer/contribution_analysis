package pl.umk.wmii.msr.contributions.aggergator;

import pl.umk.wmii.msr.contributions.config.AppConfig;
import pl.umk.wmii.msr.contributions.model.GroupableByDateDummy;
import pl.umk.wmii.msr.contributions.model.Topic;
import pl.umk.wmii.msr.contributions.model.TopicWordFrequency;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class AggregatorImplTest {

    @Autowired
    private Aggregator aggregator;

    @Test
    public void shouldAggregateByMonth() {
        //given
        List<GroupableByDateDummy> dummies = createDummies();

        Topic t1 = prepareSimpleTopic(3d, "AAA");
        Topic t2 = prepareSimpleTopic(3d, "BBB");

        Map<GroupableByDateDummy, Topic> topicMap = new HashMap<>();
        topicMap.put(dummies.get(0), t1);
        topicMap.put(dummies.get(1), t2);
        topicMap.put(dummies.get(2), t1);
        topicMap.put(dummies.get(3), t1);

        //when
        Map<Date, Map<Topic, Integer>> aggregated = aggregator.aggregate
                (dummies, new MonthlyDateGrouper(), topicMap);

        //then
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2014, 0, 1);
        Date firstOfJanuary14 = calendar.getTime();
        calendar.set(2013, 1, 1);
        Date firstOfFebruary13 = calendar.getTime();


        assertEquals(2, aggregated.keySet().size());
        assertTrue(aggregated.containsKey(firstOfJanuary14));
        assertTrue(aggregated.containsKey(firstOfFebruary13));

        Map<Topic, Integer> january14Aggregation = aggregated.get
                (firstOfJanuary14);
        Map<Topic, Integer> february13Aggregation = aggregated.get
                (firstOfFebruary13);

        assertEquals(2, january14Aggregation.keySet().size());
        assertEquals(2, february13Aggregation.keySet().size());

        assertEquals(Integer.valueOf(1), january14Aggregation.get(t1));
        assertEquals(Integer.valueOf(1), january14Aggregation.get(t2));
        assertEquals(Integer.valueOf(2), february13Aggregation.get(t1));
        assertEquals(Integer.valueOf(0), february13Aggregation.get(t2));
    }


    @Test
    public void shouldRemoveSignificant() {
        //given
        List<GroupableByDateDummy> dummies = createDummies();

        Topic t1 = prepareSimpleTopic(3d, "AAA");
        Topic t2 = prepareSimpleTopic(3d, "BBB");

        Map<GroupableByDateDummy, Topic> topicMap = new HashMap<>();
        topicMap.put(dummies.get(0), t1);
        topicMap.put(dummies.get(1), t2);
        topicMap.put(dummies.get(2), t1);
        topicMap.put(dummies.get(3), t1);

        //when
        Map<Date, Map<Topic, Integer>> aggregated = aggregator
                .aggregateWithoutMostSignificant(dummies,
                        new MonthlyDateGrouper(), topicMap, 1);

        //then
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2014, 0, 1);
        Date firstOfJanuary14 = calendar.getTime();
        calendar.set(2013, 1, 1);
        Date firstOfFebruary13 = calendar.getTime();

        assertEquals(2, aggregated.keySet().size());
        assertTrue(aggregated.containsKey(firstOfJanuary14));
        assertTrue(aggregated.containsKey(firstOfFebruary13));

        Map<Topic, Integer> january14Aggregation = aggregated.get
                (firstOfJanuary14);
        Map<Topic, Integer> february13Aggregation = aggregated.get
                (firstOfFebruary13);

        assertEquals(1, january14Aggregation.keySet().size());
        assertEquals(1, february13Aggregation.keySet().size());

        assertEquals(Integer.valueOf(1), january14Aggregation.get(t2));
        assertEquals(Integer.valueOf(0), february13Aggregation.get(t2));
    }

    private List<GroupableByDateDummy> createDummies() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        calendar.set(2014, 0, 4);
        Date forthOfJanuary14 = calendar.getTime();
        calendar.set(2014, 0, 20);
        Date twentiethOfJanuary14 = calendar.getTime();
        calendar.set(2013, 1, 4);
        Date forthOfFebruary13 = calendar.getTime();
        calendar.set(2013, 1, 20);
        Date twentiethOfFebruary13 = calendar.getTime();

        GroupableByDateDummy c1 = new GroupableByDateDummy("t1", "", "", "",
                forthOfJanuary14);
        GroupableByDateDummy c2 = new GroupableByDateDummy("t2", "", "", "",
                twentiethOfJanuary14);
        GroupableByDateDummy c3 = new GroupableByDateDummy("t1", "", "", "",
                forthOfFebruary13);
        GroupableByDateDummy c4 = new GroupableByDateDummy("t2", "", "", "",
                twentiethOfFebruary13);

        List<GroupableByDateDummy> dummies = new ArrayList<>();
        dummies.add(c1);
        dummies.add(c2);
        dummies.add(c3);
        dummies.add(c4);
        return dummies;
    }


    private Topic prepareSimpleTopic(Double frequency, String word) {
        List<TopicWordFrequency> words = new ArrayList();
        words.add(new TopicWordFrequency(frequency, word));
        return new Topic(words);
    }

}
