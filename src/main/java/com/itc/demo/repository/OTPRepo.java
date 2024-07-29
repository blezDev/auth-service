package com.itc.demo.repository;

import com.itc.demo.model.OtpAuth;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OTPRepo extends JpaRepository<OtpAuth,String>{
    Optional<OtpAuth> findByEmailAndExpirationTimeAfter(String email, LocalDateTime now);
    Optional<OtpAuth> findByEmail(String email);




    @Modifying
    @Transactional
    @Query("DELETE FROM OtpAuth WHERE expirationTime <= :now")
    void deleteByExpirationTimeBefore(@Param("now") LocalDateTime now);

}
