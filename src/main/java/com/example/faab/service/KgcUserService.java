package com.example.faab.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.faab.entity.KgcUser;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-02-25
 */
public interface KgcUserService extends IService<KgcUser> {

    public KgcUser loginUser(String username, String password);
}
