package com.example.faab.service;

import com.example.faab.domain.UserVO;
import com.example.faab.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 *
 */
public interface UserService extends IService<User> {
    public List<UserVO> getAllUsers();

    public void addUser(User user);

    public void deleteUser(String username);

    public UserVO loginUser(String username, String password);

    public UserVO getOneUser(String username);
}
