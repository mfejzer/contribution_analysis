package pl.umk.wmii.msr.contributions.utils;

import com.google.common.base.Function;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Returns year and month of given date as string
 */
public class DateMonthCategoryLabelGenerator implements Function<Date, String> {
    @Override
    public String apply(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(date);
    }
}
