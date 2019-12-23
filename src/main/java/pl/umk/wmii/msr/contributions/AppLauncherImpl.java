package pl.umk.wmii.msr.contributions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pl.umk.wmii.msr.contributions.aggergator.*;
import pl.umk.wmii.msr.contributions.correlation.Discoverer;
import pl.umk.wmii.msr.contributions.dao.Msr14Dao;
import pl.umk.wmii.msr.contributions.dao.Msr14ProjectDao;
import pl.umk.wmii.msr.contributions.extractor.TopicExtractor;
import pl.umk.wmii.msr.contributions.model.Commit;
import pl.umk.wmii.msr.contributions.model.Issue;
import pl.umk.wmii.msr.contributions.model.Topic;
import pl.umk.wmii.msr.contributions.model.TopicModel;
import pl.umk.wmii.msr.contributions.utils.ChartType;
import pl.umk.wmii.msr.contributions.utils.DateMonthCategoryLabelGenerator;
import pl.umk.wmii.msr.contributions.utils.StringIdCategoryLabelGenerator;
import pl.umk.wmii.msr.contributions.utils.Utils;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class AppLauncherImpl implements Msr14AppLauncher {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppLauncherImpl.class);

    private static final boolean DISPLAY_CHART = false;

    private static final boolean SAVE_CHART = true;

    private static final boolean SHOW_LEGEND = true;

    private Aggregator aggregator;

    private Discoverer discoverer;

    private Msr14Dao msr14Dao;

    private Msr14ProjectDao msr14ProjectDao;

    private TopicExtractor topicExtractor;

    @Override
    public void launchCommitsTimeGrouper(String[] args) {
        TopicExtractor.TopicExtractorParameters params = new TopicExtractor.TopicExtractorParameters(
                50, 1000, 10, 0.01, 8);

        Long projectId = Long.valueOf(args[0]);

        List<Issue> projectIssues = msr14Dao.findIssuesByProjectId(projectId);

        List<Commit> projectCommits = msr14Dao
                .findCommitsByProjectId(projectId);

        TopicModel<Commit> commitTopicModel = topicExtractor
                .trainTopicModel(projectCommits, params);
        TopicModel<Issue> issuesTopicModel = topicExtractor
                .trainTopicModel(projectIssues, params);

        Map<Commit, Topic> projectCommitsToTopic = topicExtractor
                .generateEntitiesToTopicsMap(projectCommits,
                        commitTopicModel);

        Map<Issue, Topic> projectIssuesToTopic = topicExtractor
                .generateEntitiesToTopicsMap(projectIssues,
                        issuesTopicModel);

        Map<Date, Map<Topic, Integer>> topicsGroupedByDate = aggregator
                .aggregate(projectCommits, new MonthlyDateGrouper<Commit>(),
                        projectCommitsToTopic);

        Map<Date, Map<Topic, Integer>> issuesTopicsGroupedByDate = aggregator
                .aggregate(projectIssues, new MonthlyDateGrouper<Issue>(),
                        projectIssuesToTopic);

        Map<String, Map<Topic, Integer>> topicsGroupedByAuthorName = aggregator
                .aggregate(projectCommits, new UserNameCommitGrouper(
                        new CommitAuthor()), projectCommitsToTopic);

        Map<String, Map<Topic, Integer>> topicsGroupedByAuthorLocation = aggregator
                .aggregate(projectCommits,
                        new UserLocationCommitGrouper(
                                new CommitAuthor()),
                        projectCommitsToTopic);

        Map<String, Map<Topic, Integer>> topicsGroupedByAuthorCompany = aggregator
                .aggregate(
                        projectCommits,
                        new UserCompanyCommitGrouper(new CommitAuthor()),
                        projectCommitsToTopic);

        Map<String, Map<Topic, Integer>> topicsGroupedByProjectLanguage = aggregator
                .aggregate(projectCommits,
                        new ProjectLanguageCommitGrouper(),
                        projectCommitsToTopic);


        Map<Date, Map<Topic, Integer>> topicsGroupedByDateWithoutMostSignigicant
                = aggregator.aggregateWithoutMostSignificant(projectCommits,
                new MonthlyDateGrouper<Commit>(),
                projectCommitsToTopic, 3);

        Map<Date, Map<Topic, Integer>> issuesGroupedByDateWithoutMostSignigicant
                = aggregator.aggregateWithoutMostSignificant(projectIssues,
                new MonthlyDateGrouper<Issue>(),
                projectIssuesToTopic, 3);

        boolean displayChart = DISPLAY_CHART;
        try {
            displayChart = Boolean.parseBoolean(args[2]);
        } catch (IndexOutOfBoundsException ex) {
            String msg = "Display chart flag set to true";
            LOGGER.info(msg);
        }


        Path chartDir = FileSystems.getDefault().getPath(args[1]);

        Utils.displayAggregationText(topicsGroupedByAuthorName);
        Utils.displayAggregationText(topicsGroupedByAuthorLocation);
        Utils.displayAggregationText(topicsGroupedByAuthorCompany);
        Utils.displayAggregationText(topicsGroupedByProjectLanguage);
        Utils.displayAggregationText(topicsGroupedByDate);
        Utils.displayAggregationText(topicsGroupedByDateWithoutMostSignigicant);
        Utils.displayAggregationText(issuesTopicsGroupedByDate);

        Utils.saveAggregationText(topicsGroupedByAuthorName, chartDir, projectId, "topicsGroupedByAuthorName");
        Utils.saveAggregationText(topicsGroupedByAuthorLocation, chartDir, projectId, "topicsGroupedByAuthorLocation");
        Utils.saveAggregationText(topicsGroupedByAuthorCompany, chartDir, projectId, "topicsGroupedByAuthorCompany");
        Utils.saveAggregationText(topicsGroupedByProjectLanguage, chartDir, projectId, "topicsGroupedByProjectLanguage");
        Utils.saveAggregationText(topicsGroupedByDate, chartDir, projectId, "topicsGroupedByDate");
        Utils.saveAggregationText(topicsGroupedByDateWithoutMostSignigicant, chartDir, projectId, "topicsGroupedByDateWithoutMostSignigicant");
        Utils.saveAggregationText(issuesTopicsGroupedByDate, chartDir, projectId, "issuesTopicsGroupedByDate");
        Utils.saveAggregationText(issuesGroupedByDateWithoutMostSignigicant, chartDir, projectId, "issuesTopicsGroupedByDateWithoutMostSignigicant");

        Utils.saveUserStatisticsText(topicsGroupedByAuthorName, chartDir, projectId, "userstatistics");

        Utils.saveMonthAggergation(issuesTopicsGroupedByDate, chartDir, projectId, "issuesByMonth");
        Utils.saveMonthAggergation(topicsGroupedByDate, chartDir, projectId, "commitsByMonth");

        Utils.createChart(topicsGroupedByDate, projectId, "topicsGroupedByDate",
                new DateMonthCategoryLabelGenerator(), ChartType.LINE,
                chartDir, displayChart, SAVE_CHART, SHOW_LEGEND);
        Utils.createChart(topicsGroupedByDateWithoutMostSignigicant, projectId,
                "topicsGroupedByDateWithoutMostSignigicant",
                new DateMonthCategoryLabelGenerator(), ChartType.LINE,
                chartDir, displayChart, SAVE_CHART, SHOW_LEGEND);
        Utils.createChart(topicsGroupedByAuthorName,
                projectId, "topicsGroupedByAuthorName",
                new StringIdCategoryLabelGenerator(), ChartType.BAR,
                chartDir, displayChart, SAVE_CHART, SHOW_LEGEND);
        Utils.createChart(topicsGroupedByAuthorLocation,
                projectId, "topicsGroupedByAuthorLocation",
                new StringIdCategoryLabelGenerator(), ChartType.BAR,
                chartDir, displayChart, SAVE_CHART, SHOW_LEGEND);
        Utils.createChart(topicsGroupedByAuthorCompany,
                projectId, "topicsGroupedByAuthorCompany",
                new StringIdCategoryLabelGenerator(), ChartType.BAR,
                chartDir, displayChart, SAVE_CHART, SHOW_LEGEND);
        Utils.createChart(topicsGroupedByProjectLanguage,
                projectId, "topicsGroupedByProjectLanguage",
                new StringIdCategoryLabelGenerator(), ChartType.BAR,
                chartDir, displayChart, SAVE_CHART, SHOW_LEGEND);
        Utils.createChart(issuesTopicsGroupedByDate, projectId,
                "issuesTopicsGroupedByDate",
                new DateMonthCategoryLabelGenerator(), ChartType.LINE,
                chartDir, displayChart, SAVE_CHART, SHOW_LEGEND);
        Utils.createChart(issuesGroupedByDateWithoutMostSignigicant, projectId,
                "issuesGroupedByDateWithoutMostSignigicant",
                new DateMonthCategoryLabelGenerator(), ChartType.LINE,
                chartDir, displayChart, SAVE_CHART, SHOW_LEGEND);
    }

    @Autowired
    public void setDiscoverer(Discoverer discoverer) {
        this.discoverer = discoverer;
    }

    @Autowired
    public void setAggregator(Aggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Autowired
    // @Qualifier("mongo")
    @Qualifier("mix")
    public void setMsr14Dao(Msr14Dao msr14Dao) {
        this.msr14Dao = msr14Dao;
    }

    @Autowired
    public void setMsr14ProjectDao(Msr14ProjectDao msr14ProjectDao) {
        this.msr14ProjectDao = msr14ProjectDao;
    }

    @Autowired
    public void setTopicExtractor(TopicExtractor topicExtractor) {
        this.topicExtractor = topicExtractor;
    }
}
