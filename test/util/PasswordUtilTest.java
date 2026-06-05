package util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 密码工具类单元测试
 */
public class PasswordUtilTest {

    @Test
    public void testEncryptAndVerify() {
        String password = "admin123";
        String salt = PasswordUtil.generateSalt();
        String hashed = PasswordUtil.encrypt(password, salt);

        assertNotNull(hashed);
        assertTrue(PasswordUtil.verify(password, salt, hashed));
        assertFalse(PasswordUtil.verify("wrongpassword", salt, hashed));
    }

    @Test
    public void testDifferentSalts() {
        String password = "test123";
        String salt1 = PasswordUtil.generateSalt();
        String salt2 = PasswordUtil.generateSalt();

        String hashed1 = PasswordUtil.encrypt(password, salt1);
        String hashed2 = PasswordUtil.encrypt(password, salt2);

        // 不同盐值产生的哈希不同
        assertNotEquals(hashed1, hashed2);

        // 但都能验证通过
        assertTrue(PasswordUtil.verify(password, salt1, hashed1));
        assertTrue(PasswordUtil.verify(password, salt2, hashed2));
    }

    @Test
    public void testSaltLength() {
        String salt = PasswordUtil.generateSalt();
        // 16字节 = 32个十六进制字符
        assertEquals(32, salt.length());
    }
}
