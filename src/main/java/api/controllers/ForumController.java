package api.controllers;

import api.models.Forum;
import api.models.User;
import api.models.Thread;
import api.services.ForumService;
import api.services.UserService;
import api.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/forum")
public class ForumController {
    final ForumService forumService;
    final UserService userService;
    final ThreadService threadService;

    @Autowired
    public ForumController(ForumService forumService, UserService userService, ThreadService threadService) {
        this.forumService = forumService;
        this.userService = userService;
        this.threadService = threadService;
    }


    @PostMapping("/create")
    public ResponseEntity<?> createForum(@RequestBody Forum forum) {
        try {
            User author = userService.getInf(forum.getUser());
            forumService.create(forum, author.getNickname());
            forum.setUser(author.getNickname());
            return ResponseEntity.status(HttpStatus.CREATED).body(forum);
        } catch (DuplicateKeyException err) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumService.getInf(forum.getSlug()));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Throwable("This user not found"));
        }
    }

    @GetMapping("/{slug}/details")
    public ResponseEntity<?> getForumDetails(@PathVariable String slug) {
        try {
            Forum foundForum = this.forumService.getInf(slug);
            return ResponseEntity.ok(foundForum);
        } catch (EmptyResultDataAccessException err) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Throwable("Not found such forum"));
        }
    }

    @GetMapping("/{slug}/threads")
    public ResponseEntity<?> getForumThreads(@PathVariable String slug, 
                                             @RequestParam(value="desc", defaultValue="false") boolean desc,
                                             @RequestParam(value="limit", defaultValue="100") Integer limit, 
                                             @RequestParam(value="since", required=false) String since) {
        try {
            String foundslug = forumService.getSlug(slug);
            List<Thread> foundThreads = this.threadService.getThreads(foundslug, desc, limit, since);
            return ResponseEntity.ok(foundThreads);
        } catch (EmptyResultDataAccessException err) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Throwable("Not found such forum"));
        }
    }

    @GetMapping("/{slug}/users")
    public ResponseEntity<?> getForumUsers(@PathVariable String slug,
                                             @RequestParam(value="desc", defaultValue="false") boolean desc,
                                             @RequestParam(value="limit", defaultValue="100") Integer limit,
                                             @RequestParam(value="since", required=false) String since) {
        try {
            String foundslug = forumService.getSlug(slug);
            List<User> foundUsers = this.userService.getForumUsers(foundslug, desc, limit, since);
            return ResponseEntity.ok(foundUsers);
        } catch (EmptyResultDataAccessException err) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Throwable("Not found such forum"));
        }
    }

    @PostMapping("/{forum}/create")
    public ResponseEntity<?> createThread(@PathVariable String forum, @RequestBody Thread thread) {
        try {
            Forum getForum = forumService.getInf(forum);
            User getUser = userService.getInf(thread.getAuthor());
            Thread newThread = threadService.create(getForum.getSlug(), thread, getUser.getNickname());
            newThread.setAuthor(getUser.getNickname());
            newThread.setForum(getForum.getSlug());
            return ResponseEntity.status(HttpStatus.CREATED).body(newThread);
        } catch (DuplicateKeyException err) {
            Thread similarUser = threadService.getThreadBySlugOrId(threadService.getThreadSlug(thread.getSlug()));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(similarUser);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Throwable("This forum not found"));
        }
    }

}
