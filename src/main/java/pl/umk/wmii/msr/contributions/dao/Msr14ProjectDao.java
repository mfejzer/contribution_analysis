package pl.umk.wmii.msr.contributions.dao;

import pl.umk.wmii.msr.contributions.model.Project;

import java.util.List;

/**
 * Project DAO
 *
 */
public interface Msr14ProjectDao {
    /**
     * Find all projects which are not forks
     *
     * @return list of non-forked projects
     */
    List<Project> findRootProjects();
}
