package dao;

import entity.BridgeComponentScore;
import util.DBUtil;
import util.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BridgeComponentScoreDaoImpl implements BridgeComponentScoreDao {

    @Override
    public boolean add(BridgeComponentScore score) {
        String sql = "INSERT INTO bridge_component_score (regular_check_id, category, component_name, score, weight, defect_desc, deduct_reason) VALUES (?,?,?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, score);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("添加部件评分失败", e);
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return false;
    }

    @Override
    public boolean update(BridgeComponentScore score) {
        String sql = "UPDATE bridge_component_score SET regular_check_id=?, category=?, component_name=?, score=?, weight=?, defect_desc=?, deduct_reason=? WHERE id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, score);
            pstmt.setInt(8, score.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("更新部件评分失败", e);
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM bridge_component_score WHERE id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("删除部件评分失败", e);
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return false;
    }

    @Override
    public boolean deleteByCheckId(int regularCheckId) {
        String sql = "DELETE FROM bridge_component_score WHERE regular_check_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, regularCheckId);
            return pstmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            Logger.error("按检查ID删除部件评分失败", e);
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return false;
    }

    @Override
    public BridgeComponentScore findById(int id) {
        String sql = "SELECT * FROM bridge_component_score WHERE id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) return mapScore(rs);
        } catch (SQLException e) {
            Logger.error("查询部件评分失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }

    @Override
    public List<BridgeComponentScore> findByCheckId(int regularCheckId) {
        String sql = "SELECT * FROM bridge_component_score WHERE regular_check_id=? ORDER BY category, id";
        List<BridgeComponentScore> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, regularCheckId);
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapScore(rs));
        } catch (SQLException e) {
            Logger.error("按检查ID查询部件评分失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public List<BridgeComponentScore> findByCheckIdAndCategory(int regularCheckId, String category) {
        String sql = "SELECT * FROM bridge_component_score WHERE regular_check_id=? AND category=? ORDER BY id";
        List<BridgeComponentScore> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, regularCheckId);
            pstmt.setString(2, category);
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapScore(rs));
        } catch (SQLException e) {
            Logger.error("按检查ID和分类查询部件评分失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    private void setParams(PreparedStatement pstmt, BridgeComponentScore s) throws SQLException {
        pstmt.setInt(1, s.getRegularCheckId());
        pstmt.setString(2, s.getCategory());
        pstmt.setString(3, s.getComponentName());
        pstmt.setDouble(4, s.getScore());
        pstmt.setDouble(5, s.getWeight());
        pstmt.setString(6, s.getDefectDesc());
        pstmt.setString(7, s.getDeductReason());
    }

    private BridgeComponentScore mapScore(ResultSet rs) throws SQLException {
        BridgeComponentScore s = new BridgeComponentScore();
        s.setId(rs.getInt("id"));
        s.setRegularCheckId(rs.getInt("regular_check_id"));
        s.setCategory(rs.getString("category"));
        s.setComponentName(rs.getString("component_name"));
        s.setScore(rs.getDouble("score"));
        s.setWeight(rs.getDouble("weight"));
        s.setDefectDesc(rs.getString("defect_desc"));
        s.setDeductReason(rs.getString("deduct_reason"));
        s.setCreateTime(rs.getTimestamp("create_time"));
        s.setUpdateTime(rs.getTimestamp("update_time"));
        return s;
    }
}
