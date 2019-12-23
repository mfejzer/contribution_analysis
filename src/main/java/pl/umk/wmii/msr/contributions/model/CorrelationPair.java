package pl.umk.wmii.msr.contributions.model;

/**
 * Exists, because this language doesn't support ordered pairs
 */
public class CorrelationPair<T, S extends Classifiable> {
    private final T _1;
    private final S _2;

    public CorrelationPair(T _1, S _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public T get_1() {
        return _1;
    }

    public S get_2() {
        return _2;
    }
}
