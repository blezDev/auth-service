package com.itc.demo.service;

import com.itc.demo.model.EmailDetails;
import com.itc.demo.model.OtpAuth;
import com.itc.demo.model.UserModel;
import com.itc.demo.repository.AuthRepo;
import com.itc.demo.repository.OTPRepo;
import com.itc.demo.utils.ResultState;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class AuthServiceImpl implements AuthService {
    /*

        public static final String ACCOUNT_SID = "ACec163fd5030fc99bb60963d9a17fc675";
        public static final String AUTH_TOKEN = "fb060c5d9f94dc05c30d47e060d8c0c7";

    */
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private OTPRepo otpRepo;

    @Value("${spring.mail.username}")
    private String sender;

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

    private static final String DIGITS = "0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateOTP(int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(DIGITS.length());
            otp.append(DIGITS.charAt(index));
        }
        return otp.toString();
    }

    @Override
    public ResultState<String> generateOTPThroughEmail(String email) {

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            Optional<OtpAuth> otpExist = otpRepo.findByEmail (email);
            if (otpExist.isPresent()) {
                OtpAuth otp = otpExist.get();
                EmailDetails emailDetail = new EmailDetails(email, "OTP for authentication : " + otp.getOtp(), "OTP VERIFICATION", "");
                mimeMessageHelper.setFrom(sender);
                mimeMessageHelper.setTo(email);
                mimeMessageHelper.setSubject(emailDetail.getSubject());
                mimeMessageHelper.setText(emailDetail.getMsgBody());
                javaMailSender.send(mimeMessage);
                return new ResultState.Success<>("Mail sent Successfully");

            }


            String otp = generateOTP(6);
            OtpAuth otpAuth = new OtpAuth(email, otp, LocalDateTime.now().plusMinutes(2));
            otpRepo.save(otpAuth);
            EmailDetails emailDetail = new EmailDetails(email, "OTP for authentication : " + otp, "OTP VERIFICATION", "");
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            mimeMessageHelper.setText(emailDetail.getMsgBody());
            javaMailSender.send(mimeMessage);
            return new ResultState.Success<>("Mail sent Successfully");
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return new ResultState.Error<>("Error while sending OTP to " + email + ".");
        }
    }

    @Override
    public ResultState<String> verifyEmailOTP(String email, String otp) {
        try {
            logger.info(otp);
            Optional<OtpAuth> otpExist = otpRepo.findByEmail(email);
            if (otpExist.isPresent()) {
                OtpAuth otpAuth = otpExist.get();
                boolean verify = Objects.equals(otpAuth.getOtp(), otp);
                if (verify) {
                    otpRepo.delete(otpAuth);
                    return new ResultState.Success<>("OTP verified.");
                }else {
                    return new ResultState.Error<>("Invalid OTP.");
                }

            }else{
                logger.severe(false + "");
                return new ResultState.Error<>("Invalid OTP.");
            }

        } catch (Exception e) {
            logger.severe(e.getMessage());
            return new ResultState.Error<>("Error while verifying OTP to " + email + ".");
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
            } else {
                return new ResultState.Success<>("Password has been changed.");
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return new ResultState.Error<>("Error while creating user.");
        }

    }

    @Scheduled(fixedRate = 60000L)
    @Override
    public void deleteExpiredOTPs() {
        otpRepo.deleteByExpirationTimeBefore(LocalDateTime.now());
    }
}
