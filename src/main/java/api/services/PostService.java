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
import java.lang.Long;

import java.time.format.DateTimeFormatter;
import org.springframework.dao.EmptyResultDataAccessException;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Repository
public class PostService {
    private JdbcTemplate jdbcTemplate;
    private UserService userService;
    
    // @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    public RowMapper<Post> PostList = (rs, rowNum) -> new Post(
        rs.getLong("id"), rs.getString("author"), rs.getString("forum"),
        LocalDateTime.ofInstant(rs.getTimestamp("created").toInstant(), ZoneOffset.ofHours(0))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")),
        rs.getBoolean("isEddited"), 
        rs.getLong("thread"), rs.getString("message"),
        rs.getLong("parent"));

        public RowMapper<Long> LongList = (rs, rowNum) -> new Long(rs.getLong("path"));

    @Autowired
    public PostService(JdbcTemplate jdbcTemplate, UserService userService) {
        this.jdbcTemplate = jdbcTemplate;
        this.userService = userService;
    }

    public List<Long> getPathFromId(Long id) {
        return this.jdbcTemplate.query("SELECT p.path FROM posts p WHERE p.id = (?)", LongList, id);
    }

    public List<Post> createListOfPosts(Thread thread, List<Post> posts) throws SQLException {
        String created = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
       

        String mquery = "INSERT INTO posts (id, forum, author, created, iseddited, thread, message, parent, path)" +
                        " VALUES (?, (SELECT f.slug FROM forums f WHERE lower(f.slug) = lower(?)), " +
                        "(SELECT u.nickname FROM users u WHERE lower(u.nickname) = lower(?)), (?::TIMESTAMPTZ), ?, ?, ?, ?, ?) RETURNING *";
                        
        try(Connection conn = jdbcTemplate.getDataSource().getConnection(); 
            PreparedStatement preparedStatement = conn.prepareStatement(mquery)) {
        
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
                    
                    Long threadId = jdbcTemplate.queryForObject("SELECT t.id FROM threads t WHERE t.id = (?)", Long.class, curPost.getThread());
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

            List<Long> path = new ArrayList<>();

            if (curPost.getParent() != null) {
                path = this.getPathFromId(curPost.getParent());
            }           
            
            path.add(curPost.getid());

            Object arr = conn.createArrayOf("BIGINT", path.toArray());

            preparedStatement.setLong(1, ID);
            preparedStatement.setString(2, threadForum);
            preparedStatement.setString(3, userService.getInf(curPost.getAuthor()).getNickname());
            preparedStatement.setString(4, created);
            preparedStatement.setBoolean(5, false);
            preparedStatement.setLong(6, thread.getid());
            preparedStatement.setString(7, curPost.getMessage());
            preparedStatement.setLong(8, curPost.getParent());
            preparedStatement.setObject(9, arr);

            preparedStatement.executeQuery();

            // createdPosts.add(jdbcTemplate.queryForObject(mquery, PostList, ID, threadForum, 
            //                  userService.getInf(curPost.getAuthor()).getNickname(), created, false, thread.getid(),
            //                  curPost.getMessage(), curPost.getParent(), arr));
        }
    

        return createdPosts;
    }

    }

    public List<Post> getPosts (Long id, String sort, Integer limit, String since, boolean desc) {
        StringBuilder mquery = new StringBuilder();
        mquery.append("SELECT p.id, p.author, p.forum, p.created, p.isEddited, p.thread, p.message, p.parent FROM posts p WHERE p.thread = (?) ");

        

        if (Objects.equals(sort, "flat")) {
            if (since != null) {
                mquery.append("AND p.created > '").append(since).append("'::TIMESTAMPTZ ");
            } 
            System.out.println("desc - ");
            System.out.println(desc);

            if (desc) {
                mquery.append("ORDER BY p.id DESC ");
            } else if (!desc) {
                mquery.append("ORDER BY p.id ASC ");
            } 
            
        }

        
       if (Objects.equals(sort, "tree")) {

            System.out.println("TREEEEEEEE");
            System.out.println(since);

            if (since != null) {
                if (desc) {
                    mquery.append("AND path < (SELECT p.path FROM posts p WHERE p.id = (?) ").append(since);
                }

                if (!desc) {
                    mquery.append("AND path > (SELECT p.path FROM posts p WHERE p.id = (?) ").append(since);
                }    
            }

            System.out.println(desc);

            if (desc) {
                mquery.append("ORDER BY p.path DESC "); 
            }

            if (!desc) {
                mquery.append("ORDER BY p.path ASC ");
            }
            
            mquery.append("LIMIT (?)");

            return this.jdbcTemplate.query(mquery.toString(), PostList, id, limit);
        }
            

        mquery.append("LIMIT (?)");

        return this.jdbcTemplate.query(mquery.toString(), PostList, id, limit);


    }
}
