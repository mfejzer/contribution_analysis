package pl.umk.wmii.msr.contributions.model;

/**
 * GenericClassifier should work with classes implementing this interface
 */
public interface Classifiable {
    String text();

    String label();

    String source();

    String name();
}
