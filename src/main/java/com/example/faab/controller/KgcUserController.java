package com.example.faab.controller;


import com.example.faab.config.DoublePairing;
import com.example.faab.config.Serial;
import com.example.faab.domain.ApiResult;
import com.example.faab.domain.ApiResultUtil;
import com.example.faab.domain.LoginVO;
import com.example.faab.entity.KgcUser;
import com.example.faab.entity.SysPara;
import com.example.faab.entity.User;
import com.example.faab.service.KgcUserService;
import com.example.faab.service.SysParaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-02-25
 */
@RestController
public class KgcUserController {
    @Autowired
    KgcUserService kgcUserService;

    @Autowired
    SysParaService sysParaService;


    @RequestMapping("/kgc-user/login")
    public ApiResult login(@RequestBody LoginVO login, HttpServletRequest request) {
        KgcUser kgcUser = kgcUserService.loginUser(login.getUsername(), login.getPassword());
        if(kgcUser != null){
            HttpSession session = request.getSession();
            session.setAttribute("kgcUser",kgcUser);
            //获得系统参数
            DoublePairing doublePairing = new DoublePairing();
            doublePairing.getStart();
            SysPara sysPara = sysParaService.getSysPara();
            Serial serial = new Serial();
            session.setAttribute("pp",serial.deserial(sysPara.getPp()));
            session.setAttribute("msk",serial.deserial(sysPara.getMsk()));
            System.out.println(sysPara);
            return ApiResultUtil.successReturn(kgcUser);
        } else {
            return ApiResultUtil.errorAuthorized("用户名或密码错误");
        }
    }
}
