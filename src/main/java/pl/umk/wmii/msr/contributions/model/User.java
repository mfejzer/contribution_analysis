package pl.umk.wmii.msr.contributions.model;

public class User {
    private final String company;
    private final long id;
    private final String location;
    private final String name;

    private User(Builder builder) {
        name = builder.name;
        company = builder.company;
        location = builder.location;

        id = builder.id;
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
        User other = (User) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    public String getCompany() {
        return company;
    }

    public long getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
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
        return "User [company=" + company + ", id=" + id
                + ", location=" + location + ", name=" + name + "]";
    }

    public static class Builder {

        private String company;
        private long id;
        private String location;
        private String name;

        public User build() {
            return new User(this);
        }

        public Builder withCompany(String company) {
            this.company = company;
            return this;
        }

        public Builder withId(long id) {
            this.id = id;
            return this;
        }

        public Builder withLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }
    }

}
