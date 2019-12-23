package pl.umk.wmii.msr.contributions.extractor;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import pl.umk.wmii.msr.contributions.model.Classifiable;

import java.util.List;

/**
 * Implementations should provide transformation from Classifiable to mallet Instance
 */
public interface InstanceTransformer {

    <T extends Classifiable> Instance prepareInstance(T classifiable);

    <T extends Classifiable> InstanceList prepareInstanceList(List<T> things);

    Pipe buildPipe();
}
