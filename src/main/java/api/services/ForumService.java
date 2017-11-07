package api.services;

import api.models.Forum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


@Repository
public class ForumService {
    private JdbcTemplate jdbcTemplate;

    public RowMapper<Forum> ForumList = (rs, rowNum) -> new Forum(
        rs.getString("slug"), rs.getString("title"),
        rs.getString("user"), rs.getLong("posts"), rs.getLong("threads"));

    @Autowired
    public ForumService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void create(Forum forum, String user) {
        this.jdbcTemplate.update(
                "INSERT INTO forums (slug, title, \"user\") VALUES (?, ? ,?)",
                forum.getSlug(), forum.getTitle(), user
        );
    }

    public Forum getInf(String slug) {
        return this.jdbcTemplate.queryForObject(
                    "SELECT slug, title, \"user\", posts, threads FROM forums AS f " +
                    "WHERE lower(f.slug) = lower(?) LIMIT 1",
            new Forum(),
            slug
    );
    }

    public String getSlug(String slug) {
        return this.jdbcTemplate.queryForObject(
                    "SELECT f.slug FROM forums f WHERE lower(f.slug) = lower(?)",
                    String.class,
                    slug
        );
    }

}
