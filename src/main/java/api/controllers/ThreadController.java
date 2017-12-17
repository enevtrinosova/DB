package api.controllers;

import api.models.Thread;
import api.models.Forum;
import api.models.User;
import api.models.Post;
import api.models.Vote;
import api.services.ThreadService;
import api.services.ForumService;
import api.services.UserService;
import api.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Objects;


import java.util.List;

@RestController
@RequestMapping(path = "/api/thread")
public class ThreadController {

    final ThreadService threadService;
    final ForumService forumService;
    final UserService userService;
    final PostService postService;
    
    @Autowired
    public ThreadController(ThreadService threadService, ForumService forumService, UserService userService,
                            PostService postService) {
        this.threadService = threadService;
        this.forumService = forumService;
        this.userService = userService;
        this.postService = postService;
    }

    @PostMapping("/{slug_or_id}/create")
    public ResponseEntity<?> createPost(@RequestBody List<Post> posts, @PathVariable String slug_or_id) {
        try {
            Thread findThread = threadService.getThreadBySlugOrId(slug_or_id);
            List<Post> createdPosts = postService.createListOfPosts(findThread, posts);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPosts);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Throwable("This thread not found"));
        }  catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Throwable(e.getMessage()));
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Throwable(e.getMessage()));
        }
    }

    @PostMapping("/{slug_or_id}/vote")
    public ResponseEntity<?> setVote(@RequestBody Vote vote, @PathVariable String slug_or_id) {
        try {
            Thread changeThread = threadService.setThreadVote(vote, slug_or_id);
            
            return ResponseEntity.ok(changeThread);
        } catch(EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Throwable("This thread not found"));
        }
    }


    @GetMapping("/{slug_or_id}/details")
    public ResponseEntity<?> threadDetails(@PathVariable String slug_or_id) {
        try {
            return ResponseEntity.ok(this.threadService.getThreadBySlugOrId(slug_or_id));
        } catch(EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Throwable("This thread not found"));
        }
    } 

    @GetMapping("/{slug_or_id}/posts")
    public ResponseEntity<?> getPosts(@PathVariable String slug_or_id,
                                      @RequestParam(value="sort", defaultValue="flat") String sort,
                                      @RequestParam(value="limit", defaultValue="100") Integer limit, 
                                      @RequestParam(value="since", required=false) String since,
                                      @RequestParam(value="desc", required=false, defaultValue="false") boolean desc)
                                      {
        try {
            Thread findThread = this.threadService.getThreadBySlugOrId(slug_or_id);

            return ResponseEntity.ok(this.postService.getPosts(findThread.getid(), sort, limit, since, desc));

        } catch(EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Throwable("This thread not found"));
        }
    }

    
}


    
    

