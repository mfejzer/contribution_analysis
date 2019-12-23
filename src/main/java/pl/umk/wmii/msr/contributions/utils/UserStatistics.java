package pl.umk.wmii.msr.contributions.utils;

import pl.umk.wmii.msr.contributions.model.Topic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates user statistics - for specialised users and multitopic users
 */
public class UserStatistics {

    /**
     * @param topics  map of topics grouped by users
     * @param <Group> group representing user (probably just String)
     * @return
     */
    public static <Group extends Comparable> String generate(Map<Group, Map<Topic, Integer>> topics) {

        Map<Topic, Integer> topicSums = new HashMap<>();
        Integer averageTopicCountPerUser = 0;
        Integer maxTopicCounter = 0;

        for (Map<Topic, Integer> userTopics : topics.values()) {
            Integer nonZeroCountPerUser = 0;
            for (Map.Entry<Topic, Integer> entry : userTopics.entrySet()) {
                Topic topic = entry.getKey();
                Integer topicCounter = entry.getValue();
                if (!topicSums.containsKey(topic)) {
                    topicSums.put(topic, entry.getValue());
                } else {
                    Integer current = topicSums.get(topic);
                    current += entry.getValue();
                    topicSums.put(topic, current);
                }

                if (topicCounter > 0) {
                    nonZeroCountPerUser++;
                    if (maxTopicCounter < topicCounter) {
                        maxTopicCounter = topicCounter;
                    }
                }
            }
            averageTopicCountPerUser += nonZeroCountPerUser;
        }
        averageTopicCountPerUser /= topics.entrySet().size();

        Integer userCount = topics.keySet().size();
        Map<Topic, Double> topicAverages = new HashMap<>();
        for (Map.Entry<Topic, Integer> sum : topicSums.entrySet()) {
            topicAverages.put(sum.getKey(), Double.valueOf(sum.getValue() / userCount));
        }
        Double average = 0d;
        for (Double topicAverage : topicAverages.values()) {
            average += topicAverage;
        }
        average /= userCount;
        List<Group> specUsers = new ArrayList<>();
        List<Group> multiUsers = new ArrayList<>();
        List<Group> coreUsers = new ArrayList<>();
        List<Group> bothSpecMultiUsers = new ArrayList<>();
        List<Group> normalUsers = new ArrayList<>();
        List<Group> allUsers = new ArrayList<>();

        for (Group user : topics.keySet()) {
            Map<Topic, Integer> topicCountPerUser = topics.get(user);
            boolean spec = isSpec(topicCountPerUser, maxTopicCounter);
            if (spec) {
                specUsers.add(user);
            }
            boolean multi = isMulti(topicCountPerUser, averageTopicCountPerUser);
            if (multi) {
                multiUsers.add(user);
            }
            if (spec || multi) {
                coreUsers.add(user);
            }
            if (spec && multi) {
                bothSpecMultiUsers.add(user);
            }
            if (!spec && !multi) {
                normalUsers.add(user);
            }
            allUsers.add(user);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Spec Users ");
        stringBuilder.append(specUsers.size());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("Multi Users ");
        stringBuilder.append(multiUsers.size());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("Core Users ");
        stringBuilder.append(coreUsers.size());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("Both Users ");
        stringBuilder.append(bothSpecMultiUsers.size());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("Normal Users ");
        stringBuilder.append(normalUsers.size());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("All Users ");
        stringBuilder.append(topics.keySet().size());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("All Users ");
        stringBuilder.append(allUsers.size());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("Spec Users:");
        stringBuilder.append(System.lineSeparator());
        for (Group user : specUsers) {
            stringBuilder.append(user);
            stringBuilder.append(System.lineSeparator());
        }
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("Multi Users:");
        stringBuilder.append(System.lineSeparator());
        for (Group user : multiUsers) {
            stringBuilder.append(user);
            stringBuilder.append(System.lineSeparator());
        }


//        stringBuilder.append(maxTopicCounter);
//        stringBuilder.append(System.lineSeparator());

        return stringBuilder.toString();
    }

    private static boolean isMulti(Map<Topic, Integer> topicCountPerUser,
                                   Integer averageTopicCountPerUser) {
        Integer nonZeroCountPerUser = 0;
        for (Map.Entry<Topic, Integer> entry : topicCountPerUser.entrySet()) {
            if (entry.getValue() > 0) {
                nonZeroCountPerUser++;
            }
        }
        return nonZeroCountPerUser >= averageTopicCountPerUser;
    }

    private static boolean isSpec(Map<Topic, Integer> topicCountPerUser,
                                  Integer maxTopicCounter) {
        boolean result = false;
        for (Map.Entry<Topic, Integer> entry : topicCountPerUser.entrySet()) {
//            Double average = topicAverage.get(entry.getKey());
//            if (entry.getValue() > (2) * average) {
//                result = true;
//            }
            if (entry.getValue() > (maxTopicCounter / 2)) {
                result = true;
            }
        }
        return result;
    }
}
