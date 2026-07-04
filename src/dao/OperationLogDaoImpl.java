package dao;

import entity.OperationLog;
import util.DBUtil;
import util.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OperationLogDaoImpl implements OperationLogDao {

    @Override
    public boolean add(OperationLog log) {
        String sql = "INSERT INTO operation_log (user_id, username, operation_type, operation_desc, ip_address) VALUES (?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, log.getUserId());
            pstmt.setString(2, log.getUsername());
            pstmt.setString(3, log.getOperationType());
            pstmt.setString(4, log.getOperationDesc());
            pstmt.setString(5, log.getIpAddress());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("添加操作日志失败", e);
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return false;
    }

    @Override
    public List<OperationLog> findAll() {
        String sql = "SELECT * FROM operation_log ORDER BY operation_time DESC";
        List<OperationLog> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapLog(rs));
        } catch (SQLException e) {
            Logger.error("查询操作日志失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public List<OperationLog> findByUser(String username) {
        String sql = "SELECT * FROM operation_log WHERE username=? ORDER BY operation_time DESC";
        List<OperationLog> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapLog(rs));
        } catch (SQLException e) {
            Logger.error("按用户查询操作日志失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public List<OperationLog> findByType(String operationType) {
        String sql = "SELECT * FROM operation_log WHERE operation_type=? ORDER BY operation_time DESC";
        List<OperationLog> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, operationType);
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapLog(rs));
        } catch (SQLException e) {
            Logger.error("按类型查询操作日志失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public List<OperationLog> findRecent(int limit) {
        String sql = "SELECT TOP (?) * FROM operation_log ORDER BY operation_time DESC";
        List<OperationLog> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapLog(rs));
        } catch (SQLException e) {
            Logger.error("查询近期操作日志失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM operation_log";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            Logger.error("统计操作日志数量失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return 0;
    }

    private OperationLog mapLog(ResultSet rs) throws SQLException {
        OperationLog log = new OperationLog();
        log.setId(rs.getInt("id"));
        log.setUserId(rs.getInt("user_id"));
        log.setUsername(rs.getString("username"));
        log.setOperationType(rs.getString("operation_type"));
        log.setOperationDesc(rs.getString("operation_desc"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setOperationTime(rs.getTimestamp("operation_time"));
        return log;
    }
}
