package com.example.faab.controller;

import com.example.faab.config.DoublePairing;
import com.example.faab.config.Serial;
import com.example.faab.domain.ApiResult;
import com.example.faab.domain.ApiResultUtil;
import com.example.faab.domain.LoginVO;
import com.example.faab.domain.UserVO;
import com.example.faab.entity.SysPara;
import com.example.faab.entity.UserKey;
import com.example.faab.service.SysParaService;
import com.example.faab.service.UserAttrService;
import com.example.faab.service.UserKeyService;
import com.example.faab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@RestController
public class LoginController {
    @Autowired
    UserService userService;

    @Autowired
    UserAttrService userAttrService;

    @Autowired
    SysParaService sysParaService;

    @Autowired
    UserKeyService userKeyService;

    @RequestMapping("/user/login")
    public ApiResult login(@RequestBody LoginVO login, HttpServletRequest request) {
        UserVO loginUser = userService.loginUser(login.getUsername(),login.getPassword());
        HttpSession session = request.getSession();
        session.setAttribute("loginUser",loginUser);
        if (loginUser != null) {
            //获得系统参数
            DoublePairing.getStart();
            SysPara sysPara = sysParaService.getSysPara();
            Serial serial = new Serial();
            session.setAttribute("pp",serial.deserial(sysPara.getPp()));
            session.setAttribute("msk",serial.deserial(sysPara.getMsk()));
            System.out.println(sysPara);
            //获得用户密钥
            System.out.println(loginUser.getUsername());
            UserKey userKey = userKeyService.getUserKey(loginUser.getUsername());
            System.out.println(userKey);
            session.setAttribute("sk",serial.deserial(userKey.getSk()));

            String token = UUID.randomUUID().toString();
            session.setAttribute("token", token);

            return ApiResultUtil.successReturn(loginUser);
        } else {
            return ApiResultUtil.errorAuthorized("用户名或密码错误");
        }
    }

    @RequestMapping("/user/info")
    public ApiResult getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        UserVO user = (UserVO)session.getAttribute("loginUser");
        if (user != null) {
            System.out.println("success");
            return ApiResultUtil.successReturn(user);
        } else {
            return ApiResultUtil.errorAuthorized("获取用户信息错误");
        }
    }
}
