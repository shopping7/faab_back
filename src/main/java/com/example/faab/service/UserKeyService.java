package com.example.faab.service;

import com.example.faab.entity.MSK;
import com.example.faab.entity.PP;
import com.example.faab.entity.UserKey;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-03-09
 */
public interface UserKeyService extends IService<UserKey> {

    public void SKGen(PP pp, MSK msk, String username, String[] attributes, boolean isNew);

    public UserKey getUserKey(String username);

}
