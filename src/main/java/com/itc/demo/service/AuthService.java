package com.itc.demo.service;

import com.itc.demo.model.UserModel;
import com.itc.demo.utils.ResultState;
import org.springframework.scheduling.annotation.Scheduled;


public interface AuthService {


    public ResultState<String> signUp(UserModel user);

    public ResultState<UserModel> login(String email, String password);


    public ResultState<String> generateOTPThroughEmail(String email);

    public ResultState<String> verifyEmailOTP(String email, String otp);



    public ResultState<String> changePassword(String email, String newPassword);


    public ResultState<String> googleSignIn(String email,String firstName, String lastName);

    // Schedule the task to run every minute
    public void deleteExpiredOTPs();


}
