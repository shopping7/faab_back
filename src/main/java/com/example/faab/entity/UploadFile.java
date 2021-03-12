package com.example.faab.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.faab.config.lsss.LSSSPolicyParameter;
import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-03-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UploadFile implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fileName;

    @TableField("CT")
    private byte[] ct;

    @TableField("lsssPolicy")
    private byte[] lsssPolicy;

    @TableField("VKM")
    private byte[] vkm;

    private byte[] thetaCt;


}
