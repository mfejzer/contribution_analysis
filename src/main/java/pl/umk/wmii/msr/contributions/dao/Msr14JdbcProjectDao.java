package pl.umk.wmii.msr.contributions.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import pl.umk.wmii.msr.contributions.model.Project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Jdbc simple implementation
 */
@Service("msr14JdbcProjectDao")
public class Msr14JdbcProjectDao implements Msr14ProjectDao {
    private JdbcOperations jdbcOperations;


    /**
     * This implementation actually maps only id field in project data model
     */
    @Override
    public List<Project> findRootProjects() {
        return jdbcOperations.query(
                "select * from projects where forked_from is null",
                new Object[]{}, new RowMapper<Project>() {

                    @Override
                    public Project mapRow(ResultSet arg0, int arg1)
                            throws SQLException {
                        return new Project.Builder().withId(
                                arg0.getLong("id")).build();
                    }
                });
    }

    @Autowired
    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

}
