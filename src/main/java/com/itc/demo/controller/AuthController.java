package com.itc.demo.controller;

import com.itc.demo.model.*;
import com.itc.demo.model.ResponseBody;
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
    public ResponseEntity<ResponseBody> generateOTP(@PathVariable String email) {
        ResultState<String> resultState = authService.generateOTPThroughEmail(email);

        if (resultState instanceof ResultState.Success<String> success) {

            return ResponseEntity.ok(new ResponseBody(success.getData()));
        } else {
            ResultState.Error<String> error = (ResultState.Error<String>) resultState;
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseBody(error.getMessage()));
        }

    }

    @PostMapping("/verify/{email}")
    public ResponseEntity<ResponseBody> generateOTP(@PathVariable String email, @RequestBody OTPModel otp) {
        ResultState<String> resultState = authService.verifyEmailOTP(email,otp.getOtp());

        if (resultState instanceof ResultState.Success<String> success) {
            return ResponseEntity.ok(new ResponseBody(success.getData()));
        } else {
            ResultState.Error<String> error = (ResultState.Error<String>) resultState;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseBody(error.getMessage()));
        }

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseBody> login(@RequestBody LoginModel loginModel) {
        ResultState<UserModel> loginState = authService.login(loginModel.getEmail(), loginModel.getPassword());
        if (loginState instanceof ResultState.Success<?> success) {
            UserModel user = loginState.getData();

            return ResponseEntity.ok( new AuthResponseBody("User Logged in.", user.getUserID(), user.getEmail(), user.getFirstName(), user.getLastName()));
        }else {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( new AuthResponseBody(loginState.getMessage(),null,null,null,null));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseBody> signup(@RequestBody UserModel userModel){
        ResultState<String> loginState = authService.signUp(userModel);
        if (loginState instanceof ResultState.Success<String> success) {
            return ResponseEntity.ok(new ResponseBody(success.getData()));
        }else {
            ResultState.Error<String> error = (ResultState.Error<String>) loginState;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseBody(error.getMessage()));
        }
    }



    @PostMapping("/changepassword")
    public ResponseEntity<ResponseBody> changePassowrd(@RequestBody LoginModel user){
        ResultState<String> loginState = authService.changePassword(user.getEmail(),user.getPassword());
        if (loginState instanceof ResultState.Success<String> success) {
            return ResponseEntity.ok(new ResponseBody(success.getData()));
        }else {
            ResultState.Error<String> error = (ResultState.Error<String>) loginState;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseBody(error.getMessage()));
        }
    }

    @PostMapping("/googlesignup")
    public ResponseEntity<ResponseBody> googleSignUp(@RequestBody GoogleModel googleModel){
        ResultState<String> loginState = authService.googleSignIn(googleModel.getEmail(),googleModel.getFirstName(),googleModel.getLastName());
        if (loginState instanceof ResultState.Success<String> success) {
            return ResponseEntity.ok(new ResponseBody(success.getData()));
        }else {
            ResultState.Error<String> error = (ResultState.Error<String>) loginState;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseBody(error.getMessage()));
        }
    }

}
