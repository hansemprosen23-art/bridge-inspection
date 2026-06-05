package dao;

import entity.BridgeComponentScore;
import java.util.List;

public interface BridgeComponentScoreDao {
    boolean add(BridgeComponentScore score);
    boolean update(BridgeComponentScore score);
    boolean delete(int id);
    boolean deleteByCheckId(int regularCheckId);
    BridgeComponentScore findById(int id);
    List<BridgeComponentScore> findByCheckId(int regularCheckId);
    List<BridgeComponentScore> findByCheckIdAndCategory(int regularCheckId, String category);
}
