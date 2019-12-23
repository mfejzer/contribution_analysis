package pl.umk.wmii.msr.contributions.model;

import java.util.Date;
import java.util.List;

/**
 * Commit classifiable data model
 *
 */
public class Commit implements Classifiable, GroupableByDate {
    private final User author;
    private final List<String> comments;
    private final User commiter;
    private final Date createdAt;
    private final long id;
    private Project parentProject;

    private Commit(Builder builder) {
        author = builder.author;
        comments = builder.comments;
        commiter = builder.commiter;
        createdAt = builder.createdAt;
        id = builder.id;
        parentProject = builder.parentProject;
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
        Commit other = (Commit) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    public User getAuthor() {
        return author;
    }

    public List<String> getComments() {
        return comments;
    }

    public User getCommiter() {
        return commiter;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date date() {
        return getCreatedAt();
    }

    public long getId() {
        return id;
    }

    public Project getParentProject() {
        return parentProject;
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

    public void setParentProject(Project parentProject) {
        this.parentProject = parentProject;
    }

    @Override
    public String source() {
        return null;
    }

    @Override
    public String text() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getComments());
        return stringBuilder.toString();
    }

    public static class Builder {

        private User author;
        private List<String> comments;
        private User commiter;
        private Date createdAt;
        private long id;
        private Project parentProject;

        public Commit build() {
            return new Commit(this);
        }

        public Builder withAuthor(User author) {
            this.author = author;
            return this;
        }

        public Builder withComments(List<String> comments) {
            this.comments = comments;
            return this;
        }

        public Builder withCommiter(User commiter) {
            this.commiter = commiter;
            return this;
        }

        public Builder withCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withId(long id) {
            this.id = id;
            return this;
        }

        public Builder withParentProject(Project parentProject) {
            this.parentProject = parentProject;
            return this;
        }
    }

}
