package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 密码加密工具类
 * 使用 SHA-256 + 盐值 加密
 */
public class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";

    /**
     * 生成随机盐值
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return bytesToHex(bytes);
    }

    /**
     * 加密密码（密码 + 盐值）
     */
    public static String encrypt(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt.getBytes());
            byte[] hashed = md.digest(password.getBytes());
            return bytesToHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("加密算法不可用", e);
        }
    }

    /**
     * 验证密码
     */
    public static boolean verify(String password, String salt, String hashedPassword) {
        return encrypt(password, salt).equals(hashedPassword);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
