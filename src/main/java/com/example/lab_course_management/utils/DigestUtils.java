package com.example.lab_course_management.utils;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 摘要工具类
 *
 * @author dddwmx
 */
@Component
public class DigestUtils {

    /**
     * 盐值
     */
    private static final String SALT = "wmx";

    /**
     * 计算字符串的MD5摘要
     *
     * @param text 待计算字符串
     * @return MD5摘要字符串
     */
    public static String md5DigestAsHex(String text) {
        if (text == null) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }

    /**
     * 计算带盐值的MD5摘要
     *
     * @param text 待计算字符串
     * @param salt 盐值
     * @return MD5摘要字符串
     */
    public static String md5DigestAsHex(String text, String salt) {
        if (text == null) {
            return null;
        }
        if (salt == null) {
            salt = SALT;
        }
        return md5DigestAsHex(text + salt);
    }

    /**
     * 使用默认盐值计算MD5摘要
     *
     * @param text 待计算字符串
     * @return MD5摘要字符串
     */
    public static String md5DigestAsHexWithSalt(String text) {
        return md5DigestAsHex(text, SALT);
    }

    /**
     * 字节数组转换为十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 验证字符串的MD5摘要
     *
     * @param text 原始字符串
     * @param md5Hex MD5摘要
     * @return 是否匹配
     */
    public static boolean verifyMd5(String text, String md5Hex) {
        String calculatedMd5 = md5DigestAsHex(text);
        return calculatedMd5 != null && calculatedMd5.equals(md5Hex);
    }

    /**
     * 验证带盐值的MD5摘要
     *
     * @param text 原始字符串
     * @param salt 盐值
     * @param md5Hex MD5摘要
     * @return 是否匹配
     */
    public static boolean verifyMd5(String text, String salt, String md5Hex) {
        String calculatedMd5 = md5DigestAsHex(text, salt);
        return calculatedMd5 != null && calculatedMd5.equals(md5Hex);
    }

    /**
     * 验证默认盐值的MD5摘要
     *
     * @param text 原始字符串
     * @param md5Hex MD5摘要
     * @return 是否匹配
     */
    public static boolean verifyMd5WithSalt(String text, String md5Hex) {
        return verifyMd5(text, SALT, md5Hex);
    }
}