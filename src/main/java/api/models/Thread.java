package api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Thread {

    private Long id;
    private String forum;
    private String author;
    private String slug;
    private String created;
    private String message;
    private String title;
    private long votes;

    @JsonCreator
    public Thread(@JsonProperty("id") Long id, @JsonProperty("forum") String forum, @JsonProperty("author") String author, 
                  @JsonProperty("slug")String slug, @JsonProperty("created")String created, 
                  @JsonProperty("message")String message, 
                  @JsonProperty("title")String title, @JsonProperty("votes")long votes) {
    
      
      this.id = id;              
      this.forum = forum;
      this.author = author;
      this.slug = slug;
      this.created = created;
      this.message = message;
      this.title = title;
      this.votes = votes;

    }

    public Thread() {}

    public Long getid() {
        return id;
    }

    public void setid(Long id) {
        this.id = id;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getVotes() {
        return votes;
    }

    public void setVotes(long votes) {
        this.votes = votes;
    }
}
