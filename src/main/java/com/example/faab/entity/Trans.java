package com.example.faab.entity;

import lombok.Data;

import it.unisa.dia.gas.jpbc.Element;

import java.io.Serializable;

@Data
public class Trans implements Serializable {
    private byte[] ct;

    private byte[] C0;

    private byte[] CM;

    private byte[] VK;
}
