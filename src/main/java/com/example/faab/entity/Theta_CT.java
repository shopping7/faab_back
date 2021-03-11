package com.example.faab.entity;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;

import java.io.Serializable;

@Data
public class Theta_CT implements Serializable {
    private static final long serialVersionUID = 1L;

    private byte[] T1;

    private byte[] T2;

    private byte[] T3;

    private byte[] T4;

    private byte[] phi_beta;

    private byte[] phi_delta_id;

    private byte[] c;

    private long st;
}
