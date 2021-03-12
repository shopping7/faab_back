package com.example.faab.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KgcUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String password;


}
