package pl.umk.wmii.msr.contributions.model;

import pl.umk.wmii.msr.contributions.aggergator.Aggregator;

import java.util.Date;

/**
 * Implementing shall enable grouping by date via Aggregator
 *
 * @see Aggregator
 */
public interface GroupableByDate {
    Date date();
}
