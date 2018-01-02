package api.controllers;


import api.models.Post;
import api.models.PostDetails;
import api.services.ForumService;
import api.services.PostService;
import api.services.ThreadService;
import api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/post")
public class PostController {
    final ThreadService threadService;
    final ForumService forumService;
    final UserService userService;
    final PostService postService;



    @Autowired
    public PostController(ThreadService threadService, ForumService forumService, UserService userService, PostService postService) {
        this.threadService = threadService;
        this.forumService = forumService;
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<?> postDetails(@PathVariable String id,
                                         @RequestParam(value = "related", required = false) List<String> related) {

        try {
            PostDetails postDetails = new PostDetails();
            postDetails.setPost(this.postService.getPostById(id));
            if (related != null) {
                if (related.contains("forum")) {
                    postDetails.setForum(forumService.getInf(postDetails.getPost().getForum()));
                }
                if (related.contains("thread")) {
                    postDetails.setThread(threadService.getThreadById(postDetails.getPost().getThread()));
                }
                if (related.contains("user")) {
                    postDetails.setAuthor(userService.getInf(postDetails.getPost().getAuthor()));
                }
            }
            return ResponseEntity.ok(postDetails);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Throwable("This post not found"));
        }
    }

    @PostMapping("/{id}/details")
    public ResponseEntity<?> setPostInformation(@PathVariable String id, @RequestBody Post post) {
        try {
            Post notUpdatePost = this.postService.getPostById(id);
            return ResponseEntity.ok(this.postService.setInformation(notUpdatePost, post));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Throwable("This post not found"));
        }
    }

}



