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



    private String firstName;
    private String lastName;
    @Id
    @Column(unique = true,nullable = false)
    private String email;
    private String password;
    private String profilePicture = "https://thumbs.dreamstime.com/z/beautiful-display-pink-white-red-petunias-summer-day-coast-pole-50746151.jpg";
    private String phoneNumber;
}
