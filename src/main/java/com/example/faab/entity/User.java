package com.example.faab.entity;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    private Boolean sex;

    private String email;

    private String phone;

    @Override
    public String toString() {
        return  "{username:'" + username + '\'' +
                ", password:'" + password + '\'' +
                ", sex:" + sex +
                ", email:'" + email + '\'' +
                ", phone:'" + phone + '\'' +
                '}';
    }
}
