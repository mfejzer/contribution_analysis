package pl.umk.wmii.msr.contributions.aggergator;

import com.google.common.base.Function;
import pl.umk.wmii.msr.contributions.model.Commit;
import pl.umk.wmii.msr.contributions.model.User;

/**
 * Groups commits by user id, user is extracted via userGetter
 */
public class UserIdCommitGrouper implements Function<Commit, Long> {

    public UserIdCommitGrouper(Function<Commit, User> userGetter) {
        this.userGetter = userGetter;
    }

    private Function<Commit, User> userGetter;

    @Override
    public Long apply(Commit commit) {
        return Long.valueOf(userGetter.apply(commit).getId());
    }
}
