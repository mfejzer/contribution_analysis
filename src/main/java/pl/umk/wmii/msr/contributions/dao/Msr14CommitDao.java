package pl.umk.wmii.msr.contributions.dao;

import pl.umk.wmii.msr.contributions.model.Commit;

import java.util.List;

/**
 * Msr14 database commits access interface
 */
public interface Msr14CommitDao {
    /**
     * Returns list of all commits.
     *
     * @return commit domain object
     */
    List<Commit> findCommits();

    /**
     * Returns list of all commits.
     *
     * @param limit size limitation
     * @return commit domain object
     * @see Commit
     */
    List<Commit> findCommits(int limit);

    /**
     * Returns list of all commits belonging to specified project
     *
     * @param projectId Id of the project
     * @return list of commits
     */
    List<Commit> findCommitsByProjectId(long projectId);

    /**
     * Returns list of all commits belonging to specified project
     *
     * @param projectId Id of the project
     * @param limit     maximum size of processed records and therefore
     *                  returned collection
     * @return list of commits
     */
    List<Commit> findCommitsByProjectId(long projectId, int limit);
}
