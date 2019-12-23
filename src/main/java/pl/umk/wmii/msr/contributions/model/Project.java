package pl.umk.wmii.msr.contributions.model;

import java.util.List;

/**
 * Project data model
 */
public class Project {
    private final List<Commit> commits;
    private final String description;
    private final long id;
    private final String language;

    private Project(Builder builder) {
        id = builder.id;
        description = builder.description;
        commits = builder.commits;
        language = builder.language;
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
        Project other = (Project) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Project [description=" + description + ", id=" + id
                + ", language=" + language + "]";
    }

    public static class Builder {

        private List<Commit> commits;
        private String description;
        private long id;
        private String language;

        public Project build() {
            return new Project(this);
        }

        public Builder withCommits(List<Commit> commits) {
            this.commits = commits;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withId(long id) {
            this.id = id;
            return this;
        }

        public Builder withLanguage(String language) {
            this.language = language;
            return this;
        }
    }

}
