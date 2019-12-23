package pl.umk.wmii.msr.contributions.dao;

import com.mongodb.DBObject;
import com.mongodb.MongoException;
import pl.umk.wmii.msr.contributions.model.Commit;
import pl.umk.wmii.msr.contributions.model.Issue;
import pl.umk.wmii.msr.contributions.model.Project;
import pl.umk.wmii.msr.contributions.model.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

@Service("mix")
public class Msr14MixDao implements Msr14Dao {
    private static final List<String> commitsBaseColumns = Arrays
            .asList(new String[]{"body", "commits.id",
                    "commit_comments.commit_id", "commits.created_at",
                    "projects.id as projectId",
                    "projects.description as projectDescription",
                    "commits.committer_id", "commits.author_id",
                    "projects.language",
                    "commiterUser.id as commiterUserId",
                    "commiterUser.company as commiterUserCompany",
                    "commiterUser.location as commiterUserLocation",
                    "commiterUser.name as commiterUserName",
                    "authorUser.id as authorUserId",
                    "authorUser.company as authorUserCompany",
                    "authorUser.location as authorUserLocation",
                    "authorUser.name as authorUserName"});
    private static final List<String> commitsBaseConditions = Arrays
            .asList(new String[]{
                    "commit_comments.commit_id = commits.id",
                    "projects.id = commits.project_id",
                    "commiterUser.id = commits.committer_id",
                    "authorUser.id = commits.author_id"});

    private static final List<String> commitsBaseTables = Arrays
            .asList(new String[]{"commit_comments", "commits",
                    "projects", "users as commiterUser",
                    "users as authorUser"});
    private JdbcOperations jdbcOperations;
    private MongoOperations mongoOperations;

    @Override
    public List<Commit> findCommits() {
        return findCommitsLogic(0);
    }

    @Override
    public List<Commit> findCommits(int limit) {
        return findCommitsLogic(limit);
    }

    @Override
    public List<Commit> findCommitsByProjectId(long projectId) {
        return findCommitsByProjectIdLogic(projectId, 0);
    }

    @Override
    public List<Commit> findCommitsByProjectId(long projectId, int limit) {
        return findCommitsByProjectIdLogic(projectId, limit);
    }

    @Override
    public List<Issue> findIssues() {
        return findMongoIssueDetails(findIssueCommentsExtRefs(0));
    }

    @Override
    public List<Issue> findIssues(int limit) {
        Map<Long, Issue> extRefIds = findIssueCommentsExtRefs(limit);
        return findMongoIssueDetails(extRefIds);
    }

    public List<Issue> findIssuesByProjectId(Long projectId) {
        return findIssuesByProjectId(projectId, 0);
    }

    public List<Issue> findIssuesByProjectId(Long projectId, int limit) {
        Map<Long, Issue> extRefIds =
                findIssueCommentsExtRefsByProjectId(projectId, limit);
        return findMongoIssueDetails(extRefIds);
    }

    @Autowired
    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Autowired
    public void setMongoOperations(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    private String buildCommitsStringQuery(int limit) {
        return buildStringQuery(commitsBaseColumns, commitsBaseTables,
                commitsBaseConditions, limit);
    }

    private String buildIssuesStringQuery(int limit) {
        return buildStringQuery(
                Arrays.asList(new String[]{
                        "ic.ext_ref_id",
                        "i.ext_ref_id as issueExtRefId",
                        "ic.issue_id",
                        "i.created_at as createdAt"}),
                Arrays.asList(new String[]{"issue_comments ic join issues i on i.id = ic.issue_id"}),
                new ArrayList<String>(), limit);
    }

    private String buildIssuesByProjectIdStringQuery(Long projectId, int limit) {
        return buildStringQuery(
                Arrays.asList(new String[]{
                        "ic.ext_ref_id",
                        "i.ext_ref_id as issueExtRefId",
                        "ic.issue_id",
                        "i.created_at as createdAt"}),
                Arrays.asList(new String[]{"issue_comments ic join issues i on i.id = ic.issue_id"}),
                Arrays.asList(new String[]{"i.repo_id = ?"}),
                limit);
    }

    private String buildStringQuery(List<String> columns,
                                    List<String> tables, List<String> conditions, int limit) {
        String queryColumns = StringUtils
                .collectionToCommaDelimitedString(columns);
        String queryTables = StringUtils
                .collectionToCommaDelimitedString(tables);
        String queryConditions = StringUtils
                .collectionToDelimitedString(conditions, " and ");

        StringBuilder queryResultStringBuilder = new StringBuilder();
        String result = queryResultStringBuilder.append("select ")
                .append(queryColumns).append(" from ")
                .append(queryTables)
                .append(conditions.isEmpty() ? "" : " where ")
                .append(queryConditions)
                .append(limit == 0 ? "" : " limit ?").toString();

        return result;
    }

    private List<Commit> findCommitsByProjectIdLogic(long projectId,
                                                     int limit) {
        List<String> columns = new ArrayList<>(commitsBaseColumns);
        // columns.add("projects.id as projectId");
        // columns.add("projects.description as projectDescription");
        List<String> tables = new ArrayList<>(commitsBaseTables);
        // tables.add("projects");
        List<String> conditions = new ArrayList<>(commitsBaseConditions);
        // conditions.add("commits.project_id = projects.id");
        conditions.add("projects.id = ?");
        String queryString = buildStringQuery(columns, tables,
                conditions, limit);
        if (limit == 0) {
            return new ArrayList<>(jdbcOperations.query(
                    queryString, new Object[]{projectId},
                    new CommitsResultSetExtractor(true)).values());
        } else {
            return new ArrayList<>(jdbcOperations.query(
                    queryString, new Object[]{projectId, limit},
                    new CommitsResultSetExtractor(true)).values());
        }
    }

    private List<Commit> findCommitsLogic(int limit) {

        List<Commit> result = new ArrayList<>();

        String query = buildCommitsStringQuery(limit);
        if (limit == 0) {
            result = new ArrayList<>(jdbcOperations.query(query,
                    new CommitsResultSetExtractor(true)).values());
        } else {
            result = new ArrayList<>(jdbcOperations.query(query,
                    new Object[]{limit},
                    new CommitsResultSetExtractor(true)).values());
        }

        return result;
    }

    private String findIssueBody(String extRefId) {
        IssueBodyExtractor bodyExtractor = new IssueBodyExtractor();
        mongoOperations.executeQuery(new Query(Criteria.where("_id")
                .is(new ObjectId(extRefId))), "issues", bodyExtractor);
        return bodyExtractor.getResultString();
    }

    private Map<Long, Issue> findIssueCommentsExtRefs(int limit) {
        Map<Long, Issue> resultMap = new HashMap<>();
        String queryString = buildIssuesStringQuery(limit);
        if (limit != 0) {
            jdbcOperations.query(queryString,
                    new Object[]{limit},
                    new IssueRowSqlCallbackHandler(resultMap));
        } else {
            jdbcOperations.query(queryString,
                    new IssueRowSqlCallbackHandler(resultMap));
        }

        return resultMap;
    }

    private Map<Long, Issue> findIssueCommentsExtRefsByProjectId(Long projectId,
                                                                 int limit) {
        Map<Long, Issue> resultMap = new HashMap<>();
        String queryString = buildIssuesByProjectIdStringQuery(projectId,
                limit);
        if (limit != 0) {
            jdbcOperations.query(queryString,
                    new Object[]{projectId, limit},
                    new IssueRowSqlCallbackHandler(resultMap));
        } else {
            jdbcOperations.query(queryString,
                    new Object[]{projectId},
                    new IssueRowSqlCallbackHandler(resultMap));
        }

        return resultMap;
    }

    private List<Issue> findMongoIssueDetails(Map<Long, Issue> issues) {
        List<Issue> result = new ArrayList<>();
        for (Entry<Long, Issue> entry : issues.entrySet()) {
            List<ObjectId> extRefObjectIds = new ArrayList<>();
            for (String extRefString : entry.getValue()
                    .getCommentExtRefIdsList()) {
                extRefObjectIds.add(new ObjectId(extRefString));
            }
            Query query = new Query(Criteria.where("_id").in(
                    extRefObjectIds));
            Issue issue = entry.getValue();
            issue.setBody(findIssueBody(issue.getExtRefId()));
            mongoOperations.executeQuery(query, "issue_comments",
                    new IssueCommentCallbackHandler(issue));
            result.add(issue);
        }
        return result;
    }

    private class CommitsResultSetExtractor implements
            ResultSetExtractor<Map<Long, Commit>> {
        private final boolean buildProject;
        private final Map<Long, Commit> commitsMap;
        private final Map<Long, Project> projectsMap;

        /**
         * @param buildProjectModel
         */
        public CommitsResultSetExtractor(boolean buildProjectModel) {
            super();
            buildProject = buildProjectModel;
            projectsMap = new HashMap<>();
            commitsMap = new HashMap<>();
        }

        @Override
        public Map<Long, Commit> extractData(ResultSet rs)
                throws SQLException, DataAccessException {
            while (rs.next()) {
                if (buildProject) {
                    Commit commit = mapCommit(rs);
                    Project project = mapProject(rs, commit);
                    commit.setParentProject(project);
                } else {
                    mapCommit(rs);
                }
            }
            return commitsMap;
        }

        private User mapAuthor(ResultSet resultSet) throws SQLException {
            long authorId = resultSet.getLong("authorUserId");
            String company = resultSet
                    .getString("authorUserCompany");
            String location = resultSet
                    .getString("authorUserLocation");
            String name = resultSet.getString("authorUserName");
            return new User.Builder().withId(authorId)
                    .withCompany(company).withLocation(location)
                    .withName(name).build();
        }

        private Commit mapCommit(ResultSet resultSet)
                throws SQLException {
            long id = resultSet.getLong("commit_id");
            String comment = resultSet.getString("body");
            Date createdAt = resultSet.getDate("created_at");

            Commit commit;

            if (commitsMap.containsKey(id)) {
                commitsMap.get(id).getComments().add(comment);
                commit = commitsMap.get(id);
            } else {
                List<String> newComments = new ArrayList<>();
                newComments.add(comment);
                User commiter = mapCommiter(resultSet);
                User author = mapAuthor(resultSet);
                commit = new Commit.Builder().withId(id)
                        .withCreatedAt(createdAt)
                        .withComments(newComments)
                        .withCommiter(commiter)
                        .withAuthor(author).build();
                commitsMap.put(id, commit);
            }
            return commit;
        }

        private User mapCommiter(ResultSet resultSet)
                throws SQLException {
            long commiterId = resultSet.getLong("commiterUserId");
            String company = resultSet
                    .getString("commiterUserCompany");
            String location = resultSet
                    .getString("commiterUserLocation");
            String name = resultSet.getString("commiterUserName");
            return new User.Builder().withId(commiterId)
                    .withCompany(company).withLocation(location)
                    .withName(name).build();
        }

        private Project mapProject(ResultSet resultSet,
                                   Commit childCommit) throws SQLException {
            long projectId = resultSet.getLong("projectId");
            String description = resultSet
                    .getString("projectDescription");
            String language = resultSet.getString("language");
            Project project;
            if (projectsMap.containsKey(projectId)) {
                projectsMap.get(projectId).getCommits()
                        .add(childCommit);
                project = projectsMap.get(projectId);
            } else {
                List<Commit> commits = new ArrayList<>();
                commits.add(childCommit);
                project = new Project.Builder()
                        .withId(projectId)
                        .withDescription(description)
                        .withCommits(commits)
                        .withLanguage(language).build();
                projectsMap.put(projectId, project);
            }
            return project;
        }
    }

    private class IssueBodyExtractor implements DocumentCallbackHandler {
        private String resultString;

        public String getResultString() {
            return resultString;
        }

        @Override
        public void processDocument(DBObject dbObject)
                throws MongoException, DataAccessException {
            resultString = (String) dbObject.get("body");
        }

    }

    private class IssueCommentCallbackHandler implements
            DocumentCallbackHandler {
        private final Issue issue;

        public IssueCommentCallbackHandler(Issue issue) {
            super();
            this.issue = issue;
        }

        @Override
        public void processDocument(DBObject dbObject)
                throws MongoException, DataAccessException {
            issue.addComment((String) dbObject.get("body"));
        }

    }

    private class IssueRowSqlCallbackHandler implements RowCallbackHandler {
        private final Map<Long, Issue> result;

        public IssueRowSqlCallbackHandler(Map<Long, Issue> result) {
            super();
            this.result = result;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            long id = rs.getLong("issue_id");
            String issueExtRefId = rs.getString("issueExtRefId");
            String extRefIdCommentString = rs
                    .getString("ext_ref_id");
            Date createdAt = rs.getDate("createdAt");
            if (result.containsKey(id)) {
                result.get(id)
                        .addCommentExtRefId(
                                extRefIdCommentString)
                        .setExtRefId(issueExtRefId);
            } else {
                result.put(
                        id,
                        new Issue()
                                .addCommentExtRefId(
                                        extRefIdCommentString)
                                .setExtRefId(issueExtRefId)
                                .setId(id).setCreatedAt(createdAt));
            }
        }

    }

}
