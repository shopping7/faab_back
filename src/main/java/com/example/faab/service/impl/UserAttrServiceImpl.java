package com.example.faab.service.impl;

import com.example.faab.mapper.UserAttrMapper;
import com.example.faab.service.UserAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAttrServiceImpl implements UserAttrService {
    @Autowired
    UserAttrMapper mapper;

    @Override
    public List<String> getUserAttr(String username) {
        return mapper.getUserAttr(username);
    }

    @Override
    public void DeleteUserAttr(String username) {

        mapper.deleteUserAttr(username);
    }

    @Override
    public void addUserAttr(String username, String attr) {
        mapper.addUserAttr(username,attr);
    }
}
