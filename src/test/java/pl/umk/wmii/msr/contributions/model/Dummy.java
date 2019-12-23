package pl.umk.wmii.msr.contributions.model;

/**
 * Dummy Classifiable, shall be used for testing only
 */
public class Dummy implements Classifiable {

    public Dummy(String text, String label, String source, String name) {
        this.text = text;
        this.label = label;
        this.source = source;
        this.name = name;
    }

    private String text;
    private String label;
    private String source;
    private String name;

    @Override
    public String text() {
        return text;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public String source() {
        return source;
    }

    @Override
    public String name() {
        return name;
    }
}
