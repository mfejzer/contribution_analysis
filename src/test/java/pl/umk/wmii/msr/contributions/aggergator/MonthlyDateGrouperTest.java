package pl.umk.wmii.msr.contributions.aggergator;

import pl.umk.wmii.msr.contributions.model.GroupableByDate;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class MonthlyDateGrouperTest {

    @Test
    public void shouldReturnStartOfMonthGivenDate() {
        //given
        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        calendar.set(2013, 0, 4);
        final Date forthOfFebruary13 = calendar.getTime();

        GroupableByDate groupableByDate = new GroupableByDate() {

            @Override
            public Date date() {
                return forthOfFebruary13;
            }
        };

        MonthlyDateGrouper grouper = new MonthlyDateGrouper();

        //when
        Date result = grouper.apply(groupableByDate);

        //then
        calendar.set(2013, 0, 1, 0, 0, 0);
        Date expectedDate = calendar.getTime();

        assertEquals(expectedDate, result);
    }
}
