package com.example.faab.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Random;

@Data
public class MSK implements Serializable {
    private static final long serialVersionUID = 1L;

    private String k;

    private byte[] alpha;

    private byte[] a;

}
