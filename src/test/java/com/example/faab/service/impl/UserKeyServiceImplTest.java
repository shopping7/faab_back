package com.example.faab.service.impl;

import com.baomidou.mybatisplus.annotation.TableId;
import com.example.faab.entity.UserKey;
import com.example.faab.service.UserKeyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserKeyServiceImplTest {

    @Autowired
    UserKeyService service;

    @Test
    public void getUserKey(){
        UserKey userKey = service.getUserKey("001");
        System.out.println(userKey);
    }
}