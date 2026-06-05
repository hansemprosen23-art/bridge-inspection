package dao;

import entity.BridgeInitialCheck;
import java.util.List;

/**
 * 桥梁初始检查记录数据访问接口
 * 负责模块: 郑晟
 */
public interface BridgeInitialCheckDao {
    
    boolean add(BridgeInitialCheck check);
    
    boolean update(BridgeInitialCheck check);
    
    boolean delete(int id);
    
    BridgeInitialCheck findById(int id);
    
    List<BridgeInitialCheck> findAll();
    
    List<BridgeInitialCheck> findByBridgeId(int bridgeId);
    
    List<BridgeInitialCheck> findByBridgeName(String bridgeName);
    
    List<BridgeInitialCheck> findByDateRange(String startDate, String endDate);
    
    int count();
}
