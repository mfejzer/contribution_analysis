package pl.umk.wmii.msr.contributions.model;

import java.util.List;

/**
 * Represents Topic
 */
public class Topic {
    private static final int WORDS_TO_COMPARE_NUMBER = 10;
    private final List<TopicWordFrequency> wordFrequencies;

    public Topic(List<TopicWordFrequency> wordFrequencies) {
        this.wordFrequencies = wordFrequencies;
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
        Topic other = (Topic) obj;
        if (wordFrequencies == null) {
            if (other.wordFrequencies != null) {
                return false;
            }
        } else if (!checkTopicsEquality(wordFrequencies,
                other.wordFrequencies, WORDS_TO_COMPARE_NUMBER)) {
            return false;
        }
        return true;
    }

    public List<TopicWordFrequency> getWordFrequencies() {
        return wordFrequencies;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        int sublistElementsNumber = Math.min(wordFrequencies.size(),
                WORDS_TO_COMPARE_NUMBER);
        if (sublistElementsNumber != 0) {
            result = prime
                    * result
                    + wordFrequencies.subList(0,
                    sublistElementsNumber - 1).hashCode();
        } else {
            result = prime * result + 0;
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int minSize = wordFrequencies.size();
        int size = WORDS_TO_COMPARE_NUMBER > minSize ? minSize
                : WORDS_TO_COMPARE_NUMBER;
        for (int i = 0; i < size; i++) {
            stringBuilder.append(wordFrequencies.get(i));
        }
        return stringBuilder.toString();
    }

    private boolean checkTopicsEquality(
            List<TopicWordFrequency> firstTopicWordFrequencies,
            List<TopicWordFrequency> secondTopicWordFrequencies,
            int firstWordsParameter) {
        for (int i = 0; i < firstWordsParameter; i++) {
            try {
                if (!firstTopicWordFrequencies
                        .get(i)
                        .getWord()
                        .equals(secondTopicWordFrequencies.get(
                                i).getWord())) {
                    return false;
                }
            } catch (IndexOutOfBoundsException ex) {
                break;
            }
        }
        return true;
    }

}
