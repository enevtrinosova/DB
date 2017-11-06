package api.models;

/**
 * Created by evgenia on 14.10.17.
 */
public class Post {

    private long pID;
    private String author;
    private String forum;
    private String created;
    private boolean isEddited;
    private long thread;
    private String message;
    private int parent;

    public Post(long pID, String author, String forum, String created, boolean isEddited,
                        long thread, String message, int parent) {
        this.pID = pID;
        this.author = author;
        this.forum = forum;
        this.created = created;
        this.isEddited = isEddited;
        this.thread = thread;
        this.message = message;
        this.parent = parent;
    }

    public long getpID() {
        return pID;
    }

    public void setpID(long pID) {
        this.pID = pID;
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

    public boolean isEddited() {
        return isEddited;
    }

    public void setEddited(boolean eddited) {
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

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }
}
