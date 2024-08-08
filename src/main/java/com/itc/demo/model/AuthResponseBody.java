package com.itc.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AuthResponseBody {
    private String message;
    private Integer userId;
    private String email;
    private String firstName;
    private String lastName;
}
