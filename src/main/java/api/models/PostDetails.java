package api.models;

public class PostDetails {
    private Forum forum;
    private Post post;
    private Thread thread;
    private User author;

    public PostDetails() {
    }

    public PostDetails(Forum forum, Post post, Thread thread, User author) {
        this.forum = forum;
        this.post = post;
        this.thread = thread;
        this.author = author;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
