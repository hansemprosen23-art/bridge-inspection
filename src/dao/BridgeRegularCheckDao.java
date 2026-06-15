package dao;

import entity.BridgeRegularCheck;
import java.sql.Connection;
import java.util.List;

/**
 * 桥梁定期检查记录数据访问接口
 * 负责模块: 谭容昊
 */
public interface BridgeRegularCheckDao {

    boolean add(BridgeRegularCheck check);

    /**
     * 在指定连接上添加记录（用于事务控制）
     */
    boolean add(Connection conn, BridgeRegularCheck check);

    /**
     * 在指定连接上添加记录并返回自增主键（用于事务控制）
     */
    int addAndGetId(Connection conn, BridgeRegularCheck check);

    boolean update(BridgeRegularCheck check);
    
    boolean delete(int id);
    
    BridgeRegularCheck findById(int id);
    
    List<BridgeRegularCheck> findAll();
    
    List<BridgeRegularCheck> findByBridgeId(int bridgeId);
    
    List<BridgeRegularCheck> findByBridgeName(String bridgeName);
    
    List<BridgeRegularCheck> findByTechStatus(String techStatus);
    
    List<BridgeRegularCheck> findByDateRange(String startDate, String endDate);
    
    int count();
}
