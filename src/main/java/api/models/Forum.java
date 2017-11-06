package api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Forum implements RowMapper<Forum>{

    private String slug;
    private String title;
    private String user;
    private long posts;
    private long threads;

    @JsonCreator
    public Forum(@JsonProperty("slug")String slug, @JsonProperty("title")String title, 
                 @JsonProperty("user")String user, @JsonProperty("posts")long posts,
                 @JsonProperty("threads")long threads) {
        this.slug = slug;
        this.title = title;
        this.user = user;
        this.posts = posts;
        this.threads = threads;
    }

    public Forum() {}

    // public long getID() {
    //     return fID;
    // }

    public String getSlug() {
        return slug;
    }

    public String getTitle() { return title; }

    public String getUser() {
        return user;
    }

    public long getPosts() {
        return posts;
    }

    public long getThreads() {
        return threads;
    }

    // public void setID(long id) {
    //     this.fID = id;
    // }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUser(String user) {
        this.user  = user;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public void setThreads(long threads) {
        this.threads = threads;
    }

    @Override
    public Forum mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Forum(
                resultSet.getString("slug"),
                resultSet.getString("title"),
                resultSet.getString("user"),
                resultSet.getLong("posts"),
                resultSet.getLong("threads")
        );
    }

}
