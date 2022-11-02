package com.example.onlimemusic.tools;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    //定义一个固定的盐值
    private static final String salt = "1b2i3t4e";

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    /**
     * 第一次加密 ：模拟前端自己加密，然后传到后端
     *
     * @param inputPass
     * @return
     */
    public static String inputPassToFormPass(String inputPass) {
        String str = "" + salt.charAt(1) + salt.charAt(3) + inputPass
                + salt.charAt(5) + salt.charAt(6);
        return md5(str);
    }

    /**
     * 第2次MD5加密
     *
     * @param formPass 前端加密过的密码，传给后端进行第2次加密
     * @param salt     用户数据库当中的盐值
     * @return
     */
    public static String formPassToDBPass(String formPass, String salt) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5)
                + salt.charAt(4);
        return md5(str);
    }

    /**
     * 上面两个函数合到一起进行调用
     *
     * @param inputPass
     *                  输出结果：
     *                  不管运行多少次，这个密码是规定的。因为这里没有用随机盐值。当密码长度很大，盐值也是随机的情况下，密码
     *                  的强度也加大了。破解成本也增加了。
     *                  4.2 BCrypt加密设计
     *                  Bcrypt就是一款加密工具，可以比较方便地实现数据的加密工作。你也可以简单理解为它内部自己实现了随机加盐
     *                  处理 。我们使用MD5加密，每次加密后的密文其实都是一样的，这样就方便了MD5通过大数据的方式进行破解。
     *                  Bcrypt生成的密文是60位的。而MD5的是32位的。Bcrypt破解难度更大。
     *                  添加依赖：
     *                  在springboot启动类添加：
     * @param saltDB
     * @return
     */
    public static String inputPassToDbPass(String inputPass, String saltDB) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, saltDB);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println("对用户输入密码进行第1次加密：" + inputPassToFormPass("123456"));
        System.out.println("对用户输入密码进行第2次加密：" + formPassToDBPass(inputPassToFormPass("123456"),
                "1b2i3t4e"));
        System.out.println("对用户输入密码进行第2次加密：" + inputPassToDbPass("123456", "1b2i3t4e"));
    }
}

