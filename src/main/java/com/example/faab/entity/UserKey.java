package com.example.faab.entity;

import java.sql.Blob;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-03-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserKey implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String username;

    private byte[] sk;


}
