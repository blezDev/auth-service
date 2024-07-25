package com.itc.demo.repository;

import com.itc.demo.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  AuthRepo extends JpaRepository<UserModel, Integer> {

    UserModel findByEmail(String email);
}
