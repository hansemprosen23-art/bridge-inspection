package service;

import util.DBUtil;
import util.Logger;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 统计查询业务逻辑层
 * 负责模块: 曹城钧
 */
public class StatisticsService {
    
    private static StatisticsService instance;
    
    private StatisticsService() {}
    
    public static synchronized StatisticsService getInstance() {
        if (instance == null) {
            instance = new StatisticsService();
        }
        return instance;
    }
    
    /**
     * 一次性获取所有统计数据，使用单个连接减少数据库往返
     */
    public StatisticsResult getAllStatistics() {
        StatisticsResult result = new StatisticsResult();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();

            // 三个总数
            pstmt = conn.prepareStatement("SELECT " +
                "(SELECT COUNT(*) FROM bridge) as bridge_count, " +
                "(SELECT COUNT(*) FROM bridge_initial_check) as initial_count, " +
                "(SELECT COUNT(*) FROM bridge_regular_check) as regular_count");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result.totalBridges = rs.getInt("bridge_count");
                result.totalInitialChecks = rs.getInt("initial_count");
                result.totalRegularChecks = rs.getInt("regular_count");
            }
            DBUtil.close(null, pstmt, rs);

            // 按桥梁类型
            result.byBridgeType = executeGroupCount(conn, "SELECT bridge_type, COUNT(*) as cnt FROM bridge GROUP BY bridge_type", "bridge_type", "cnt");
            // 按检查等级
            result.byCheckLevel = executeGroupCount(conn, "SELECT check_level, COUNT(*) as cnt FROM bridge GROUP BY check_level", "check_level", "cnt");
            // 按技术状况
            result.byTechStatus = executeGroupCount(conn, "SELECT tech_status, COUNT(*) as cnt FROM bridge_regular_check GROUP BY tech_status", "tech_status", "cnt");
            // 按年份
            result.byYear = executeGroupCount(conn, "SELECT YEAR(check_date) as year_val, COUNT(*) as cnt FROM bridge_regular_check GROUP BY YEAR(check_date) ORDER BY year_val", "year_val", "cnt");

        } catch (SQLException e) {
            Logger.error("获取所有统计数据失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return result;
    }

    /**
     * 获取桥梁总数
     */
    public int getTotalBridges() {
        return executeCount("SELECT COUNT(*) FROM bridge");
    }

    /**
     * 获取初始检查记录总数
     */
    public int getTotalInitialChecks() {
        return executeCount("SELECT COUNT(*) FROM bridge_initial_check");
    }

    /**
     * 获取定期检查记录总数
     */
    public int getTotalRegularChecks() {
        return executeCount("SELECT COUNT(*) FROM bridge_regular_check");
    }

    /**
     * 按桥梁类型统计
     */
    public Map<String, Integer> countByBridgeType() {
        String sql = "SELECT bridge_type, COUNT(*) as cnt FROM bridge GROUP BY bridge_type";
        return executeGroupCount(sql, "bridge_type", "cnt");
    }

    /**
     * 按检查等级统计
     */
    public Map<String, Integer> countByCheckLevel() {
        String sql = "SELECT check_level, COUNT(*) as cnt FROM bridge GROUP BY check_level";
        return executeGroupCount(sql, "check_level", "cnt");
    }

    /**
     * 按技术状况等级统计
     */
    public Map<String, Integer> countByTechStatus() {
        String sql = "SELECT tech_status, COUNT(*) as cnt FROM bridge_regular_check GROUP BY tech_status";
        return executeGroupCount(sql, "tech_status", "cnt");
    }

    /**
     * 按年份统计检查次数
     */
    public Map<String, Integer> countChecksByYear() {
        String sql = "SELECT YEAR(check_date) as year_val, COUNT(*) as cnt FROM bridge_regular_check GROUP BY YEAR(check_date) ORDER BY year_val";
        return executeGroupCount(sql, "year_val", "cnt");
    }

    /**
     * 执行计数查询
     */
    private int executeCount(String sql) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            Logger.error("统计查询失败: " + sql, e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return 0;
    }

    /**
     * 执行分组计数查询（使用已有连接）
     */
    private Map<String, Integer> executeGroupCount(Connection conn, String sql, String keyCol, String valCol) {
        Map<String, Integer> map = new HashMap<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String key = rs.getString(keyCol);
                int val = rs.getInt(valCol);
                map.put(key != null ? key : "未知", val);
            }
        } catch (SQLException e) {
            Logger.error("分组统计查询失败: " + sql, e);
        } finally {
            DBUtil.close(null, pstmt, rs);
        }
        return map;
    }

    /**
     * 执行分组计数查询
     */
    private Map<String, Integer> executeGroupCount(String sql, String keyCol, String valCol) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            return executeGroupCount(conn, sql, keyCol, valCol);
        } catch (SQLException e) {
            Logger.error("分组统计查询失败: " + sql, e);
            return new HashMap<>();
        } finally {
            DBUtil.close(conn, null);
        }
    }

    /**
     * 统计结果包装类
     */
    public static class StatisticsResult {
        public int totalBridges;
        public int totalInitialChecks;
        public int totalRegularChecks;
        public Map<String, Integer> byBridgeType = new HashMap<>();
        public Map<String, Integer> byCheckLevel = new HashMap<>();
        public Map<String, Integer> byTechStatus = new HashMap<>();
        public Map<String, Integer> byYear = new HashMap<>();
    }
}
