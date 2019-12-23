package pl.umk.wmii.msr.contributions.aggergator;

import com.google.common.base.Function;
import pl.umk.wmii.msr.contributions.model.GroupableByDate;

import java.util.Calendar;
import java.util.Date;


/**
 * Groups by week
 */
public class WeeklyDateGrouper<T extends GroupableByDate> implements
        Function<T, Date> {

    @Override
    public Date apply(T created) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime(created.date());
        int week = c.get(Calendar.WEEK_OF_YEAR);
        int year = c.get(Calendar.YEAR);
        c.clear();
        c.set(Calendar.WEEK_OF_YEAR, week);
        c.set(Calendar.YEAR, year);
        return c.getTime();
    }
}