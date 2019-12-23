package pl.umk.wmii.msr.contributions.extractor;

import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import pl.umk.wmii.msr.contributions.config.AppConfig;
import pl.umk.wmii.msr.contributions.model.Dummy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class InstanceTransformerTest {

    public static final String TEXT_TO_CLASSIFY = "text to classify";
    public static final String ITS_LABEL = "its label";
    public static final String SOURCE = "source";
    public static final String DOCUMENT_NAME = "document name";

    @Autowired
    private InstanceTransformer tested;

    @Test
    public void shouldPrepareInstance() throws Exception {
        //given
        Dummy dummy = new Dummy(TEXT_TO_CLASSIFY, ITS_LABEL, SOURCE, DOCUMENT_NAME);

        //when
        Instance instance = tested.prepareInstance(dummy);

        //then
        assertEquals(dummy.text(), instance.getData());
    }

    @Test
    public void shouldPrepareInstanceList() throws Exception {
        //given
        List<Dummy> dummies = new ArrayList<>();
        dummies.add(new Dummy(TEXT_TO_CLASSIFY, ITS_LABEL, SOURCE, DOCUMENT_NAME));

        //when
        InstanceList instanceList = tested.prepareInstanceList(dummies);

        //then
        assertEquals(dummies.size(), instanceList.size());
        FeatureSequence featureSequence = (FeatureSequence) instanceList.get(0).getData();
        assertEquals(2, featureSequence.size());
        assertEquals("text", featureSequence.get(0));
        assertEquals("classify", featureSequence.get(1));
    }
}
