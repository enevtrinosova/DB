package api.controllers;

import api.models.User;
import api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {

    final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{nickname}/create")
    public ResponseEntity<?> createUser(@PathVariable String nickname, @RequestBody User user) {
        try {
            user.setNickname(nickname);
            User createdUser = this.userService.create(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (DuplicateKeyException err) {
            List<User> existUsers = userService.findSimilarUsers(nickname, user.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(existUsers);
        }
    }

    @GetMapping("/{nickname}/profile")
    public ResponseEntity<?> getUserInformation(@PathVariable String nickname) {
        try {
            User foundUser = this.userService.getInf(nickname);
            return ResponseEntity.ok(foundUser);
        } catch (EmptyResultDataAccessException err) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Throwable("Not found such user"));
        }
    }

    @PostMapping("/{nickname}/profile")
    public ResponseEntity<?> setUserInformation(@PathVariable String nickname, @RequestBody User user) {
        try {
            User foundUser = this.userService.setInf(nickname, user);
            return ResponseEntity.ok(foundUser);
        } catch (EmptyResultDataAccessException err1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Throwable("Can't find this user"));
        } catch (DuplicateKeyException err2) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Throwable("This email is already registered"));
        }
    }

}