package com.example.faab.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginVO implements Serializable {

    private String username;
    private String password;

    public LoginVO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
