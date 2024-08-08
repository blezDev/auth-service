package com.itc.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String userID = null;
    private String firstName;
    private String lastName;

    @Column(unique = true,nullable = false)
    private String email;
    private String password;
    private String profilePicture = "https://thumbs.dreamstime.com/z/beautiful-display-pink-white-red-petunias-summer-day-coast-pole-50746151.jpg";
    private String phoneNumber;

    public UserModel(String firstName, String lastName, String email, String password, String profilePicture, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
        this.phoneNumber = phoneNumber;
    }
}
