package com.personal.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    private final static String SALT = "rd&*f1!50,12.df89z@5541q";

    /**
     * 对字符串进行MD5加密
     * 加密的字符串为  入参 str + Constant.SALT
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getMD5Str(String str) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return Base64.encodeBase64String(md5.digest((str + SALT).getBytes()));
    }
}
