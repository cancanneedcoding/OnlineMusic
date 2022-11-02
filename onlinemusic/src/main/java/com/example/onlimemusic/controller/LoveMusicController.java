package com.example.onlimemusic.controller;

import com.example.onlimemusic.mapper.LoveMusicMapper;
import com.example.onlimemusic.mapper.MusicMapper;
import com.example.onlimemusic.model.Music;
import com.example.onlimemusic.model.User;
import com.example.onlimemusic.tools.Constant;
import com.example.onlimemusic.tools.ResponseBodyMessage;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/lovemusic")
public class LoveMusicController {

    @Autowired
    private LoveMusicMapper loveMusicMapper;

    @Autowired
    private MusicMapper musicMapper;

    //添加一个用户喜欢的音乐
    @RequestMapping("/likemusic")
    public ResponseBodyMessage<Boolean> insert(@RequestParam int musicid, HttpServletRequest request) {
        System.out.println("要喜欢的音乐id" + musicid);
        //1.检查是否登录了
        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            System.out.println("没有登录！");
            return new ResponseBodyMessage<>(-1, "请登录后再收藏", false);
        }
        User user = (User) httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userid = user.getId();
        System.out.println("要喜欢的音乐的用户" + userid);

        //2.先看看有没有这个音乐
        Music music1 = musicMapper.selectById(musicid);
        if (music1 == null) {
            System.out.println("并没有当前编号的音乐");
            return new ResponseBodyMessage<>(-1, "并没有当前编号的音乐", false);
        }
        //3.再看是否已经喜欢了
        Music music = loveMusicMapper.findLoveMusic(userid, musicid);
        if (music != null) {
            //TODO:加一个取消收藏的功能
            //1.反馈已经收藏
           /* int ret = loveMusicMapper.deleteLoveMusic(userid, musicid);
            if (ret == 1) {
                System.out.println("取消收藏成功！");
                return new ResponseBodyMessage<>(1, "取消收藏成功", true);
            } else {
                System.out.println("取消收藏失败！");
                return new ResponseBodyMessage<>(-1, "取消收藏失败", false);
            }*/
            return new ResponseBodyMessage<>(-1,"你已经喜欢该音乐！",false);

        } else {
            //2.添加进收藏
            boolean f = loveMusicMapper.insertLoveMusic(userid, musicid);
            if (f) {
                System.out.println("添加喜欢成功");
                return new ResponseBodyMessage<>(1, "添加喜欢成功", true);
            } else {
                System.out.println("添加喜欢失败");
                return new ResponseBodyMessage<>(1, "添加喜欢失败", false);
            }
        }
    }

    @RequestMapping("/findlovemusic")
    //查询某个用户喜欢的某个音乐,如果不加音乐名进行模糊查询，那就全部喜欢的都输出
    public ResponseBodyMessage<List<Music>> findLoveMusic(@RequestParam(required = false) String musicname, HttpServletRequest request) {
        //1.检查是否登录了
        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            System.out.println("没有登录！");
            return new ResponseBodyMessage<>(-1, "请登录后再查询", null);
        }
        User user = (User) httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userid = user.getId();

        List<Music> list;
        if (musicname != null) {
            list = loveMusicMapper.findLoveMusicBykeyAndUID(musicname, userid);
        } else {
            list = loveMusicMapper.findLoveMusicByUserid(userid);
        }
        return new ResponseBodyMessage<>(1, "查询成功", list);
    }

    @RequestMapping("/deletelovemusic")
    public ResponseBodyMessage<Boolean> deleteLoveMusic(@RequestParam int musicid,HttpServletRequest request){
        //1.检查是否登录了
        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            System.out.println("没有登录！");
            return new ResponseBodyMessage<>(-1, "请登录后再移除", null);
        }
        User user = (User) httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userid = user.getId();

        //2.先看看有没有这个音乐
        Music music1 = musicMapper.selectById(musicid);
        if (music1 == null) {
            System.out.println("并没有当前编号的音乐");
            return new ResponseBodyMessage<>(-1, "并没有当前编号的音乐", false);
        }
        int ret=loveMusicMapper.deleteLoveMusic(userid,musicid);
        if (ret==1){
            System.out.println("取消收藏成功！");
            return new ResponseBodyMessage<>(1, "取消收藏成功", true);
        }else{
            System.out.println("取消收藏失败！");
            return new ResponseBodyMessage<>(-1, "取消收藏失败", false);
        }
    }
}
