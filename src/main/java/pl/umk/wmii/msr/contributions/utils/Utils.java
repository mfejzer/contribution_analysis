package pl.umk.wmii.msr.contributions.utils;

import com.google.common.base.Function;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.umk.wmii.msr.contributions.model.Issue;
import pl.umk.wmii.msr.contributions.model.Topic;
import pl.umk.wmii.msr.contributions.model.TopicWordFrequency;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;

public class Utils {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(Utils.class);

    /**
     * Displays chart showing topics popularity in time intervals
     *
     * @param topics                 Topics model map ordered by Group. Each Group entry
     *                               represents some group and contains another map with
     *                               topics popularity in which key is topic and value is
     *                               topic popularity.
     * @param projectId              project id, just to show it as chart name
     * @param desc                   description, used as part of chart name
     * @param categoryLabelGenerator function providing category labels
     * @param chartType              type of chart - bar chart or line chart
     * @param <Group>                type of group
     * @param chartDirectory         directory where generated charts will be saved
     * @param displayChart           if true chart will be displayed
     * @param saveChart              if true chart will be saved at chartDirectory
     *                               directory
     * @param showLegend             if true legend will be shown below chart
     * @see Topic
     */
    public static <Group extends Comparable> void createChart(
            final Map<Group, Map<Topic, Integer>> topics,
            final Long projectId, final String desc,
            final Function<Group, String> categoryLabelGenerator,
            final ChartType chartType, final Path chartDirectory,
            final boolean displayChart, final boolean saveChart,
            final boolean showLegend) {
        final JFreeChart chart = categoryChart("Project id\n"
                        + projectId, topics, categoryLabelGenerator, chartType,
                true);
        if (saveChart) {
            try {
                ImageIO.write(
                        chart.createBufferedImage(1024, 768),
                        "png",
                        Files.createTempFile(
                                chartDirectory,
                                "chart-project_id" + projectId + "_" + desc
                                        + "-", ".png").toFile());
            } catch (IOException e) {
                String msg = String.format(
                        "Coldn't save chart file. Reason: %s",
                        e.toString());
                LOGGER.debug(msg);
            }
        }
        if (displayChart) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {

                    JFrame frame = new JFrame();
                    ChartPanel chartPanel = new ChartPanel(
                            chart);
                    chartPanel
                            .setPreferredSize(new Dimension(
                                    1024, 768));
                    frame.setContentPane(chartPanel);
                    frame.pack();
                    frame.setVisible(true);
                }
            });
        }
    }

    /**
     * Creates directory structure understandable by mallet.
     *
     * @param issues issues collection
     * @param parent parent directory
     * @throws IOException thrown when directory cannot be created
     */
    public static void createIssuesDir(List<Issue> issues, Path parent)
            throws IOException {
        for (int i = 0; i < issues.size(); i++) {
            Path dirPath = parent.resolve(Integer.toString(i));
            Files.createDirectories(dirPath);
            Path issueFilePath = dirPath.resolve("issue" + i
                    + ".txt");
            if (issues.get(i).getBody() != null) {
                Files.write(
                        issueFilePath,
                        Arrays.asList(new String[]{issues
                                .get(i).getBody()
                                .replaceAll("\n", "")}),
                        // Arrays.asList(issues.get(i).getBody()
                        // .split("\n")),
                        Charset.defaultCharset(),
                        StandardOpenOption.WRITE,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            }
        }
    }

    /**
     * Displays text showing topics popularity in aggregations
     *
     * @param topics  Topics model map ordered by Group. Each Group entry
     *                represents some group and contains another map with
     *                topics popularity in which key is topic and value is
     *                topic popularity.
     * @param <Group> type of group
     */
    public static <Group extends Comparable> void displayAggregationText(
            Map<Group, Map<Topic, Integer>> topics) {
        String text = createAggregationText(topics);
        System.out.println(text);
    }

    /**
     * @param topics  Topics model map ordered by Group. Each Group entry
     *                represents some group and contains another map with
     *                topics popularity in which key is topic and value is
     *                topic popularity.
     * @param <Group> type of group
     */
    public static <Group extends Comparable> void saveAggregationText(Map<Group, Map<Topic, Integer>> topics,
                                                                      Path chartDirectory, Long projectId, String desc) {
        try {
            File file = Files.createTempFile(
                    chartDirectory,
                    "text-project_id_" + projectId + "_" + desc
                            + "-", ".txt").toFile();
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new FileWriter(file));
            bufferedWriter.write(createAggregationText(topics));
            bufferedWriter.close();

        } catch (IOException e) {
            String msg = String.format(
                    "Couldn't save text file. Reason: %s",
                    e.toString());
            LOGGER.debug(msg);
        }
    }

    public static <Group extends Comparable> void saveMonthAggergation(Map<Group, Map<Topic, Integer>> topics,
                                                                       Path directory, Long projectId, String desc) {
        try {
            File file = Files.createTempFile(
                    directory,
                    "text-project_id_" + projectId + "_" + desc
                            + "-", ".txt").toFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(createMonthText(topics));
            bufferedWriter.close();

        } catch (IOException e) {
            String msg = String.format(
                    "Couldn't save text file. Reason: %s",
                    e.toString());
            LOGGER.debug(msg);
        }
    }

    private static <Group extends Comparable> String createMonthText(Map<Group, Map<Topic, Integer>> topics) {
        StringBuilder stringBuilder = new StringBuilder();
        SortedSet<Group> sortedGroupKeys = new TreeSet<>(topics.keySet());

        Map<Group, Integer> bugOccurrencesPerMonth = new HashMap<>();
        Map<Group, Integer> presentTopicsPerMonth = new HashMap<>();
        for (Group group : sortedGroupKeys) {
            for (Entry<Topic, Integer> topicCounter : topics.get(group).entrySet()) {
                Topic topic = topicCounter.getKey();
                boolean containsBug = false;
                if (topicCounter.getValue() > 0) {
                    Integer topicsInMonth = presentTopicsPerMonth.getOrDefault(group, 0);
                    topicsInMonth += 1;
                    presentTopicsPerMonth.put(group, topicsInMonth);
                    for (TopicWordFrequency topicWordFrequency : topic.getWordFrequencies()) {
                        if (topicWordFrequency.getWord().contains("bug") ||
                                topicWordFrequency.getWord().contains("fix") ||
                                topicWordFrequency.getWord().contains("solve")
                        ) {
                            containsBug = true;
                        }
                    }
                }
                if (containsBug) {
                    Integer occurences = bugOccurrencesPerMonth.getOrDefault(group, 0);
                    occurences += 1;
                    bugOccurrencesPerMonth.put(group, occurences);
                }
            }
        }

        stringBuilder.append("All months: ");
        stringBuilder.append(topics.keySet().size()).append(" ");
        stringBuilder.append("Months with bugs: ");
        stringBuilder.append(bugOccurrencesPerMonth.keySet().size());
        stringBuilder.append(System.lineSeparator());

        Integer counter = 0;
        Integer total = 0;

        for (Entry<Group, Integer> entry : bugOccurrencesPerMonth.entrySet()) {
            stringBuilder.append("Month: ").append(entry.getKey()).append(" ");
            stringBuilder.append("Buggy counter: ").append(entry.getValue()).append(" ");
            stringBuilder.append("Total counter: ").append(presentTopicsPerMonth.getOrDefault(entry.getKey(), 0));
            stringBuilder.append(System.lineSeparator());
            total += presentTopicsPerMonth.getOrDefault(entry.getKey(), 0);
            counter += entry.getValue();
        }
        stringBuilder.append("All months buggy counter: ").append(counter).append(" ");
        stringBuilder.append("All months total counter: ").append(total);
        stringBuilder.append(System.lineSeparator());

        return stringBuilder.toString();
    }

    /**
     * Temporary solution, should be made refactored along with saveAggregationText
     */
    public static <Group extends Comparable> void saveUserStatisticsText(
            Map<Group, Map<Topic, Integer>> topics,
            Path chartDirectory, Long projectId, String desc) {
        try {
            File file = Files.createTempFile(
                    chartDirectory,
                    "text-project_id_" + projectId + "_" + desc
                            + "-", ".txt").toFile();
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new FileWriter(file));
            bufferedWriter.write(UserStatistics.generate(topics));
            bufferedWriter.close();

        } catch (IOException e) {
            String msg = String.format(
                    "Couldn't save text file. Reason: %s",
                    e.toString());
            LOGGER.debug(msg);
        }

    }


    public static <Group extends Comparable> String createAggregationText(Map<Group, Map<Topic, Integer>> topics) {
        StringBuilder stringBuilder = new StringBuilder();
        SortedSet<Group> sortedGroupKeys = new TreeSet<>(topics.keySet());
        for (Group group : sortedGroupKeys) {
            stringBuilder.append("For group " + group);
            stringBuilder.append(System.lineSeparator());
            for (Entry<Topic, Integer> topicCounter : topics.get(group).entrySet()) {
                stringBuilder.append("Topic: " + topicCounter.getKey());
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append("Counter: " + topicCounter.getValue());
                stringBuilder.append(System.lineSeparator());
            }
            stringBuilder.append("###########################");
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    private static <Group extends Comparable> JFreeChart categoryChart(
            String chartTitle, Map<Group, Map<Topic, Integer>> topics,
            Function<Group, String> categoryLabelGenerator,
            ChartType chartType, boolean showLegend) {
        CategoryDataset dataset = createCategoryDataset(topics,
                categoryLabelGenerator);
        JFreeChart chart = createChart(dataset, chartTitle, chartType,
                showLegend);
        return chart;
    }

    private static <Group extends Comparable> CategoryDataset createCategoryDataset(
            Map<Group, Map<Topic, Integer>> topics,
            Function<Group, String> categoryLabelGenerator) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Group group : topics.keySet()) {
            for (Entry<Topic, Integer> dateEntry : topics.get(group).entrySet()) {
                dataset.addValue(dateEntry.getValue(), new TopicJFreeChartWrapper(dateEntry.getKey()), categoryLabelGenerator.apply(group));
            }
        }
        return dataset;
    }

    private static JFreeChart createChart(CategoryDataset dataset,
                                          String title, ChartType chartType, boolean showLegend) {
        JFreeChart chart = null;
        switch (chartType) {
            case BAR:
                chart = ChartFactory.createBarChart(title,
                        "Counter", "Grouping", dataset,
                        PlotOrientation.VERTICAL, showLegend,
                        false, false);
                break;
            case LINE:
                chart = ChartFactory.createLineChart(title,
                        "Counter", "Grouping", dataset,
                        PlotOrientation.VERTICAL, showLegend,
                        false, false);
                break;
        }

        CategoryPlot categoryPlot = chart.getCategoryPlot();
        CategoryAxis domainAxis = categoryPlot.getDomainAxis();
        domainAxis
                .setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        return chart;

    }
}
