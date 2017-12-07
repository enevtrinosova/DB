package api.services;

import api.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class UserService {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public RowMapper<User> UserList = (rs, rowNum) -> new User(
                rs.getString("nickname"), rs.getString("fullname"),
                rs.getString("email"), rs.getString("about"));

    public User create(User user) {
            this.jdbcTemplate.update(
                    "INSERT INTO users (nickname, fullname, email, about) VALUES (?, ? ,? , ? )",
                    user.getNickname(), user.getFullname(), user.getEmail(), user.getAbout()
            );
            return user;
    }

    public User getInf(String nickname) {
        return this.jdbcTemplate.queryForObject(
                "SELECT nickname, fullname, email, about FROM users AS u " +
                        "WHERE lower(u.nickname) = lower(?) LIMIT 1",
                new User(),
                nickname
        );
    }


    public User setInf(String nickname, User user) {
        String sql = "UPDATE users SET " +
                "fullname = COALESCE(?, fullname)," +
                "email = COALESCE(?, email)," +
                "about = COALESCE(?, about) " +
                "WHERE lower(nickname) = lower(?) RETURNING *";

        return this.jdbcTemplate.queryForObject(sql, UserList,
                user.getFullname(), user.getEmail(), user.getAbout(), nickname);

    }


    public List<User> findSimilarUsers(String nickname, String email) {
        String result = "SELECT nickname, fullname, email, about FROM users AS u " +
                "WHERE lower(u.nickname) = lower(?) OR lower(u.email) = lower(?)";
        return jdbcTemplate.query(result, UserList, nickname, email);

    }

}
