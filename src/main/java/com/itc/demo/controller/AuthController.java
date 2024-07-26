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

    @PostMapping("/generateotp/{phonenumber}")
    public ResponseEntity<String> generateOTP(@PathVariable String phonenumber) {
        ResultState<String> resultState = authService.generateOTPThroughNumber(phonenumber);

        if (resultState instanceof ResultState.Success<String> success) {
           return ResponseEntity.ok(success.getData());
        } else {
            ResultState.Error<String> error = (ResultState.Error<String>) resultState;
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getMessage());
        }

    }

    @PostMapping("/verify/{phonenumber}")
    public ResponseEntity<String> generateOTP(@PathVariable String phonenumber, @RequestBody String otp) {
        ResultState<String> resultState = authService.verifyPhoneOTP(phonenumber,otp);

        if (resultState instanceof ResultState.Success<String> success) {
            return ResponseEntity.ok(success.getData());
        } else {
            ResultState.Error<String> error = (ResultState.Error<String>) resultState;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getMessage());
        }

    }
}
