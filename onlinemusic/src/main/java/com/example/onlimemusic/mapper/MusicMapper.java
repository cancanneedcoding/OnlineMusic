package com.example.onlimemusic.mapper;


import com.example.onlimemusic.model.Music;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MusicMapper {

     int insert(String title,String singer,String time,String url,int userid);


     Music select(String title,String singer);

     //根据id删除音乐
     int delete(Integer id);

     //根据id查询音乐
     Music selectById(Integer id);

     //查询所有的音乐
     List<Music> findMusic();


     //查询指定name的音乐
     List<Music> findMusicByName(String musicName);
}
