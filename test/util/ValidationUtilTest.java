package util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 数据校验工具类单元测试
 */
public class ValidationUtilTest {

    @Test
    public void testValidDate() {
        assertTrue(ValidationUtil.isValidDate("2024-01-15"));
        assertTrue(ValidationUtil.isValidDate("2023-12-31"));
        assertTrue(ValidationUtil.isValidDate("")); // 空值允许
        assertTrue(ValidationUtil.isValidDate(null)); // null允许
    }

    @Test
    public void testInvalidDate() {
        assertFalse(ValidationUtil.isValidDate("2024-13-01")); // 无效月份
        assertFalse(ValidationUtil.isValidDate("2024-01-32")); // 无效日期
        assertFalse(ValidationUtil.isValidDate("01-15-2024")); // 错误格式
        assertFalse(ValidationUtil.isValidDate("2024/01/15")); // 错误分隔符
    }

    @Test
    public void testValidLongitude() {
        assertTrue(ValidationUtil.isValidLongitude("106.55"));
        assertTrue(ValidationUtil.isValidLongitude("-122.45"));
        assertTrue(ValidationUtil.isValidLongitude("0"));
        assertTrue(ValidationUtil.isValidLongitude("180"));
        assertTrue(ValidationUtil.isValidLongitude("-180"));
        assertTrue(ValidationUtil.isValidLongitude("")); // 空值允许
    }

    @Test
    public void testInvalidLongitude() {
        assertFalse(ValidationUtil.isValidLongitude("181")); // 超出范围
        assertFalse(ValidationUtil.isValidLongitude("-181")); // 超出范围
        assertFalse(ValidationUtil.isValidLongitude("abc")); // 非数字
    }

    @Test
    public void testValidLatitude() {
        assertTrue(ValidationUtil.isValidLatitude("29.57"));
        assertTrue(ValidationUtil.isValidLatitude("-45.0"));
        assertTrue(ValidationUtil.isValidLatitude("0"));
        assertTrue(ValidationUtil.isValidLatitude("90"));
        assertTrue(ValidationUtil.isValidLatitude("-90"));
        assertTrue(ValidationUtil.isValidLatitude("")); // 空值允许
    }

    @Test
    public void testInvalidLatitude() {
        assertFalse(ValidationUtil.isValidLatitude("91")); // 超出范围
        assertFalse(ValidationUtil.isValidLatitude("-91")); // 超出范围
        assertFalse(ValidationUtil.isValidLatitude("abc")); // 非数字
    }

    @Test
    public void testValidScore() {
        assertTrue(ValidationUtil.isValidScore(0));
        assertTrue(ValidationUtil.isValidScore(50));
        assertTrue(ValidationUtil.isValidScore(100));
    }

    @Test
    public void testInvalidScore() {
        assertFalse(ValidationUtil.isValidScore(-1));
        assertFalse(ValidationUtil.isValidScore(101));
    }

    @Test
    public void testIsNotEmpty() {
        assertTrue(ValidationUtil.isNotEmpty("hello"));
        assertTrue(ValidationUtil.isNotEmpty("  hello  "));
        assertFalse(ValidationUtil.isNotEmpty(""));
        assertFalse(ValidationUtil.isNotEmpty("   "));
        assertFalse(ValidationUtil.isNotEmpty(null));
    }
}
