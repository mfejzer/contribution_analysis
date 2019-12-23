package pl.umk.wmii.msr.contributions.utils;

import pl.umk.wmii.msr.contributions.model.Topic;

public class TopicJFreeChartWrapper implements Comparable<TopicJFreeChartWrapper> {
    private static final int TOPIC_COUNT = 3;
    private final Topic topic;

    /**
     * @param topic
     */
    public TopicJFreeChartWrapper(Topic topic) {
        super();
        this.topic = topic;
    }

    @Override
    public int compareTo(TopicJFreeChartWrapper o) {
        return topic.toString().compareTo(o.topic.toString());
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
        TopicJFreeChartWrapper other = (TopicJFreeChartWrapper) obj;
        if (topic == null) {
            if (other.topic != null) {
                return false;
            }
        } else if (!topic.equals(other.topic)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((topic == null) ? 0 : topic.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return new Topic(topic.getWordFrequencies()
                .subList(
                        0,
                        Math.min(TOPIC_COUNT, topic
                                .getWordFrequencies().size())))
                .toString();
    }

}
