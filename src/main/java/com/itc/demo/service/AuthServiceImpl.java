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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
            UserModel byEmail = repo.findByEmail(user.getEmail());
            if (byEmail != null) {
                return new ResultState.Error<>("User already exists.");
            }

            String password = user.getPassword();
            String hashedPassword = passwordEncoder.encode(password);
            user.setPassword(hashedPassword);
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
    public ResultState<UserModel> login(String email, String password) {
        try {
            UserModel user = repo.findByEmail(email);
            if (user == null) {
                return new ResultState.Error<>("User doesn't exist.");
            }
            boolean matches = passwordEncoder.matches(password, user.getPassword());
            if (!matches) {
                return new ResultState.Error<>("Wrong password.");
            }
            return new ResultState.Success<>(user);
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
            Optional<OtpAuth> otpExist = otpRepo.findByEmail(email);
            if (otpExist.isPresent()) {
                OtpAuth otp = otpExist.get();
                EmailDetails emailDetail = new EmailDetails(email, "OTP for authentication : " + otp.getOtp() + "\n This OTP is valid for the next 15 minutes. Please do not share this code with anyone.", "OTP VERIFICATION", "");
                mimeMessageHelper.setFrom(sender);
                mimeMessageHelper.setTo(email);
                mimeMessageHelper.setSubject(emailDetail.getSubject());
                mimeMessageHelper.setText(emailDetail.getMsgBody());
                javaMailSender.send(mimeMessage);
                return new ResultState.Success<>("Mail sent Successfully");

            }


            String otp = generateOTP(6);
            OtpAuth otpAuth = new OtpAuth(email, otp, LocalDateTime.now().plusMinutes(15));
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
                var time = LocalDateTime.now();
                LocalDateTime expirationTime = otpAuth.getExpirationTime();
                if (time.isBefore(expirationTime)) {
                    boolean verify = Objects.equals(otpAuth.getOtp(), otp);
                    if (verify) {
                        otpRepo.delete(otpAuth);
                        return new ResultState.Success<>("OTP verified.");
                    } else {
                        return new ResultState.Error<>("Invalid OTP.");
                    }
                } else {
                    return new ResultState.Error<>("OTP is Expired.");
                }


            } else {
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
                user.setPassword(passwordEncoder.encode(newPassword));
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

    @Scheduled(fixedRate = 900000L)
    @Override
    public void deleteExpiredOTPs() {
        logger.severe(LocalDateTime.now().getMinute() + "");
        otpRepo.deleteByExpirationTimeBefore(LocalDateTime.now());
    }

    @Override
    public ResultState<String> googleSignIn(String email, String firstName, String lastName) {
        try {
            UserModel user = repo.findByEmail(email);
            if (user== null) {
                String password = passwordEncoder.encode("password");
                UserModel userModel = new UserModel(firstName, lastName, email, password, "https://thumbs.dreamstime.com/z/beautiful-display-pink-white-red-petunias-summer-day-coast-pole-50746151.jpg", "NA");
                UserModel userSaved = repo.save(userModel);


                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

                EmailDetails emailDetail = new EmailDetails(email, "Welcome " + email + "\n Your credentials are : \n Email : " + email + "\n Password : password ", "Registration through Google", "");
                mimeMessageHelper.setFrom(sender);
                mimeMessageHelper.setTo(email);
                mimeMessageHelper.setSubject(emailDetail.getSubject());
                mimeMessageHelper.setText(emailDetail.getMsgBody());
                javaMailSender.send(mimeMessage);
                return new ResultState.Success<>(userSaved.getUserID().toString());

            }else {
                return new ResultState.Success<>(user.getUserID().toString());
            }

        } catch (Exception e) {
            logger.severe(e.getMessage());
            return new ResultState.Error<>("Error while creating user.");
        }
    }
}
