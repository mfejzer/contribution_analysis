package pl.umk.wmii.msr.contributions.extractor;

import com.google.common.base.Function;
import pl.umk.wmii.msr.contributions.model.Commit;
import pl.umk.wmii.msr.contributions.model.CorrelationPair;
import pl.umk.wmii.msr.contributions.model.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * For each Commit, extracts ids of issues from commit body,
 * and creates CorrelationPair if such issue exists in idToIssueRetrievalMap
 */
public class CommitToCommitIssueListTransformer implements Function<Commit, Iterable<CorrelationPair<Commit, Issue>>> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CommitToCommitIssueListTransformer.class);

    private Map<Long, Issue> idToIssueRetrievalMap;

    public CommitToCommitIssueListTransformer(Map<Long, Issue> idToIssueRetrievalMap) {
        this.idToIssueRetrievalMap = idToIssueRetrievalMap;
    }

    @Override
    public Iterable<CorrelationPair<Commit, Issue>> apply(Commit commit) {
        Pattern numberInCommentPattern = Pattern.compile("\\d+");

        Set<Long> possibleIssueIds = new HashSet<>();
        for (String comment : commit.getComments()) {
            Matcher matcher = numberInCommentPattern.matcher(comment);
            while (matcher.find()) {
                String possibleId = matcher.group();
                Long id = null;
                try {
                    id = Long.valueOf(possibleId);
                } catch (NumberFormatException nfe) {
                    LOGGER.debug(nfe.getLocalizedMessage(), nfe);
                }
                if (id != null) {
                    possibleIssueIds.add(id);
                }
            }
        }

        List<CorrelationPair<Commit, Issue>> result = new ArrayList<>();
        for (Long issueId : possibleIssueIds) {
            if (idToIssueRetrievalMap.containsKey(issueId)) {
                Issue issue = idToIssueRetrievalMap.get(issueId);
                result.add(new CorrelationPair(commit, issue));
            }
        }
        return result;
    }

}