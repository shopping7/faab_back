package com.example.faab.controller;


import com.example.faab.domain.ApiResult;
import com.example.faab.domain.ApiResultUtil;
import com.example.faab.domain.UserVO;
import com.example.faab.entity.MSK;
import com.example.faab.entity.PP;
import com.example.faab.entity.User;
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
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-03-10
 */
@RestController
public class UserController {
    @Autowired
    UserAttrService userAttrService;

    @Autowired
    UserService userService;

    @Autowired
    UserKeyService userKeyService;

    @RequestMapping("/users")
    public ApiResult getAllUsers(HttpServletRequest request, HttpServletResponse response){
        List<UserVO> allUsers = userService.getAllUsers();
        return ApiResultUtil.successReturn(allUsers);
    }

    @RequestMapping("/addUser")
    public ApiResult addUser(@RequestBody UserVO user, HttpServletRequest request){
        String username = user.getUsername();
        List<String> user_attr = user.getAttr();
        HttpSession session = request.getSession();
        PP pp = (PP)session.getAttribute("pp");
        MSK msk = (MSK)session.getAttribute("msk");
        User newUser = new User();
        if(username !=null && user_attr != null){
            newUser.setUsername(username);
            userService.addUser(newUser);
            for(String attr : user_attr){
                userAttrService.addUserAttr(username,attr);
            }
        }
        String[] userAttr = user_attr.toArray(new String[user_attr.size()]);
        userKeyService.SKGen(pp, msk, username, userAttr,true);
        return ApiResultUtil.success();
    }

    @RequestMapping("/deleteUser")
    public ApiResult deleteUser(@RequestBody UserVO user, HttpServletRequest request){
        String username = user.getUsername();
        userService.deleteUser(username);
        return ApiResultUtil.success();
    }

    @RequestMapping("/user_detail")
    public ApiResult userDetail(@RequestBody UserVO user, HttpServletRequest request){
        String username = user.getUsername();
        UserVO oneUser = userService.getOneUser(username);
        return ApiResultUtil.successReturn(oneUser);
    }
}
