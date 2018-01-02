package api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/service")
public class ServiceControllers {
    final JdbcTemplate jdbcTemplate;

    public ServiceControllers(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(
                new Object() {
                    public final int forum = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM forums;", Integer.class);
                    public final int thread = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM threads;", Integer.class);
                    public final int user = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users;", Integer.class);
                    public final int post = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM posts;", Integer.class);
                }
        );
    }


    @PostMapping("/clear")
    public ResponseEntity<?> clear() {
        jdbcTemplate.update("TRUNCATE TABLE posts CASCADE ");
        jdbcTemplate.update("TRUNCATE TABLE threads CASCADE ");
        jdbcTemplate.update("TRUNCATE TABLE forums CASCADE ");
        jdbcTemplate.update("TRUNCATE TABLE users CASCADE ");
        jdbcTemplate.update("TRUNCATE TABLE votes CASCADE ");
        jdbcTemplate.update("TRUNCATE TABLE forum_members CASCADE ");
        return ResponseEntity.ok("");
    }

}
