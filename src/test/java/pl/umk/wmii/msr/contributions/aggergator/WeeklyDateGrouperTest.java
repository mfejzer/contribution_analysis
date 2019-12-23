package pl.umk.wmii.msr.contributions.aggergator;

import pl.umk.wmii.msr.contributions.model.GroupableByDate;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class WeeklyDateGrouperTest {

    @Ignore
    @Test
    public void shouldReturnStartOfWeekGivenDate() {
        //given
        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        calendar.set(2013, 0, 4, 0, 0, 0);
        final Date forthOfFebruary13 = calendar.getTime();

        GroupableByDate groupableByDate = new GroupableByDate() {

            @Override
            public Date date() {
                return forthOfFebruary13;
            }
        };

        WeeklyDateGrouper grouper = new WeeklyDateGrouper();

        //when
        Date result = grouper.apply(groupableByDate);

        //then
        calendar.set(2012, 11, 31, 0, 0, 0);
        Date expectedDate = calendar.getTime();

        assertEquals(expectedDate, result);
    }

}
