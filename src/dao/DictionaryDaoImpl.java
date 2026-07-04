package dao;

import entity.DictionaryItem;
import util.DBUtil;
import util.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DictionaryDaoImpl implements DictionaryDao {

    @Override
    public boolean add(DictionaryItem item) {
        String sql = "INSERT INTO dictionary_item (dict_type, item_code, item_name, sort_order, remark) VALUES (?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, item.getDictType());
            pstmt.setString(2, item.getItemCode());
            pstmt.setString(3, item.getItemName());
            pstmt.setInt(4, item.getSortOrder());
            pstmt.setString(5, item.getRemark());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("添加字典项失败", e);
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return false;
    }

    @Override
    public boolean update(DictionaryItem item) {
        String sql = "UPDATE dictionary_item SET dict_type=?, item_code=?, item_name=?, sort_order=?, remark=? WHERE id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, item.getDictType());
            pstmt.setString(2, item.getItemCode());
            pstmt.setString(3, item.getItemName());
            pstmt.setInt(4, item.getSortOrder());
            pstmt.setString(5, item.getRemark());
            pstmt.setInt(6, item.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("更新字典项失败", e);
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM dictionary_item WHERE id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.error("删除字典项失败", e);
        } finally {
            DBUtil.close(conn, pstmt);
        }
        return false;
    }

    @Override
    public List<DictionaryItem> findByType(String dictType) {
        String sql = "SELECT * FROM dictionary_item WHERE dict_type=? ORDER BY sort_order, id";
        List<DictionaryItem> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dictType);
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapItem(rs));
        } catch (SQLException e) {
            Logger.error("按类型查询字典项失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public List<DictionaryItem> findAllTypes() {
        String sql = "SELECT DISTINCT dict_type FROM dictionary_item ORDER BY dict_type";
        List<DictionaryItem> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                DictionaryItem item = new DictionaryItem();
                item.setDictType(rs.getString("dict_type"));
                list.add(item);
            }
        } catch (SQLException e) {
            Logger.error("查询字典类型失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    @Override
    public List<DictionaryItem> findAll() {
        String sql = "SELECT * FROM dictionary_item ORDER BY dict_type, sort_order, id";
        List<DictionaryItem> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapItem(rs));
        } catch (SQLException e) {
            Logger.error("查询所有字典项失败", e);
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    private DictionaryItem mapItem(ResultSet rs) throws SQLException {
        DictionaryItem item = new DictionaryItem();
        item.setId(rs.getInt("id"));
        item.setDictType(rs.getString("dict_type"));
        item.setItemCode(rs.getString("item_code"));
        item.setItemName(rs.getString("item_name"));
        item.setSortOrder(rs.getInt("sort_order"));
        item.setRemark(rs.getString("remark"));
        return item;
    }
}
