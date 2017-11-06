package api.controllers;

import api.models.Thread;
import api.models.Forum;
import api.models.User;
import api.services.ThreadService;
import api.services.ForumService;
import api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api/forum")
public class ThreadController {

    final ThreadService threadService;
    final ForumService forumService;
    final UserService userService;
    
    @Autowired
    public ThreadController(ThreadService threadService, ForumService forumService, UserService userService) {
        this.threadService = threadService;
        this.forumService = forumService;
        this.userService = userService;
    }

    @PostMapping("/{forum}/create")
    public ResponseEntity<?> createThread(@PathVariable String forum, @RequestBody Thread thread) {
        try {
            Forum getForum = forumService.getInf(forum);
            User getUser = userService.getInf(thread.getAuthor());
            thread = threadService.create(getForum.getSlug(), thread, getUser.getNickname());
            thread.setAuthor(getUser.getNickname());
            thread.setForum(getForum.getSlug());
            return ResponseEntity.status(HttpStatus.CREATED).body(thread);
        } catch (DuplicateKeyException err) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("");
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Throwable("This forum not found"));
        }
    }
}
