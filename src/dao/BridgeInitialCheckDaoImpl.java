package dao;

import entity.BridgeInitialCheck;
import util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BridgeInitialCheckDaoImpl implements BridgeInitialCheckDao {
    
    @Override
    public boolean add(BridgeInitialCheck check) {
        String sql = "INSERT INTO bridge_initial_check (bridge_id, check_no, check_date, checker, weather, temperature, " +
                "check_content, deck_condition, superstructure_condition, substructure_condition, accessory_condition, " +
                "defect_desc, suggest, conclusion, next_check_date) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, check);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return false;
    }
    
    @Override
    public boolean update(BridgeInitialCheck check) {
        String sql = "UPDATE bridge_initial_check SET bridge_id=?, check_no=?, check_date=?, checker=?, weather=?, " +
                "temperature=?, check_content=?, deck_condition=?, superstructure_condition=?, substructure_condition=?, " +
                "accessory_condition=?, defect_desc=?, suggest=?, conclusion=?, next_check_date=? WHERE id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, check);
            pstmt.setInt(16, check.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM bridge_initial_check WHERE id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return false;
    }
    
    @Override
    public BridgeInitialCheck findById(int id) {
        String sql = "SELECT c.*, b.bridge_name FROM bridge_initial_check c LEFT JOIN bridge b ON c.bridge_id=b.id WHERE c.id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapCheck(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public List<BridgeInitialCheck> findAll() {
        String sql = "SELECT c.*, b.bridge_name FROM bridge_initial_check c LEFT JOIN bridge b ON c.bridge_id=b.id ORDER BY c.check_date DESC";
        List<BridgeInitialCheck> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapCheck(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<BridgeInitialCheck> findByBridgeId(int bridgeId) {
        String sql = "SELECT c.*, b.bridge_name FROM bridge_initial_check c LEFT JOIN bridge b ON c.bridge_id=b.id WHERE c.bridge_id=? ORDER BY c.check_date DESC";
        List<BridgeInitialCheck> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bridgeId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapCheck(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<BridgeInitialCheck> findByBridgeName(String bridgeName) {
        String sql = "SELECT c.*, b.bridge_name FROM bridge_initial_check c LEFT JOIN bridge b ON c.bridge_id=b.id WHERE b.bridge_name LIKE ? ORDER BY c.check_date DESC";
        List<BridgeInitialCheck> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + bridgeName + "%");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapCheck(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<BridgeInitialCheck> findByDateRange(String startDate, String endDate) {
        String sql = "SELECT c.*, b.bridge_name FROM bridge_initial_check c LEFT JOIN bridge b ON c.bridge_id=b.id WHERE c.check_date BETWEEN ? AND ? ORDER BY c.check_date DESC";
        List<BridgeInitialCheck> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapCheck(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM bridge_initial_check";
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
    
    private void setParams(PreparedStatement pstmt, BridgeInitialCheck c) throws SQLException {
        pstmt.setInt(1, c.getBridgeId());
        pstmt.setString(2, c.getCheckNo());
        pstmt.setString(3, c.getCheckDate());
        pstmt.setString(4, c.getChecker());
        pstmt.setString(5, c.getWeather());
        pstmt.setString(6, c.getTemperature());
        pstmt.setString(7, c.getCheckContent());
        pstmt.setString(8, c.getDeckCondition());
        pstmt.setString(9, c.getSuperstructureCondition());
        pstmt.setString(10, c.getSubstructureCondition());
        pstmt.setString(11, c.getAccessoryCondition());
        pstmt.setString(12, c.getDefectDesc());
        pstmt.setString(13, c.getSuggest());
        pstmt.setString(14, c.getConclusion());
        pstmt.setString(15, c.getNextCheckDate());
    }
    
    private BridgeInitialCheck mapCheck(ResultSet rs) throws SQLException {
        BridgeInitialCheck c = new BridgeInitialCheck();
        c.setId(rs.getInt("id"));
        c.setBridgeId(rs.getInt("bridge_id"));
        c.setBridgeName(rs.getString("bridge_name"));
        c.setCheckNo(rs.getString("check_no"));
        c.setCheckDate(rs.getString("check_date"));
        c.setChecker(rs.getString("checker"));
        c.setWeather(rs.getString("weather"));
        c.setTemperature(rs.getString("temperature"));
        c.setCheckContent(rs.getString("check_content"));
        c.setDeckCondition(rs.getString("deck_condition"));
        c.setSuperstructureCondition(rs.getString("superstructure_condition"));
        c.setSubstructureCondition(rs.getString("substructure_condition"));
        c.setAccessoryCondition(rs.getString("accessory_condition"));
        c.setDefectDesc(rs.getString("defect_desc"));
        c.setDefectPhoto(rs.getString("defect_photo"));
        c.setSuggest(rs.getString("suggest"));
        c.setConclusion(rs.getString("conclusion"));
        c.setNextCheckDate(rs.getString("next_check_date"));
        c.setCheckReport(rs.getString("check_report"));
        c.setCreateTime(rs.getTimestamp("create_time"));
        c.setUpdateTime(rs.getTimestamp("update_time"));
        return c;
    }
}
