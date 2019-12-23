package pl.umk.wmii.msr.contributions.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Issue implements Classifiable, GroupableByDate {
    private String body;
    private Date createdAt;
    private final List<String> commentExtRefIdsList;
    private final List<String> comments;
    private String extRefId;
    private long id;

    public Issue() {
        super();
        comments = new ArrayList<>();
        commentExtRefIdsList = new ArrayList<>();
    }

    private Issue(Builder builder) {
        body = builder.body;
        createdAt = builder.createdAt;
        commentExtRefIdsList = builder.commentExtRefIdsList;
        comments = builder.comments;
        extRefId = builder.extRefId;
        id = builder.id;
    }

    public Issue addComment(String comment) {
        comments.add(comment);
        return this;
    }

    public Issue addCommentExtRefId(String extRefId) {
        commentExtRefIdsList.add(extRefId);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Issue other = (Issue) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    public String getBody() {
        return body;
    }

    public List<String> getCommentExtRefIdsList() {
        return commentExtRefIdsList;
    }

    public List<String> getComments() {
        return comments;
    }

    public String getExtRefId() {
        return extRefId;
    }

    public long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public String label() {
        return null;
    }

    @Override
    public String name() {
        return null;
    }

    public Issue setBody(String body) {
        this.body = body;
        return this;
    }

    public Issue setExtRefId(String extRefId) {
        this.extRefId = extRefId;
        return this;
    }

    public Issue setId(long id) {
        this.id = id;
        return this;
    }

    public Issue setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @Override
    public String source() {
        return null;
    }

    @Override
    public String text() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getBody());
        stringBuilder.append(getComments());
        return stringBuilder.toString();
    }

    @Override
    public Date date() {
        return createdAt;
    }

    public static class Builder {

        private String body;
        private Date createdAt;
        private List<String> commentExtRefIdsList;
        private List<String> comments;
        private String extRefId;
        private long id;

        public Issue build() {
            return new Issue(this);
        }

        public Builder withBody(String body) {
            this.body = body;
            return this;
        }

        public Builder withCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withCommentExtRefIdsList(
                List<String> commentExtRefIdsList) {
            this.commentExtRefIdsList = commentExtRefIdsList;
            return this;
        }

        public Builder withComments(List<String> comments) {
            this.comments = comments;
            return this;
        }

        public Builder withExtRefId(String extRefId) {
            this.extRefId = extRefId;
            return this;
        }

        public Builder withId(long id) {
            this.id = id;
            return this;
        }
    }
}
