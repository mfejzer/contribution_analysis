package pl.umk.wmii.msr.contributions.aggergator;

import com.google.common.base.Function;
import pl.umk.wmii.msr.contributions.model.Commit;
import pl.umk.wmii.msr.contributions.model.User;


/**
 * Extracts author of commit
 */
public class CommitAuthor implements Function<Commit, User> {
    @Override
    public User apply(Commit commit) {
        return commit.getAuthor();
    }
}
