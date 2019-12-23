package pl.umk.wmii.msr.contributions.dao;

import pl.umk.wmii.msr.contributions.model.Issue;

import java.util.List;

/**
 * Msr14 database issues access interface
 */
public interface Msr14IssueDao {
    /**
     * Returns list of all issues.
     *
     * @return issue domain object
     * @see Issue
     */
    List<Issue> findIssues();

    /**
     * Returns list of all issues.
     *
     * @param limit size limitation
     * @return issue domain object
     * @see Issue
     */
    List<Issue> findIssues(int limit);

    List<Issue> findIssuesByProjectId(Long projectId);

    List<Issue> findIssuesByProjectId(Long projectId, int limit);
}
