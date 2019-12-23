package pl.umk.wmii.msr.contributions.aggergator;

import com.google.common.base.Function;
import pl.umk.wmii.msr.contributions.model.Commit;
import pl.umk.wmii.msr.contributions.model.User;

/**
 * Groups commits by user location, user is extracted via userGetter
 * Users with null location get "" as group
 */
public class UserLocationCommitGrouper implements
        Function<Commit, String> {

    public UserLocationCommitGrouper(Function<Commit, User> userGetter) {
        this.userGetter = userGetter;
    }

    private Function<Commit, User> userGetter;

    @Override
    public String apply(Commit commit) {
        String location = userGetter.apply(commit).getLocation();
        return location == null ? "" : location;
    }
}
