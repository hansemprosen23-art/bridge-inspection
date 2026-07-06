package util;

import java.sql.*;

/**
 * 数据库工具类 (SQL Server版本)
 * 提供数据库连接、关闭、事务控制等基础操作
 *
 * 高级优化：
 * 1. 使用内置连接池管理数据库连接，减少连接创建/销毁开销
 * 2. 连接池支持最大连接数限制和连接复用
 * 3. 保留原始 DriverManager 直连作为降级方案
 *
 * 使用说明:
 * 1. 确保SQL Server已安装并运行
 * 2. 确保已创建数据库: CREATE DATABASE bridge_inspection;
 * 3. 确保SQL Server JDBC驱动(mssql-jdbc jar)已添加到项目依赖
 * 4. 在SSMS中启用sa账号并设置密码为123456（或修改下面PASSWORD）
 */
public class DBUtil {

    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    // 使用固定端口 1433 直连（已在 SQL Server 配置管理器中确认）
    // trustServerCertificate=true 解决 SQL Server 2022 驱动 SSL 握手问题
    private static final String URL = "jdbc:sqlserver://127.0.0.1:1433;databaseName=bridge_inspection;encrypt=false;trustServerCertificate=true;loginTimeout=5;socketTimeout=10";

    private static final String USERNAME = "sa";
    private static final String PASSWORD = "123456";

    // 连接池开关：如果遇到连接问题，可临时设为 false 使用直连（排查数据库问题时常用）
    private static final boolean USE_CONNECTION_POOL = true;

    // 内置连接池实例
    private static ConnectionPool connectionPool;

    static {
        try {
            Class.forName(DRIVER);
            if (USE_CONNECTION_POOL) {
                connectionPool = new ConnectionPool(URL, USERNAME, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            Logger.error("SQL Server JDBC驱动加载失败", e);
            throw new RuntimeException("SQL Server JDBC驱动加载失败，请确保mssql-jdbc驱动jar包已添加到项目中: " + e.getMessage());
        }
    }

    /**
     * 获取数据库连接
     * 优先从连接池获取，如果连接池未启用则新建连接
     */
    public static Connection getConnection() throws SQLException {
        if (USE_CONNECTION_POOL && connectionPool != null) {
            return connectionPool.getConnection();
        }
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

    /**
     * 获取连接池状态信息（用于系统维护面板显示）
     */
    public static String getPoolStatus() {
        if (USE_CONNECTION_POOL && connectionPool != null) {
            return "连接池已启用，当前活跃连接: " + connectionPool.getActiveConnections();
        }
        return "连接池未启用";
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
