package util;

import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

/**
 * 简易数据库连接池
 * 使用 Apache DBCP 或 HikariCP 是生产环境推荐方案
 * 本实现为教学演示用途，展示连接池基本概念
 *
 * 连接池优势：
 * 1. 减少连接创建/销毁开销
 * 2. 限制最大连接数，防止数据库过载
 * 3. 连接复用，提高响应速度
 */
public class ConnectionPool {

    private static final int MAX_POOL_SIZE = 10;
    private static final int INITIAL_POOL_SIZE = 3;
    private static final long CONNECTION_TIMEOUT_MS = 5000;

    private final BlockingQueue<Connection> pool;
    private final String url;
    private final String username;
    private final String password;
    private volatile boolean shutdown = false;

    private static ConnectionPool instance;

    public static synchronized ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool(
                "jdbc:sqlserver://localhost:1433;databaseName=bridge_inspection;encrypt=false",
                "sa",
                "123456"
            );
        }
        return instance;
    }

    private ConnectionPool(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.pool = new ArrayBlockingQueue<>(MAX_POOL_SIZE);
        initializePool();
    }

    private void initializePool() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                Connection conn = createNewConnection();
                if (conn != null) {
                    pool.offer(conn);
                }
            }
            Logger.info("连接池初始化完成，初始连接数: " + pool.size());
        } catch (ClassNotFoundException e) {
            Logger.error("JDBC驱动加载失败", e);
            throw new RuntimeException("JDBC驱动加载失败", e);
        }
    }

    private Connection createNewConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            Logger.error("创建数据库连接失败", e);
            return null;
        }
    }

    /**
     * 获取连接
     */
    public Connection getConnection() throws SQLException {
        if (shutdown) {
            throw new SQLException("连接池已关闭");
        }

        Connection conn = pool.poll();
        if (conn == null || conn.isClosed()) {
            conn = createNewConnection();
        }

        if (conn == null) {
            throw new SQLException("无法获取数据库连接，连接池已满");
        }

        // 包装连接，归还时自动回到连接池
        return new PooledConnection(conn, this);
    }

    /**
     * 归还连接到池
     */
    void returnConnection(Connection conn) {
        if (shutdown || conn == null) {
            return;
        }
        try {
            if (!conn.isClosed() && pool.size() < MAX_POOL_SIZE) {
                pool.offer(conn);
            } else {
                conn.close();
            }
        } catch (SQLException e) {
            Logger.error("归还连接失败", e);
        }
    }

    /**
     * 关闭连接池
     */
    public void shutdown() {
        shutdown = true;
        for (Connection conn : pool) {
            try {
                conn.close();
            } catch (SQLException e) {
                Logger.error("关闭连接失败", e);
            }
        }
        pool.clear();
        Logger.info("连接池已关闭");
    }

    public int getActiveConnections() {
        return pool.size();
    }

    /**
     * 连接池中的连接包装类
     * 关闭时自动归还到连接池
     */
    private static class PooledConnection implements Connection {
        private final Connection realConnection;
        private final ConnectionPool pool;
        private boolean closed = false;

        PooledConnection(Connection realConnection, ConnectionPool pool) {
            this.realConnection = realConnection;
            this.pool = pool;
        }

        @Override
        public void close() throws SQLException {
            if (!closed) {
                closed = true;
                pool.returnConnection(realConnection);
            }
        }

        @Override
        public boolean isClosed() throws SQLException {
            return closed || realConnection.isClosed();
        }

        // 委托所有其他方法到真实连接
        @Override public Statement createStatement() throws SQLException { return realConnection.createStatement(); }
        @Override public PreparedStatement prepareStatement(String sql) throws SQLException { return realConnection.prepareStatement(sql); }
        @Override public CallableStatement prepareCall(String sql) throws SQLException { return realConnection.prepareCall(sql); }
        @Override public String nativeSQL(String sql) throws SQLException { return realConnection.nativeSQL(sql); }
        @Override public void setAutoCommit(boolean autoCommit) throws SQLException { realConnection.setAutoCommit(autoCommit); }
        @Override public boolean getAutoCommit() throws SQLException { return realConnection.getAutoCommit(); }
        @Override public void commit() throws SQLException { realConnection.commit(); }
        @Override public void rollback() throws SQLException { realConnection.rollback(); }
        @Override public DatabaseMetaData getMetaData() throws SQLException { return realConnection.getMetaData(); }
        @Override public void setReadOnly(boolean readOnly) throws SQLException { realConnection.setReadOnly(readOnly); }
        @Override public boolean isReadOnly() throws SQLException { return realConnection.isReadOnly(); }
        @Override public void setCatalog(String catalog) throws SQLException { realConnection.setCatalog(catalog); }
        @Override public String getCatalog() throws SQLException { return realConnection.getCatalog(); }
        @Override public void setTransactionIsolation(int level) throws SQLException { realConnection.setTransactionIsolation(level); }
        @Override public int getTransactionIsolation() throws SQLException { return realConnection.getTransactionIsolation(); }
        @Override public SQLWarning getWarnings() throws SQLException { return realConnection.getWarnings(); }
        @Override public void clearWarnings() throws SQLException { realConnection.clearWarnings(); }
        @Override public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException { return realConnection.createStatement(resultSetType, resultSetConcurrency); }
        @Override public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return realConnection.prepareStatement(sql, resultSetType, resultSetConcurrency); }
        @Override public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return realConnection.prepareCall(sql, resultSetType, resultSetConcurrency); }
        @Override public java.util.Map<String, Class<?>> getTypeMap() throws SQLException { return realConnection.getTypeMap(); }
        @Override public void setTypeMap(java.util.Map<String, Class<?>> map) throws SQLException { realConnection.setTypeMap(map); }
        @Override public void setHoldability(int holdability) throws SQLException { realConnection.setHoldability(holdability); }
        @Override public int getHoldability() throws SQLException { return realConnection.getHoldability(); }
        @Override public Savepoint setSavepoint() throws SQLException { return realConnection.setSavepoint(); }
        @Override public Savepoint setSavepoint(String name) throws SQLException { return realConnection.setSavepoint(name); }
        @Override public void rollback(Savepoint savepoint) throws SQLException { realConnection.rollback(savepoint); }
        @Override public void releaseSavepoint(Savepoint savepoint) throws SQLException { realConnection.releaseSavepoint(savepoint); }
        @Override public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return realConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability); }
        @Override public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return realConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability); }
        @Override public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return realConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability); }
        @Override public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException { return realConnection.prepareStatement(sql, autoGeneratedKeys); }
        @Override public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException { return realConnection.prepareStatement(sql, columnIndexes); }
        @Override public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException { return realConnection.prepareStatement(sql, columnNames); }
        @Override public Clob createClob() throws SQLException { return realConnection.createClob(); }
        @Override public Blob createBlob() throws SQLException { return realConnection.createBlob(); }
        @Override public NClob createNClob() throws SQLException { return realConnection.createNClob(); }
        @Override public SQLXML createSQLXML() throws SQLException { return realConnection.createSQLXML(); }
        @Override public boolean isValid(int timeout) throws SQLException { return realConnection.isValid(timeout); }
        @Override public void setClientInfo(String name, String value) throws SQLClientInfoException { realConnection.setClientInfo(name, value); }
        @Override public void setClientInfo(java.util.Properties properties) throws SQLClientInfoException { realConnection.setClientInfo(properties); }
        @Override public String getClientInfo(String name) throws SQLException { return realConnection.getClientInfo(name); }
        @Override public java.util.Properties getClientInfo() throws SQLException { return realConnection.getClientInfo(); }
        @Override public Array createArrayOf(String typeName, Object[] elements) throws SQLException { return realConnection.createArrayOf(typeName, elements); }
        @Override public Struct createStruct(String typeName, Object[] attributes) throws SQLException { return realConnection.createStruct(typeName, attributes); }
        @Override public void setSchema(String schema) throws SQLException { realConnection.setSchema(schema); }
        @Override public String getSchema() throws SQLException { return realConnection.getSchema(); }
        @Override public void abort(Executor executor) throws SQLException { realConnection.abort(executor); }
        @Override public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException { realConnection.setNetworkTimeout(executor, milliseconds); }
        @Override public int getNetworkTimeout() throws SQLException { return realConnection.getNetworkTimeout(); }
        @Override public <T> T unwrap(Class<T> iface) throws SQLException { return realConnection.unwrap(iface); }
        @Override public boolean isWrapperFor(Class<?> iface) throws SQLException { return realConnection.isWrapperFor(iface); }
    }
}
