package api.services;
import api.models.Thread;
import api.models.User;
import api.models.Forum;
import api.models.Vote;
import api.services.UserService;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import java.util.List;

@Repository
public class ThreadService {
    private JdbcTemplate jdbcTemplate;

    private UserService userService;
    
    public RowMapper<Thread> ThreadList = (rs, rowNum) -> new Thread(
        rs.getLong("id"), rs.getString("forum"), rs.getString("author"),
        rs.getString("slug"), 
        LocalDateTime.ofInstant(rs.getTimestamp("created").toInstant(), ZoneOffset.ofHours(0))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")),
        rs.getString("message"), rs.getString("title"),
        rs.getLong("votes"));

    public RowMapper<Vote> VoteList = (rs, rowNum) -> new Vote(
        rs.getString("nickname"), rs.getString("thread"), rs.getInt("voice"));
           

    @Autowired
    public ThreadService(JdbcTemplate jdbcTemplate, UserService userService) {
        this.jdbcTemplate = jdbcTemplate;
        this.userService = userService;
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

    public String getThreadSlug(String slug) {
                return this.jdbcTemplate.queryForObject(
                    "SELECT t.slug FROM threads t WHERE lower(t.slug) = lower(?)",
                    String.class,
                    slug
        );
    }

    public Thread setThreadVote(Vote vote, String slug_or_id) {
        Thread findThread = this.getThreadBySlugOrId(slug_or_id);
        
        User user = userService.getInf(vote.getNickname());
        if (findThread == null || user == null) {
            return null;
        }
    
        
        try {
            String voice = "SELECT * FROM votes v WHERE lower(v.thread) = lower(?) AND lower(v.nickname) = lower(?)";
            Vote searchVote = this.jdbcTemplate.queryForObject(voice, VoteList, findThread.getSlug(), vote.getNickname()); 
            String updateSql = "UPDATE votes SET voice = (?) WHERE nickname = (?) AND thread = (?)";

            
            this.jdbcTemplate.update(updateSql, vote.getVoice(), vote.getNickname(), findThread.getSlug());


            String returnSql = "UPDATE threads SET votes = (SELECT SUM(voice) FROM votes WHERE thread = (?)) WHERE slug = (?) RETURNING *";
            
            return this.jdbcTemplate.queryForObject(returnSql, ThreadList, findThread.getSlug(), findThread.getSlug());
        } catch (EmptyResultDataAccessException err) {


            String updateSql = "INSERT INTO votes (nickname, thread, voice)" +
            "VALUES (?, ?, ?)";

            this.jdbcTemplate.update(updateSql, vote.getNickname(), findThread.getSlug(), vote.getVoice());

            String returnSql = "UPDATE threads SET votes = (SELECT SUM(voice) FROM votes WHERE thread = (?)) WHERE slug = (?) RETURNING *";



            return this.jdbcTemplate.queryForObject(returnSql, ThreadList, findThread.getSlug(), findThread.getSlug());
        }


        // Vote vote = this.jdbcTemplate.queryForObject(search, ThreadList, vote.getVoice(), slug_or_id);
       
    }

    


   
}
