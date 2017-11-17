package api.services;
import api.models.Thread;
import api.models.User;
import api.models.Forum;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import java.util.List;

@Repository
public class ThreadService {
    private JdbcTemplate jdbcTemplate;
    
    public RowMapper<Thread> ThreadList = (rs, rowNum) -> new Thread(
        rs.getLong("id"), rs.getString("forum"), rs.getString("author"),
        rs.getString("slug"), 
        LocalDateTime.ofInstant(rs.getTimestamp("created").toInstant(), ZoneOffset.ofHours(0))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")),
        rs.getString("message"), rs.getString("title"),
        rs.getLong("votes"));

    @Autowired
    public ThreadService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Thread create(String forum, Thread thread, String user) {
        return this.jdbcTemplate.queryForObject(
                "INSERT INTO threads (forum, author, slug, created, message, title) VALUES (?, ?, ?, COALESCE(?::TIMESTAMPTZ, current_timestamp), ?, ?) RETURNING *",
                ThreadList, 
                forum, user, thread.getSlug(), thread.getCreated(),   
                thread.getMessage(), thread.getTitle()
        );
    }

    public Thread getThreadBySlugOrId(String slug_or_id) {
        if(slug_or_id.matches("\\d+")) {
            return this.jdbcTemplate.queryForObject(
                "SELECT t.id, t.slug, t.author, t.forum, t.created, t.message, t.title, t.votes FROM threads t WHERE t.id = (?)",
                ThreadList, 
                Long.valueOf(slug_or_id)
            );
        } else {
            return this.jdbcTemplate.queryForObject(
                "SELECT t.id, t.slug, t.author, t.forum, t.created, t.message, t.title, t.votes FROM threads t WHERE lower(t.slug) = lower(?)",
                ThreadList, 
                slug_or_id
            );
        }
    }

    public List<Thread> getThreads(String slug, boolean desc, Integer limit, String since) {
        StringBuilder mquery = new StringBuilder();
        mquery.append("SELECT t.id, t.slug, t.author, t.forum, t.created, t.message, t.title, t.votes FROM threads t WHERE lower(t.forum) = lower(?) ");

        

        if(since != null) {
            if(desc) {
                mquery.append("AND t.created <= '").append(since).append("'::TIMESTAMPTZ ");
            } else if (!desc) {
                mquery.append("AND t.created >= '").append(since).append("'::TIMESTAMPTZ ");
            }
        }

        if(desc) {
            mquery.append("ORDER BY t.created DESC ");
        } else if(!desc) {
            mquery.append("ORDER BY t.created ASC ");           
        }

        mquery.append("LIMIT (?)");
        

        return this.jdbcTemplate.query(mquery.toString(), ThreadList, slug, limit);
    }
}
