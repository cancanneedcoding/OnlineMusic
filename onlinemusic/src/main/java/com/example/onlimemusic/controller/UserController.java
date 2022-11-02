package com.example.onlimemusic.controller;

import com.example.onlimemusic.mapper.UserMapper;
import com.example.onlimemusic.model.User;
import com.example.onlimemusic.tools.Constant;
import com.example.onlimemusic.tools.ResponseBodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    //未使用加密的算法 不使用
    @RequestMapping("/login1")
    public ResponseBodyMessage<User> login(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        User userLogin = new User();
        userLogin.setUsername(username);
        userLogin.setPassword(password);
        User user = userMapper.login(userLogin);
        //大于等0为成功，小于0失败
        if (user != null) {
            System.out.println("登录成功");
            request.getSession().setAttribute(Constant.USERINFO_SESSION_KEY, user);
            //返回信息
            return new ResponseBodyMessage<>(1, "登陆成功", userLogin);
        } else {
            System.out.println("登录失败");
            return new ResponseBodyMessage<>(-1, "登陆失败", userLogin);
        }
    }

    /*加密的登录算法*/
    @RequestMapping("/login")
    public ResponseBodyMessage<User> login1(@RequestParam String username, String password, HttpServletRequest request) {
        User user = userMapper.selectByName(username);

        if(user == null) {
            System.out.println("登录失败！");
            return new ResponseBodyMessage<>(-1,"用户名或者密码错误！",user);
        }else {
            boolean flg = bCryptPasswordEncoder.matches(password,user.getPassword());
            if(!flg) {
                return new ResponseBodyMessage<>(-1,"用户名或者密码错误！",user);
            }
            request.getSession().setAttribute(Constant.USERINFO_SESSION_KEY,user);
            return new ResponseBodyMessage<>(1,"登录成功！",user);
        }
    }
}
