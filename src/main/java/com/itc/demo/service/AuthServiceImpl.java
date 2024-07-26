package com.itc.demo.service;

import com.itc.demo.model.UserModel;
import com.itc.demo.repository.AuthRepo;
import com.itc.demo.utils.ResultState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class AuthServiceImpl implements AuthService {
/*

    public static final String ACCOUNT_SID = "AC5214af5ccdf03f72a16eb2b0068c9f14";
    public static final String AUTH_TOKEN = "ceabaa3df4c6d9fad6b3ac4017610b5c";
*/


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

        /*    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Verification verification = Verification.creator(
                            "VA989e139dc19ecedc828dcc132e2b2c13", // verification sid
                            "+91"+phoneNumber, // recipient phone number
                            "sms") //  channel type
                    .create();
            logger.info("SMS sent verification status : " + verification.getStatus());

            return new ResultState.Success<>("SMS to "+phoneNumber+" status : " + verification.getStatus());*/
            return new ResultState.Error<>("Error while sending OTP to " + phoneNumber + ".");
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return new ResultState.Error<>("Error while sending OTP to " + phoneNumber + ".");
        }
    }

    @Override
    public ResultState<String> verifyPhoneOTP(String phoneNumber, String otp) {
        try {
     /*       Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            VerificationCheck verificationCheck = VerificationCheck.creator(
                           "VA989e139dc19ecedc828dcc132e2b2c13")
                    .setTo( "+91"+phoneNumber)
                    .setCode(otp)
                    .create();
            logger.info("SMS verification status : " + verificationCheck.getStatus());
            return new ResultState.Success<>("SMS verification "+phoneNumber+ " status : " + verificationCheck.getStatus());*/
            return new ResultState.Error<>("Error while sending OTP to " + phoneNumber + ".");
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
