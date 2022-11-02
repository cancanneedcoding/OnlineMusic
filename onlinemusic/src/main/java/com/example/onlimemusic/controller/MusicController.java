package com.example.onlimemusic.controller;


import com.example.onlimemusic.mapper.LoveMusicMapper;
import com.example.onlimemusic.mapper.MusicMapper;
import com.example.onlimemusic.model.Music;
import com.example.onlimemusic.model.User;
import com.example.onlimemusic.tools.Constant;
import com.example.onlimemusic.tools.ResponseBodyMessage;
import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/music")
public class MusicController {

    //Linux环境下使用
    private static final String SAVE_PATH = "/root/music/";

    //本地环境下使用

    //private final String SAVE_PATH = "C:/work/local/music1/";

    @Autowired
    private MusicMapper musicMapper;

    @Autowired
    private LoveMusicMapper loveMusicMapper;

    @RequestMapping("/upload")
    public ResponseBodyMessage<Boolean> insertMusic(@RequestParam String singer,
                                                    @RequestParam("filename") MultipartFile file,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) throws IOException {
        //1.检查是否登录了
        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            System.out.println("没有登录！");
            return new ResponseBodyMessage<>(-1, "请登录后再上传", false);
        }


        //3.获取上传的文件名称
        String fileNameAndType = file.getOriginalFilename(); //xxx.mp3

        //把title先得到
        String title = fileNameAndType.substring(0, fileNameAndType.lastIndexOf('.'));
        //2.先查询数据库是否有相同的音乐 【判断逻辑是否有歌曲名和歌手一样认为是一样的】
        Music music = musicMapper.select(title, singer);
        if (music != null) {
            //说明已经有了这个文件
            System.out.println("该音乐文件已存在");
            return new ResponseBodyMessage<>(-1, "该音乐文件已存在", false);
        }

        System.out.println("fileNameAdnType" + fileNameAndType);

        String path = SAVE_PATH + fileNameAndType;

        File dest = new File(path);

        //如果没有这个路径，就生成一个
        if (!dest.exists()) {
            dest.mkdir();
        }

        try {
            file.transferTo(dest);
            /*return new ResponseBodyMessage<>(0, "上传成功！", true);*/
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseBodyMessage<>(-1, "服务器上传失败", false);
        }


        //进行数据库的上传
        //1.准备数据   调用insert进行上传
        //得到文件名


        //得到用户id
        int userid = ((User) httpSession.getAttribute(Constant.USERINFO_SESSION_KEY)).getId();

        //得到url 播放音乐->http请求   存进去不用加后缀.mp3
        String url = "/music/get?path=" + title;

        //得到时间
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        //格式化成字符串
        String time = sf.format(new Date());

        try {
            //放入数据库
            int ret = musicMapper.insert(title, singer, time, url, userid);
            if (ret == 1) {
                //这里应该跳转到音乐列表界面
                response.sendRedirect("/list.html");
                return new ResponseBodyMessage<>(0, "数据库上传成功！", true);
            } else {
                return new ResponseBodyMessage<>(-1, "数据库上传失败", false);
            }
        } catch (BindingException e) {
            dest.delete();
            return new ResponseBodyMessage<>(-1, "数据库上传失败", false);
        }

        //另外一个问题-> 如果重复上传一首歌曲，能否上传成功
    }

    //播放音乐的时候： /music/get?path=xxx.mp3
    @RequestMapping("/get")
    public ResponseEntity<byte[]> get(String path) {
        try {
            byte[] bytes = Files.readAllBytes(new File(SAVE_PATH + File.separator + path).toPath());
            return ResponseEntity.ok(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //根据id删除音乐
    @RequestMapping("/delete")
    public ResponseBodyMessage<Boolean> deleteMusicById(@RequestParam Integer id) {
        //1.先查一下这个id的音乐是否存在
        Music music = musicMapper.selectById(id);
        if (music == null) {
            System.out.println("不存在该音乐");
            return new ResponseBodyMessage<>(-1, "不存在该音乐", false);
        }
        //2.进行删除
        //2.1 现在数据库里删除
        int ret = musicMapper.delete(id);
        if (ret == 1) {
            //2.2说明数据库删除成功，删除服务器上的数据
            String filename = music.getTitle();
            File file = new File(SAVE_PATH  + filename + ".mp3");
            System.out.println("当前的路径" + file.getPath());

            if (file.delete()) {
                //TODO 同步删除lovamusic表当中的音乐
                loveMusicMapper.deleteLoveMusicById(music.getId());
                return new ResponseBodyMessage<>(1, "服务器中的音乐删除成功！", true);
            } else {
                return new ResponseBodyMessage<>(-1, "服务器中的音乐删除失败！", false);
            }
        }
        {
            //2.3说明数据库没有删除成功
            return new ResponseBodyMessage<>(-1, "数据库当中的音乐没有删除成功", false);
        }
    }


    @RequestMapping("deleteSel")
    //批量删除音乐
    public ResponseBodyMessage<Boolean> deleteSelMusic(@RequestParam("id[]") List<Integer> id) {
        for (int i : id) {
            Music music = musicMapper.selectById(i);
            if (music == null) {
                System.out.println("编号为" + i + "的音乐不存在");
                return new ResponseBodyMessage<>(-1, "编号为" + i + "的音乐不存在", false);
            }
            int ret = musicMapper.delete(i);
            if (ret == 1) {
                //数据库删除成功，那么服务器跟着一起删除
                //TODO 同步删除lovamusic表当中的音乐
                loveMusicMapper.deleteLoveMusicById(music.getId());
                File file = new File(SAVE_PATH  + music.getTitle() + ".mp3");
                if (!file.delete()) {
                    System.out.println("编号为" + i + "的音乐在服务器删除失败");
                    return new ResponseBodyMessage<>(-1, "编号为" + i + "的音乐在服务器删除失败", false);
                }
            } else {
                System.out.println("编号为" + i + "的音乐在数据库删除失败");
                return new ResponseBodyMessage<>(-1, "编号为" + i + "的音乐在服务器删除失败", false);
            }
        }
        System.out.println("批量删除成功");
        return new ResponseBodyMessage<>(1, "批量删除成功", true);
    }


    @RequestMapping("/findmusic")
    public ResponseBodyMessage<List<Music>> findMusic(@RequestParam(required = false) String musicName) {
        List<Music> list;
        if (musicName != null) {
            System.out.println(musicName);
            list = musicMapper.findMusicByName(musicName);
        } else {
            list = musicMapper.findMusic();
        }
        return new ResponseBodyMessage<>(0, "查询到了所有的音乐", list);
    }

}
