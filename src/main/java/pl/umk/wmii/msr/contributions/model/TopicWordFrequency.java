package pl.umk.wmii.msr.contributions.model;

/**
 * Represents word existing in Topic with its frequency
 */
public class TopicWordFrequency {
    private final Double frequency;
    private final Object word;

    public TopicWordFrequency(Double frequency, Object word) {
        this.frequency = frequency;
        this.word = word;
    }

    public Double getFrequency() {
        return frequency;
    }

    public String getWord() {
        return (String) word;
    }


    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(word + " (" + frequency + ") ");
        return stringBuilder.toString();
    }
}
