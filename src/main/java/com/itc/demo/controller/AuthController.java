package com.itc.demo.controller;

import com.itc.demo.service.AuthService;
import com.itc.demo.utils.ResultState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/generateotp/{email}")
    public ResponseEntity<String> generateOTP(@PathVariable String email) {
        ResultState<String> resultState = authService.generateOTPThroughEmail(email);

        if (resultState instanceof ResultState.Success<String> success) {
           return ResponseEntity.ok(success.getData());
        } else {
            ResultState.Error<String> error = (ResultState.Error<String>) resultState;
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getMessage());
        }

    }

    @PostMapping("/verify/{email}")
    public ResponseEntity<String> generateOTP(@PathVariable String email, @RequestBody String otp) {
        ResultState<String> resultState = authService.verifyEmailOTP(email,otp);

        if (resultState instanceof ResultState.Success<String> success) {
            return ResponseEntity.ok(success.getData());
        } else {
            ResultState.Error<String> error = (ResultState.Error<String>) resultState;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getMessage());
        }

    }
}
