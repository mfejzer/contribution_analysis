package pl.umk.wmii.msr.contributions.aggergator;


import com.google.common.base.Function;
import pl.umk.wmii.msr.contributions.model.GroupableByDate;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Groups by month
 */
public class MonthlyDateGrouper<T extends GroupableByDate> implements
        Function<T, Date> {

    @Override
    public Date apply(T created) {
        return DateUtils.truncate(created.date(), Calendar.MONTH);
    }
}
