package api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Vote {

    // private long id;
    private String nickname;
    private String thread;
    private int voice;

    @JsonCreator
    public Vote(@JsonProperty("nickname")String nickname,@JsonProperty("thread") String thread,
                @JsonProperty("voice") int voice) {
        // this.id = id;
        this.nickname = nickname;
        this.thread = thread;
        this.voice = voice;
    }

    // public long getid() {
    //     return id;
    // }

    // public void setid(long id) {
    //     this.id = id;
    // }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }
}
