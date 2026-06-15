package dao;

import entity.BridgeRegularCheck;
import util.DBUtil;
import util.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BridgeRegularCheckDaoImpl implements BridgeRegularCheckDao {
    
    @Override
    public boolean add(BridgeRegularCheck check) {
        String sql = "INSERT INTO bridge_regular_check (bridge_id, check_no, check_date, checker, weather, temperature, " +
                "check_type, deck_score, superstructure_score, substructure_score, accessory_score, bci, tech_status, " +
                "defect_desc, maintenance_suggest, limitation_suggest, check_conclusion, next_check_date) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, check);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("添加定期检查记录失败", e);
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return false;
    }

    @Override
    public boolean add(Connection conn, BridgeRegularCheck check) {
        String sql = "INSERT INTO bridge_regular_check (bridge_id, check_no, check_date, checker, weather, temperature, " +
                "check_type, deck_score, superstructure_score, substructure_score, accessory_score, bci, tech_status, " +
                "defect_desc, maintenance_suggest, limitation_suggest, check_conclusion, next_check_date) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, check);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("添加定期检查记录失败", e);
        } finally {
            DBUtil.close(null, pstmt);
        }
        return false;
    }

    @Override
    public int addAndGetId(Connection conn, BridgeRegularCheck check) {
        String sql = "INSERT INTO bridge_regular_check (bridge_id, check_no, check_date, checker, weather, temperature, " +
                "check_type, deck_score, superstructure_score, substructure_score, accessory_score, bci, tech_status, " +
                "defect_desc, maintenance_suggest, limitation_suggest, check_conclusion, next_check_date) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setParams(pstmt, check);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            Logger.error("添加定期检查记录并获取主键失败", e);
        } finally {
            DBUtil.close(null, pstmt, rs);
        }
        return -1;
    }

    @Override
    public boolean update(BridgeRegularCheck check) {
        String sql = "UPDATE bridge_regular_check SET bridge_id=?, check_no=?, check_date=?, checker=?, weather=?, " +
                "temperature=?, check_type=?, deck_score=?, superstructure_score=?, substructure_score=?, " +
                "accessory_score=?, bci=?, tech_status=?, defect_desc=?, maintenance_suggest=?, limitation_suggest=?, " +
                "check_conclusion=?, next_check_date=? WHERE id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, check);
            pstmt.setInt(19, check.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("更新定期检查记录失败", e);
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM bridge_regular_check WHERE id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("删除定期检查记录失败", e);
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return false;
    }

    @Override
    public BridgeRegularCheck findById(int id) {
        String sql = "SELECT c.*, b.bridge_name FROM bridge_regular_check c LEFT JOIN bridge b ON c.bridge_id=b.id WHERE c.id=?";
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
            Logger.error("查询定期检查记录失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }

    @Override
    public List<BridgeRegularCheck> findAll() {
        String sql = "SELECT c.*, b.bridge_name FROM bridge_regular_check c LEFT JOIN bridge b ON c.bridge_id=b.id ORDER BY c.check_date DESC";
        List<BridgeRegularCheck> list = new ArrayList<>();
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
            Logger.error("查询所有定期检查记录失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public List<BridgeRegularCheck> findByBridgeId(int bridgeId) {
        String sql = "SELECT c.*, b.bridge_name FROM bridge_regular_check c LEFT JOIN bridge b ON c.bridge_id=b.id WHERE c.bridge_id=? ORDER BY c.check_date DESC";
        List<BridgeRegularCheck> list = new ArrayList<>();
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
            Logger.error("按桥梁ID查询定期检查记录失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public List<BridgeRegularCheck> findByBridgeName(String bridgeName) {
        String sql = "SELECT c.*, b.bridge_name FROM bridge_regular_check c LEFT JOIN bridge b ON c.bridge_id=b.id WHERE b.bridge_name LIKE ? ORDER BY c.check_date DESC";
        List<BridgeRegularCheck> list = new ArrayList<>();
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
            Logger.error("按桥梁名称查询定期检查记录失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public List<BridgeRegularCheck> findByTechStatus(String techStatus) {
        String sql = "SELECT c.*, b.bridge_name FROM bridge_regular_check c LEFT JOIN bridge b ON c.bridge_id=b.id WHERE c.tech_status=? ORDER BY c.check_date DESC";
        List<BridgeRegularCheck> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, techStatus);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapCheck(rs));
            }
        } catch (SQLException e) {
            Logger.error("按技术状况查询定期检查记录失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public List<BridgeRegularCheck> findByDateRange(String startDate, String endDate) {
        String sql = "SELECT c.*, b.bridge_name FROM bridge_regular_check c LEFT JOIN bridge b ON c.bridge_id=b.id WHERE c.check_date BETWEEN ? AND ? ORDER BY c.check_date DESC";
        List<BridgeRegularCheck> list = new ArrayList<>();
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
            Logger.error("按日期范围查询定期检查记录失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM bridge_regular_check";
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
            Logger.error("统计定期检查记录数量失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return 0;
    }
    
    private void setParams(PreparedStatement pstmt, BridgeRegularCheck c) throws SQLException {
        pstmt.setInt(1, c.getBridgeId());
        pstmt.setString(2, c.getCheckNo());
        pstmt.setString(3, c.getCheckDate());
        pstmt.setString(4, c.getChecker());
        pstmt.setString(5, c.getWeather());
        pstmt.setString(6, c.getTemperature());
        pstmt.setString(7, c.getCheckType());
        pstmt.setInt(8, c.getDeckScore());
        pstmt.setInt(9, c.getSuperstructureScore());
        pstmt.setInt(10, c.getSubstructureScore());
        pstmt.setInt(11, c.getAccessoryScore());
        pstmt.setDouble(12, c.getBci());
        pstmt.setString(13, c.getTechStatus());
        pstmt.setString(14, c.getDefectDesc());
        pstmt.setString(15, c.getMaintenanceSuggest());
        pstmt.setString(16, c.getLimitationSuggest());
        pstmt.setString(17, c.getCheckConclusion());
        pstmt.setString(18, c.getNextCheckDate());
    }
    
    private BridgeRegularCheck mapCheck(ResultSet rs) throws SQLException {
        BridgeRegularCheck c = new BridgeRegularCheck();
        c.setId(rs.getInt("id"));
        c.setBridgeId(rs.getInt("bridge_id"));
        c.setBridgeName(rs.getString("bridge_name"));
        c.setCheckNo(rs.getString("check_no"));
        c.setCheckDate(rs.getString("check_date"));
        c.setChecker(rs.getString("checker"));
        c.setWeather(rs.getString("weather"));
        c.setTemperature(rs.getString("temperature"));
        c.setCheckType(rs.getString("check_type"));
        c.setDeckScore(rs.getInt("deck_score"));
        c.setSuperstructureScore(rs.getInt("superstructure_score"));
        c.setSubstructureScore(rs.getInt("substructure_score"));
        c.setAccessoryScore(rs.getInt("accessory_score"));
        c.setBci(rs.getDouble("bci"));
        c.setTechStatus(rs.getString("tech_status"));
        c.setDefectDesc(rs.getString("defect_desc"));
        c.setMaintenanceSuggest(rs.getString("maintenance_suggest"));
        c.setLimitationSuggest(rs.getString("limitation_suggest"));
        c.setCheckConclusion(rs.getString("check_conclusion"));
        c.setNextCheckDate(rs.getString("next_check_date"));
        c.setCreateTime(rs.getTimestamp("create_time"));
        c.setUpdateTime(rs.getTimestamp("update_time"));
        return c;
    }
}
