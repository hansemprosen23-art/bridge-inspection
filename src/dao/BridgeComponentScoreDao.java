package dao;

import entity.BridgeComponentScore;
import java.sql.Connection;
import java.util.List;

public interface BridgeComponentScoreDao {
    boolean add(BridgeComponentScore score);

    /**
     * 在指定连接上添加记录（用于事务控制）
     */
    boolean add(Connection conn, BridgeComponentScore score);

    boolean update(BridgeComponentScore score);
    boolean delete(int id);
    boolean deleteByCheckId(int regularCheckId);
    BridgeComponentScore findById(int id);
    List<BridgeComponentScore> findByCheckId(int regularCheckId);
    List<BridgeComponentScore> findByCheckIdAndCategory(int regularCheckId, String category);
}
