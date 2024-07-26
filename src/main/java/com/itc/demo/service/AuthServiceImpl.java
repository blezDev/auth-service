package com.itc.demo.service;

import com.itc.demo.model.UserModel;
import com.itc.demo.repository.AuthRepo;
import com.itc.demo.utils.ResultState;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class AuthServiceImpl implements AuthService {

    public static final String ACCOUNT_SID = "ACec163fd5030fc99bb60963d9a17fc675";
    public static final String AUTH_TOKEN = "fb060c5d9f94dc05c30d47e060d8c0c7";


    Logger logger = Logger.getLogger(AuthServiceImpl.class.getName());

    @Autowired
    AuthRepo repo;


    //TODO("Implement password encoder and decoder")
    @Override
    public ResultState<String> signUp(UserModel user) {

        try {
            String password = user.getPassword();

            UserModel savedState = repo.save(user);
            if (savedState == null) {
                return new ResultState.Error<>("Error while creating user.");
            } else {
                return new ResultState.Success<>("User has been saved.");
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return new ResultState.Error<>("Error while creating user.");
        }
    }


    //TODO("Implement password encoder and decoder")
    @Override
    public ResultState<String> login(String email, String password) {
        try {
            UserModel user = repo.findByEmail(email);
            if (user == null) {
                return new ResultState.Error<>("User doesn't exist.");
            }
            if (!user.getPassword().equals(password)) {
                return new ResultState.Error<>("Wrong password.");
            }
            return new ResultState.Success<>("User logged in.");
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return new ResultState.Error<>("Error while creating user.");
        }
    }

    @Override
    public ResultState<String> generateOTPThroughNumber(String phoneNumber) {

        try {

            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Verification verification = Verification.creator(
                            "VAfb7550c2574d21cc4474b062800a13da", // verification sid
                            "+91"+phoneNumber, // recipient phone number
                            "sms") //  channel type
                    .create();
            logger.info("SMS sent verification status : " + verification.getStatus());

            return new ResultState.Success<>("SMS to "+phoneNumber+" verification status : " + verification.getStatus());

        } catch (Exception e) {
            logger.severe(e.getMessage());
            return new ResultState.Error<>("Error while sending OTP to " + phoneNumber + ".");
        }
    }

    @Override
    public ResultState<String> verifyPhoneOTP(String phoneNumber, String otp) {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            VerificationCheck verificationCheck = VerificationCheck.creator(
                           "VAfb7550c2574d21cc4474b062800a13da")
                    .setTo( "+91"+phoneNumber)
                    .setCode(otp)
                    .create();
            logger.info("SMS verification status : " + verificationCheck.getStatus());
            return new ResultState.Success<>("SMS verification "+phoneNumber+ " status : " + verificationCheck.getStatus());

        } catch (Exception e) {
            logger.severe(e.getMessage());
            return new ResultState.Error<>("Error while verifying OTP to " + phoneNumber + ".");
        }
    }

    //TODO("Implement password encoder and decoder")
    @Override
    public ResultState<String> changePassword(String email, String newPassword) {
        try {
            UserModel user = repo.findByEmail(email);
            if (user == null) {
                return new ResultState.Error<>("User doesn't exist.");
            }
            if (!newPassword.isEmpty()) {
                user.setPassword(newPassword);
            }
            UserModel savedState = repo.save(user);
            if (savedState == null) {
                return new ResultState.Error<>("Error while changing password.");
            }else{
                return new ResultState.Success<>("Password has been changed.");
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return new ResultState.Error<>("Error while creating user.");
        }

    }
}
