package pl.umk.wmii.msr.contributions.aggergator;

import com.google.common.base.Function;
import pl.umk.wmii.msr.contributions.model.Commit;
import pl.umk.wmii.msr.contributions.model.User;

/**
 * Groups commits by user name, user is extracted via userGetter
 */
public class UserNameCommitGrouper implements
        Function<Commit, String> {

    public UserNameCommitGrouper(Function<Commit, User> userGetter) {
        this.userGetter = userGetter;
    }

    private Function<Commit, User> userGetter;

    @Override
    public String apply(Commit commit) {
        return userGetter.apply(commit).getName();
    }
}
