package api.services;

import api.models.Post;
import api.models.Thread;
import api.services.UserService;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.lang.Long;

import java.time.format.DateTimeFormatter;

import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

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

    public RowMapper<ArrayList<Long>> ArrayLongListRowMapper = (rs, rowNum) -> new ArrayList<Long>(Arrays.asList((Long[]) rs.getArray("path").getArray()));

    @Autowired
    public PostService(JdbcTemplate jdbcTemplate, UserService userService) {
        this.jdbcTemplate = jdbcTemplate;
        this.userService = userService;
    }

    public ArrayList<Long> getPathFromId(Long id) {
        return this.jdbcTemplate.queryForObject("SELECT p.path FROM posts p WHERE p.id = (?)", ArrayLongListRowMapper, id);
    }

    public List<Post> createListOfPosts(Thread thread, List<Post> posts) throws SQLException {
        String created = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));


        String mquery = "INSERT INTO posts (id, forum, author, created, iseddited, thread, message, parent, path)" +
                " VALUES (?, (SELECT f.slug FROM forums f WHERE lower(f.slug) = lower(?)), " +
                "(SELECT u.nickname FROM users u WHERE lower(u.nickname) = lower(?)), (?::TIMESTAMPTZ), ?, ?, ?, ?, ?)";


        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(mquery, Statement.NO_GENERATED_KEYS)) {

            String threadForum = thread.getForum();

//            List<Post> createdPosts = new ArrayList<>();

            for (Post curPost : posts) {

                if (curPost.getParent() == null || curPost.getParent() == 0) {
                    curPost.setParent(0);
                } else {
                    try {
                        Long threadId = jdbcTemplate.queryForObject("SELECT p.thread FROM posts p WHERE p.id = (?)", Long.class, curPost.getParent());
                        if (!Objects.equals(threadId, thread.getid())) {
                            throw new NoSuchElementException("");
                        }
                    } catch (EmptyResultDataAccessException e) {
                        throw new SQLException();
                    }
                }


                Long ID = jdbcTemplate.queryForObject("SELECT nextval('posts_id_seq')", Long.class);


                ArrayList<Long> path;

                if (curPost.getParent() != null && curPost.getParent() != 0) {
                    path = this.getPathFromId(curPost.getParent());
                } else {
                    path = new ArrayList<>();
                }

                path.add(ID);


                curPost.setid(ID);
                curPost.setAuthor(userService.getInf(curPost.getAuthor()).getNickname());
                curPost.setForum(thread.getForum());
                curPost.setThread(thread.getid());
                curPost.setCreated(created);

                Array pathArray = conn.createArrayOf("BIGINT", path.toArray());


                preparedStatement.setLong(1, ID);
                preparedStatement.setString(2, threadForum);
                preparedStatement.setString(3, curPost.getAuthor());
                preparedStatement.setString(4, created);
                preparedStatement.setBoolean(5, false);
                preparedStatement.setLong(6, thread.getid());
                preparedStatement.setString(7, curPost.getMessage());
                preparedStatement.setLong(8, curPost.getParent());
                preparedStatement.setObject(9, pathArray);

                preparedStatement.addBatch();

            }

            preparedStatement.executeBatch();

            conn.close();

            return posts;
        }

    }

    public List<Post> getPosts(Long id, String sort, Integer limit, String since, boolean desc) {
        StringBuilder mquery = new StringBuilder();
        mquery.append("SELECT * FROM posts p WHERE p.thread = (?) ");


        if (Objects.equals(sort, "flat")) {
            if (since != null) {
                if (desc) {
                    mquery.append("AND p.id < ").append(since);
                } else {
                    mquery.append("AND p.id > ").append(since);
                }
            }

            if (desc) {
                mquery.append(" ORDER BY p.id DESC ");
            } else if (!desc) {
                mquery.append(" ORDER BY p.id ASC ");
            }

            mquery.append("LIMIT (?)");

            return this.jdbcTemplate.query(mquery.toString(), PostList, id, limit);

        }


        if (Objects.equals(sort, "tree")) {


            if (since != null) {
                if (desc) {
                    mquery.append("AND path < (SELECT p.path FROM posts p WHERE p.id = ").append(since);
                }

                if (!desc) {
                    mquery.append("AND path > (SELECT p.path FROM posts p WHERE p.id = ").append(since);
                }

                mquery.append(")");

            }

            if (desc) {
                mquery.append(" ORDER BY p.path DESC ");
            }

            if (!desc) {
                mquery.append(" ORDER BY p.path ASC ");
            }

            mquery.append(" LIMIT (?)");

            return this.jdbcTemplate.query(mquery.toString(), PostList, id, limit);
        }

        mquery.append(" AND p.path[1] in (SELECT id FROM posts as sub_p WHERE sub_p.thread = $1 AND sub_p.parent = 0 ");


        if (since != null) {
            if (desc) {
                mquery.append(" AND sub_p.path < (SELECT p.path FROM posts p WHERE p.id = ").append(since);
            }

            if (!desc) {
                mquery.append(" AND sub_p.path > (SELECT p.path FROM posts p WHERE p.id = ").append(since);
            }

            mquery.append(")");
        }

        if (desc) {
            mquery.append(" ORDER BY sub_p.path DESC ");
        }

        if (!desc) {
            mquery.append(" ORDER BY sub_p.path ASC ");
        }

        mquery.append(" LIMIT (?)");

        mquery.append(")");

        if (desc) {
            mquery.append(" ORDER BY p.path DESC ");
        }

        if (!desc) {
            mquery.append(" ORDER BY p.path ASC ");
        }

        return this.jdbcTemplate.query(mquery.toString(), PostList, id, limit);


    }
}
