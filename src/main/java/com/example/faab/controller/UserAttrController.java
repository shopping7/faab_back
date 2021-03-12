package com.example.faab.controller;


import com.example.faab.domain.ApiResult;
import com.example.faab.domain.ApiResultUtil;
import com.example.faab.domain.UserVO;
import com.example.faab.entity.MSK;
import com.example.faab.entity.PP;
import com.example.faab.service.UserAttrService;
import com.example.faab.service.UserKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-02-08
 */
@RestController
public class UserAttrController {
    @Autowired
    UserAttrService userAttrService;

    @Autowired
    UserKeyService userKeyService;


    @RequestMapping("/userAttrEdit")
    public ApiResult userAttrEdit(@RequestBody UserVO user, HttpServletRequest request){
        String username = user.getUsername();
        List<String> user_attr = user.getAttr();
        userAttrService.DeleteUserAttr(username);
        for(String attr : user_attr){
            userAttrService.addUserAttr(username,attr);
        }

        List<String> userAttrList = userAttrService.getUserAttr(username);
        String[] userAttr = userAttrList.toArray(new String[userAttrList.size()]);
        HttpSession session = request.getSession();
        PP pp = (PP)session.getAttribute("pp");
        MSK msk = (MSK)session.getAttribute("msk");
        userKeyService.SKGen(pp, msk, username,userAttr,false);
        return ApiResultUtil.success();
    }

}

