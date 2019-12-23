package pl.umk.wmii.msr.contributions.aggergator;

import com.google.common.base.Function;
import pl.umk.wmii.msr.contributions.model.Commit;
import pl.umk.wmii.msr.contributions.model.User;

/**
 * Extracts commiter of commit
 */
public class CommitCommiter implements Function<Commit, User> {
    @Override
    public User apply(Commit commit) {
        return commit.getCommiter();
    }
}