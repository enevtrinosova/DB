package api.models;

/**
 * Created by evgenia on 14.10.17.
 */
public class Vote {

    private long vID;
    private String user;
    private long thread;
    private int voice;

    public Vote(long vID, String user, long thread, int voice) {
        this.vID = vID;
        this.user = user;
        this.thread = thread;
        this.voice = voice;
    }

    public long getvID() {
        return vID;
    }

    public void setvID(long vID) {
        this.vID = vID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getThread() {
        return thread;
    }

    public void setThread(long thread) {
        this.thread = thread;
    }

    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }
}
