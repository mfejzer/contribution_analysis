package pl.umk.wmii.msr.contributions.aggergator;

import com.google.common.base.Function;
import pl.umk.wmii.msr.contributions.model.Commit;
import pl.umk.wmii.msr.contributions.model.User;

/**
 * Groups commits by user company, user is extracted via userGetter
 * Users with null company get "" as group
 */
public class UserCompanyCommitGrouper implements
        Function<Commit, String> {
    private Function<Commit, User> userGetter;

    public UserCompanyCommitGrouper(Function<Commit, User> userGetter) {
        this.userGetter = userGetter;
    }

    @Override
    public String apply(Commit commit) {
        String company = userGetter.apply(commit).getCompany();
        return company == null ? "" : company;
    }
}
