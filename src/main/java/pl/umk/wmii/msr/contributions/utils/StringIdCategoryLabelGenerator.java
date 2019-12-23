package pl.umk.wmii.msr.contributions.utils;

import com.google.common.base.Function;

/**
 * Returns given string
 */
public class StringIdCategoryLabelGenerator implements Function<String, String> {
    @Override
    public String apply(String string) {
        return string;
    }
}
