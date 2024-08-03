package com.itc.demo.controller;

import com.itc.demo.model.GoogleModel;
import com.itc.demo.model.LoginModel;
import com.itc.demo.model.UserModel;
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

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginModel loginModel) {
        ResultState<String> loginState = authService.login(loginModel.getEmail(), loginModel.getPassword());
        if (loginState instanceof ResultState.Success<String> success) {
            return ResponseEntity.ok(success.getData());
        }else {
            ResultState.Error<String> error = (ResultState.Error<String>) loginState;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserModel userModel){
        ResultState<String> loginState = authService.signUp(userModel);
        if (loginState instanceof ResultState.Success<String> success) {
            return ResponseEntity.ok(success.getData());
        }else {
            ResultState.Error<String> error = (ResultState.Error<String>) loginState;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getMessage());
        }
    }



    @PostMapping("/changepassword")
    public ResponseEntity<String> changePassowrd(@RequestBody LoginModel user){
        ResultState<String> loginState = authService.changePassword(user.getEmail(),user.getPassword());
        if (loginState instanceof ResultState.Success<String> success) {
            return ResponseEntity.ok(success.getData());
        }else {
            ResultState.Error<String> error = (ResultState.Error<String>) loginState;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getMessage());
        }
    }

    @PostMapping("/googlesignup")
    public ResponseEntity<String> googleSignUp(@RequestBody GoogleModel googleModel){
        ResultState<String> loginState = authService.googleSignIn(googleModel.getEmail(),googleModel.getFirstName(),googleModel.getLastName());
        if (loginState instanceof ResultState.Success<String> success) {
            return ResponseEntity.ok(success.getData());
        }else {
            ResultState.Error<String> error = (ResultState.Error<String>) loginState;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getMessage());
        }
    }

}
