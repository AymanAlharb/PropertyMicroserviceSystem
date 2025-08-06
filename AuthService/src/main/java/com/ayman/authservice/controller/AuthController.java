package com.ayman.authservice.controller;

import com.ayman.authservice.constant.ApiRoutes;
import com.ayman.authservice.model.dto.request.CreateLoginRequest;
import com.ayman.authservice.model.dto.request.CreateUserRequest;
import com.ayman.authservice.model.dto.response.ApiResponse;
import com.ayman.authservice.model.dto.response.UserResponse;
import com.ayman.authservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.AUTH)
@RestController
public class AuthController {
    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody CreateUserRequest userRequest) {
        userService.registerUser(userRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Registered successfully"));
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody CreateLoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Token: " + userService.login(loginRequest)));
    }

    @GetMapping("/get-by-username")
    public ResponseEntity<UserResponse> getUserByUsername(@RequestParam String username) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByUsername(username));
    }

    @GetMapping("/get-by-id")
    public ResponseEntity<UserResponse> getUserById(@RequestParam Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
    }


}
