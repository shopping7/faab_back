package com.example.faab.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.faab.domain.UserVO;
import com.example.faab.entity.User;
import com.example.faab.mapper.UserMapper;
import com.example.faab.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-03-10
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    UserMapper mapper;


    @Override
    public List<UserVO> getAllUsers() {
        return mapper.getAllUsers();
    }

    @Override
    public void addUser(User user) {
        mapper.insert(user);
    }

    @Override
    public void deleteUser(String username) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username",username);
        mapper.delete(queryWrapper);
    }

    @Override
    public UserVO loginUser(String username, String password) {
        return mapper.loginUser(username,password);
    }

    @Override
    public UserVO getOneUser(String username) {
        return mapper.getOneUser(username);
    }

}
