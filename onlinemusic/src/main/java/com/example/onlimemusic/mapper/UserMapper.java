package com.example.onlimemusic.mapper;


import com.example.onlimemusic.model.User;
import org.apache.ibatis.annotations.Mapper;


@Mapper
//书写Mapper文件
public interface UserMapper {
    //查询用户
    User login(User loginUser);

    //
    User selectByName(String username);
}
