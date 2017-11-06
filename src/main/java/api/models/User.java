package api.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User implements RowMapper<User> {

    private String nickname;
    private String fullname;
    private String email;
    private String about;

    @JsonCreator
    public User(@JsonProperty("nickname")String nickname,
                @JsonProperty("fullname")String fullname,
                @JsonProperty("email")String email,
                @JsonProperty("about")String about) {

        this.nickname = nickname;
        this.fullname = fullname;
        this.email = email;
        this.about = about;
    }

    public User() {}

//    public long getuID() {
//        return uID;
//    }

//    public void setuID(long uID) {
//        this.uID = uID;
//    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        return new User(
                resultSet.getString("nickname"),
                resultSet.getString("fullname"),
                resultSet.getString("email"),
                resultSet.getString("about")
        );
    }

    //    @JsonCreator
//    public User(@JsonProperty("nickname") @Nullable String nickname, @JsonProperty("fullname") String fullname,
//                @JsonProperty("email") String email, @JsonProperty("about") String about) {
//        this.nickname = nickname;
//        this.email = email;
//        this.fullname = fullname;
//        this.about = about;
//    }
}
