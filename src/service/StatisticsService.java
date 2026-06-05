package service;

import util.DBUtil;
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
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return 0;
    }
    
    /**
     * 执行分组计数查询
     */
    private Map<String, Integer> executeGroupCount(String sql, String keyCol, String valCol) {
        Map<String, Integer> map = new HashMap<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String key = rs.getString(keyCol);
                int val = rs.getInt(valCol);
                map.put(key != null ? key : "未知", val);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return map;
    }
}
