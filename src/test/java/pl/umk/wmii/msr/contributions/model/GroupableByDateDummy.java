package pl.umk.wmii.msr.contributions.model;

import java.util.Date;

public class GroupableByDateDummy extends Dummy implements GroupableByDate, Classifiable {
    private Date date;

    public GroupableByDateDummy(String text, String label, String source,
                                String name, Date date) {
        super(text, label, source, name);
        this.date = date;
    }

    @Override
    public Date date() {
        return date;
    }

}
