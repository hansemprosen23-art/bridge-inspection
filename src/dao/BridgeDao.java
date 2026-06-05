package dao;

import entity.Bridge;
import java.util.List;

/**
 * 桥梁数据访问接口
 * 负责模块: 张子健
 */
public interface BridgeDao {
    
    boolean add(Bridge bridge);
    
    boolean update(Bridge bridge);
    
    boolean delete(int id);
    
    Bridge findById(int id);
    
    Bridge findByBridgeNo(String bridgeNo);
    
    List<Bridge> findAll();
    
    List<Bridge> findByName(String name);
    
    List<Bridge> findByType(String type);
    
    List<Bridge> findByLevel(String level);
    
    int count();
}
