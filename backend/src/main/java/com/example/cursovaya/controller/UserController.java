//package com.example.cursovaya.controller;
//
//import com.example.cursovaya.DTO.request.UserRequest;
//import com.example.cursovaya.DTO.response.UserResponse;
//import com.example.cursovaya.entity.User;
//import com.example.cursovaya.service.UserService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;

//@RestController
//@RequestMapping("user")
//public class UserController {
//    private final UserService userService;
//    private static final Logger log = Logger.getLogger(UserController.class.getName());
//
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @GetMapping("{id}")
//    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
//        log.info("called method getUserById");
//        UserResponse user = userService.getUserById(id);
//        return ResponseEntity.ok(user);
//    }
//
//    @GetMapping()
//    public ResponseEntity<List<UserResponse>> getAllUsers()
//    {
//        log.info("called method getAllUsers");
//        List<UserResponse> users = userService.getAllUsers();
//        return ResponseEntity.ok(users);
//    }
//
//    @PostMapping()
//    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest){
//        log.info("called method createUser");
//        UserResponse user = userService.createUser(userRequest);
//        return ResponseEntity.ok(user);
//    }
//
//    @PostMapping("description/{id}")
//    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody String description){
//        log.info("called method updateUser");
//        UserResponse user = userService.addDescriptionToUser(id, description);
//        return ResponseEntity.ok(user);
//    }
//}
