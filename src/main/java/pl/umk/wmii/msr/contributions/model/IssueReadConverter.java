package pl.umk.wmii.msr.contributions.model;

import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;

public class IssueReadConverter implements Converter<DBObject, Issue> {

    @Override
    public Issue convert(DBObject source) {
        String body = (String) source.get("body");
        return new Issue().setBody(body);
    }

}
