package com.example.faab.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.faab.entity.KgcUser;
import com.example.faab.mapper.KgcUserMapper;
import com.example.faab.service.KgcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-02-25
 */
@Service
public class KgcUserServiceImpl extends ServiceImpl<KgcUserMapper, KgcUser> implements KgcUserService {

    @Autowired
    KgcUserMapper mapper;

    @Override
    public KgcUser loginUser(String username, String password) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username",username);
        queryWrapper.eq("password",password);
        KgcUser kgcUser = mapper.selectOne(queryWrapper);
        return kgcUser;
    }
}
