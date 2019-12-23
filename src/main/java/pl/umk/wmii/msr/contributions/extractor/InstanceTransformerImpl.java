package pl.umk.wmii.msr.contributions.extractor;

import cc.mallet.pipe.*;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import pl.umk.wmii.msr.contributions.model.Classifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Stateless service
 */
@Service("instanceTransformer")
public class InstanceTransformerImpl implements InstanceTransformer {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(InstanceTransformerImpl.class);

    private final Resource stopWordsResource = new ClassPathResource(
            "stopwords.txt");

    @Override
    public Pipe buildPipe() {
        List<Pipe> pipeList = new ArrayList<>();
        pipeList.add(new CharSequenceLowercase());
        pipeList.add(new CharSequence2TokenSequence(Pattern
                .compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
        pipeList.add(createStopWords());
        // pipeList.add(stemmerPipe());
        // pipeList.add(createStopWords());
        pipeList.add(new TokenSequence2FeatureSequence());
        return new SerialPipes(pipeList);
    }

    @Override
    public <T extends Classifiable> Instance prepareInstance(T classifiable) {
        Instance instance = new Instance(classifiable.text(),
                classifiable.label(), classifiable.name(),
                classifiable.source());
        return instance;
    }

    @Override
    public <T extends Classifiable> InstanceList prepareInstanceList(
            List<T> things) {
        InstanceList instanceList = new InstanceList(buildPipe());

        Function<? super T, Instance> transformer = new Function<T, Instance>() {
            @Override
            public Instance apply(T t) {
                return prepareInstance(t);
            }
        };

        instanceList.addThruPipe(Collections2.transform(things,
                transformer).iterator());
        return instanceList;
    }

    private TokenSequenceRemoveStopwords createStopWords() {
        TokenSequenceRemoveStopwords sequenceTransformer = new TokenSequenceRemoveStopwords();
        try {
            ArrayList<String> stopwordsList = new ArrayList<>();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            stopWordsResource.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                stopwordsList.add(line);
            }
            reader.close();
            String[] stopwords = new String[stopwordsList.size()];
            stopwords = stopwordsList.toArray(stopwords);
            sequenceTransformer.addStopWords(stopwords);
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            System.exit(1);
        }
        return sequenceTransformer;
    }

    private Pipe stemmerPipe() {
        return new Pipe() {
            @Override
            public Instance pipe(Instance carrier) {
                EnglishStemmer stemmer = new EnglishStemmer();
                TokenSequence in = (TokenSequence) carrier
                        .getData();

                for (Token token : in) {
                    stemmer.setCurrent(token.getText());
                    stemmer.stem();
                    token.setText(stemmer.getCurrent());
                }

                return carrier;
            }
        };
    }

}
