package pl.umk.wmii.msr.contributions.model;

/**
 * Exists, because this language doesn't support ordered pairs
 */
public class TopicCorrelation {
    private final Topic first;
    private final Topic second;

    public TopicCorrelation(Topic first, Topic second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TopicCorrelation other = (TopicCorrelation) obj;
        if (first == null) {
            if (other.first != null) {
                return false;
            }
        } else if (!first.equals(other.first)) {
            return false;
        }
        if (second == null) {
            if (other.second != null) {
                return false;
            }
        } else if (!second.equals(other.second)) {
            return false;
        }
        return true;
    }

    public Topic getFirst() {
        return first;
    }

    public Topic getSecond() {
        return second;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((first == null) ? 0 : first.hashCode());
        result = prime * result
                + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(first + "\n");
        stringBuilder.append(second);
        return stringBuilder.toString();
    }

}
