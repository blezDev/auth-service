package com.itc.demo.service;

import com.itc.demo.model.UserModel;
import com.itc.demo.utils.ResultState;


public interface AuthService {


    public ResultState<String> signUp(UserModel user);

    public ResultState<String> login(String email, String password);


    public ResultState<String> generateOTPThroughNumber(String phoneNumber);

    public ResultState<String> verifyPhoneOTP(String phoneNumber, String otp);



    public ResultState<String> changePassword(String email, String newPassword);


}
