package api.services;

import api.models.Post;
import api.models.Thread;
import api.services.UserService;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.*;

@Repository
public class PostService {
    private JdbcTemplate jdbcTemplate;
    private UserService userService;
    
    public RowMapper<Post> PostList = (rs, rowNum) -> new Post(
        rs.getLong("id"), rs.getString("author"), rs.getString("forum"),
        LocalDateTime.ofInstant(rs.getTimestamp("created").toInstant(), ZoneOffset.ofHours(0))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")),
        rs.getBoolean("isEddited"), 
        rs.getLong("thread"), rs.getString("message"),
        rs.getLong("parent"));

    @Autowired
    public PostService(JdbcTemplate jdbcTemplate, UserService userService) {
        this.jdbcTemplate = jdbcTemplate;
        this.userService = userService;
    }


    public List<Post> createListOfPosts(Thread thread, List<Post> posts) {
        String created = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
       

        String mquery = "INSERT INTO posts (id, forum, author, created, iseddited, thread, message, parent)" +
                        " VALUES (?, (SELECT f.slug FROM forums f WHERE lower(f.slug) = lower(?)), " +
                        "(SELECT u.nickname FROM users u WHERE lower(u.nickname) = lower(?)), (?::TIMESTAMPTZ), ?, ?, ?, ?) RETURNING *";
                        
        String threadForum = thread.getForum();

        System.out.println(threadForum);
        
        List<Post> createdPosts = new ArrayList<>();

        for(Post curPost : posts) {

            if(curPost.getParent() == null || curPost.getParent() == 0) {
                System.out.println( "BBBBBBBBBBBBBBBBBBBBB");
                curPost.setParent(0);
                System.out.println(curPost.getParent());        
            } else {
                try {

                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAA");
                    
                    Long threadId = jdbcTemplate.queryForObject("SELECT t.id FROM threads t WHERE t.id = (?)", Long.class, curPost.getParent());
                    System.out.println(threadId);
                    if(!Objects.equals(threadId, thread.getid())) {
                        throw new NoSuchElementException("");
                    } 
                } catch(EmptyResultDataAccessException e) {
                     
                }
            }


            Long ID = jdbcTemplate.queryForObject("SELECT nextval('posts_id_seq')", Long.class);
            System.out.println(ID);
            System.out.println(created);
            System.out.println(curPost.getAuthor());
            System.out.println(thread.getid());

            createdPosts.add(jdbcTemplate.queryForObject(mquery, PostList, ID, threadForum, 
                             userService.getInf(curPost.getAuthor()).getNickname(), created, false, thread.getid(),
                             curPost.getMessage(), curPost.getParent()));
        }

        return createdPosts;

    }
}
