package dao;

import entity.Bridge;
import util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BridgeDaoImpl implements BridgeDao {
    
    @Override
    public boolean add(Bridge bridge) {
        String sql = "INSERT INTO bridge (bridge_no, bridge_name, route_name, route_grade, bridge_type, structure_type, " +
                "span_combination, total_length, total_width, clear_span, design_load, anti_seismic, design_unit, " +
                "construct_unit, supervise_unit, complete_date, open_date, manage_unit, maintain_unit, check_level, " +
                "tech_status, maintenance_length, longitude, latitude, remark) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, bridge);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return false;
    }
    
    @Override
    public boolean update(Bridge bridge) {
        String sql = "UPDATE bridge SET bridge_no=?, bridge_name=?, route_name=?, route_grade=?, bridge_type=?, " +
                "structure_type=?, span_combination=?, total_length=?, total_width=?, clear_span=?, design_load=?, " +
                "anti_seismic=?, design_unit=?, construct_unit=?, supervise_unit=?, complete_date=?, open_date=?, " +
                "manage_unit=?, maintain_unit=?, check_level=?, tech_status=?, maintenance_length=?, longitude=?, " +
                "latitude=?, remark=? WHERE id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, bridge);
            pstmt.setInt(26, bridge.getId());
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
        String sql = "DELETE FROM bridge WHERE id=?";
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
    public Bridge findById(int id) {
        String sql = "SELECT * FROM bridge WHERE id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapBridge(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public Bridge findByBridgeNo(String bridgeNo) {
        String sql = "SELECT * FROM bridge WHERE bridge_no=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bridgeNo);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapBridge(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }
    
    @Override
    public List<Bridge> findAll() {
        String sql = "SELECT * FROM bridge ORDER BY id";
        List<Bridge> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapBridge(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<Bridge> findByName(String name) {
        String sql = "SELECT * FROM bridge WHERE bridge_name LIKE ? ORDER BY id";
        List<Bridge> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + name + "%");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapBridge(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<Bridge> findByType(String type) {
        String sql = "SELECT * FROM bridge WHERE bridge_type=? ORDER BY id";
        List<Bridge> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, type);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapBridge(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
    
    @Override
    public List<Bridge> findByLevel(String level) {
        String sql = "SELECT * FROM bridge WHERE check_level=? ORDER BY id";
        List<Bridge> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, level);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapBridge(rs));
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
        String sql = "SELECT COUNT(*) FROM bridge";
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
    
    private void setParams(PreparedStatement pstmt, Bridge b) throws SQLException {
        pstmt.setString(1, b.getBridgeNo());
        pstmt.setString(2, b.getBridgeName());
        pstmt.setString(3, b.getRouteName());
        pstmt.setString(4, b.getRouteGrade());
        pstmt.setString(5, b.getBridgeType());
        pstmt.setString(6, b.getStructureType());
        pstmt.setString(7, b.getSpanCombination());
        pstmt.setDouble(8, b.getTotalLength());
        pstmt.setDouble(9, b.getTotalWidth());
        pstmt.setDouble(10, b.getClearSpan());
        pstmt.setString(11, b.getDesignLoad());
        pstmt.setString(12, b.getAntiSeismic());
        pstmt.setString(13, b.getDesignUnit());
        pstmt.setString(14, b.getConstructUnit());
        pstmt.setString(15, b.getSuperviseUnit());
        pstmt.setString(16, b.getCompleteDate());
        pstmt.setString(17, b.getOpenDate());
        pstmt.setString(18, b.getManageUnit());
        pstmt.setString(19, b.getMaintainUnit());
        pstmt.setString(20, b.getCheckLevel());
        pstmt.setInt(21, b.getTechStatus());
        pstmt.setDouble(22, b.getMaintenanceLength());
        pstmt.setString(23, b.getLongitude());
        pstmt.setString(24, b.getLatitude());
        pstmt.setString(25, b.getRemark());
    }
    
    private Bridge mapBridge(ResultSet rs) throws SQLException {
        Bridge b = new Bridge();
        b.setId(rs.getInt("id"));
        b.setBridgeNo(rs.getString("bridge_no"));
        b.setBridgeName(rs.getString("bridge_name"));
        b.setRouteName(rs.getString("route_name"));
        b.setRouteGrade(rs.getString("route_grade"));
        b.setBridgeType(rs.getString("bridge_type"));
        b.setStructureType(rs.getString("structure_type"));
        b.setSpanCombination(rs.getString("span_combination"));
        b.setTotalLength(rs.getDouble("total_length"));
        b.setTotalWidth(rs.getDouble("total_width"));
        b.setClearSpan(rs.getDouble("clear_span"));
        b.setDesignLoad(rs.getString("design_load"));
        b.setAntiSeismic(rs.getString("anti_seismic"));
        b.setDesignUnit(rs.getString("design_unit"));
        b.setConstructUnit(rs.getString("construct_unit"));
        b.setSuperviseUnit(rs.getString("supervise_unit"));
        b.setCompleteDate(rs.getString("complete_date"));
        b.setOpenDate(rs.getString("open_date"));
        b.setManageUnit(rs.getString("manage_unit"));
        b.setMaintainUnit(rs.getString("maintain_unit"));
        b.setCheckLevel(rs.getString("check_level"));
        b.setTechStatus(rs.getInt("tech_status"));
        b.setMaintenanceLength(rs.getDouble("maintenance_length"));
        b.setLongitude(rs.getString("longitude"));
        b.setLatitude(rs.getString("latitude"));
        b.setPhotoFront(rs.getString("photo_front"));
        b.setPhotoLeft(rs.getString("photo_left"));
        b.setPhotoRight(rs.getString("photo_right"));
        b.setRemark(rs.getString("remark"));
        b.setCreateTime(rs.getTimestamp("create_time"));
        b.setUpdateTime(rs.getTimestamp("update_time"));
        return b;
    }
}
