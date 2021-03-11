package com.example.faab.mapper;

import com.example.faab.domain.UserVO;
import com.example.faab.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 *
 */
public interface UserMapper extends BaseMapper<User> {
    public List<UserVO> getAllUsers();

    public UserVO loginUser(String username, String password);

    public UserVO getOneUser(String username);
}
