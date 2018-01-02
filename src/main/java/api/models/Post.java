package api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;


public class Post implements RowMapper<Post>{

    private long id;
    private String author;
    private String forum;
    private String created;
    private boolean isEddited;
    private long thread;
    private String message;
    private Long parent;
    // public List<Long> path;
   
    // @SuppressWarnings("unchecked")
    @JsonCreator
    public <T> Post(@JsonProperty("id")long id, @JsonProperty("author")String author, 
                @JsonProperty("forum")String forum, @JsonProperty("created")String created, 
                @JsonProperty("isEddited")boolean isEddited,
                @JsonProperty("thread")long thread, @JsonProperty("message")String message, 
                @JsonProperty("parent")Long parent) {
        this.id = id;
        this.author = author;
        this.forum = forum;
        this.created = created;
        this.isEddited = isEddited;
        this.thread = thread;
        this.message = message;
        this.parent = parent;

        // this.path = (List<Long>)path;
        
}

    public Post() {}

    public long getid() {
        return id;
    }

    public void setid(long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public boolean getIsEdited() {
        return isEddited;
    }

    public void setIsEdited(boolean eddited) {
        isEddited = eddited;
    }

    public long getThread() {
        return thread;
    }

    public void setThread(long thread) {
        this.thread = thread;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    // public List<Long> getPath() {
    //     return path;
    // }
    // public void setPath(List<Long> path) {
    //     this.path = path;
    // }

 
    @Override
    public Post mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Post(
                resultSet.getLong("id"),
                resultSet.getString("author"),
                resultSet.getString("forum"),
                resultSet.getString("created"),
                resultSet.getBoolean("isEddited"),
                resultSet.getLong("thread"),
                resultSet.getString("message"),
                resultSet.getLong("parent")
                
                // Arrays.asList(resultSet.getArray("path"))
        );
    }
}
