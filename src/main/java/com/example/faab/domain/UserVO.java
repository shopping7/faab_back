package com.example.faab.domain;

import lombok.Data;

import java.util.List;

@Data
public class UserVO {
    private String username;

    private boolean sex;

    private String email;

    private String phone;

    private List<String> attr;
}
