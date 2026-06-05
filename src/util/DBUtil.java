package util;

import java.sql.*;

/**
 * 数据库工具类 (SQL Server版本)
 * 提供数据库连接、关闭、事务控制等基础操作
 *
 * 使用说明:
 * 1. 确保SQL Server已安装并运行
 * 2. 确保已创建数据库: CREATE DATABASE bridge_inspection;
 * 3. 确保SQL Server JDBC驱动(mssql-jdbc jar)已添加到项目依赖
 * 4. 在SSMS中启用sa账号并设置密码为123456（或修改下面PASSWORD）
 */
public class DBUtil {

    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=bridge_inspection;encrypt=false";

    private static final String USERNAME = "sa";
    private static final String PASSWORD = "123456";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            Logger.error("SQL Server JDBC驱动加载失败", e);
            throw new RuntimeException("SQL Server JDBC驱动加载失败，请确保mssql-jdbc驱动jar包已添加到项目中: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            Logger.error("关闭数据库资源失败", e);
        }
    }

    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }

    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            Logger.error("数据库连接测试失败", e);
            return false;
        } finally {
            close(conn, null);
        }
    }

    // ========== 事务控制方法 ==========

    public static void beginTransaction(Connection conn) throws SQLException {
        if (conn != null) {
            conn.setAutoCommit(false);
        }
    }

    public static void commit(Connection conn) {
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                Logger.error("事务提交失败", e);
            }
        }
    }

    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
                Logger.warn("事务已回滚");
            } catch (SQLException e) {
                Logger.error("事务回滚失败", e);
            }
        }
    }

    public static void endTransaction(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                Logger.error("结束事务失败", e);
            }
        }
    }
}
