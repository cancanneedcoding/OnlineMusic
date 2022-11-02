package com.example.onlimemusic.mapper;


import com.example.onlimemusic.model.Music;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoveMusicMapper {

    //查询某个人是否已经喜欢了某个音乐
    Music findLoveMusic(int userid, int musicid);

    //添加收藏
    boolean insertLoveMusic(int userid,int musicid);


    //删除收藏
    int deleteLoveMusic(int userid,int musicid);


    //查询用户收藏的音乐
    List<Music> findLoveMusicByUserid(int userid);


    //根据某个用户的ID和歌曲名称查询，某个用户收藏的音乐，对音乐名进行模糊查询
    List<Music> findLoveMusicBykeyAndUID(String musicname, int userid);

    //根据音乐的id进行删除
    int deleteLoveMusicById(int musicid);
}
