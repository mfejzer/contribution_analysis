package pl.umk.wmii.msr.contributions.aggergator;

import com.google.common.base.Function;
import pl.umk.wmii.msr.contributions.model.Commit;

/**
 * Groups commits by project language
 * Projects with null language get "" as group
 */
public class ProjectLanguageCommitGrouper implements Function<Commit, String> {
    @Override
    public String apply(Commit commit) {
        String language = commit.getParentProject().getLanguage();
        return language == null ? "" : language;
    }
}
