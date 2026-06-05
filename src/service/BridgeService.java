package service;

import dao.BridgeDao;
import dao.BridgeDaoImpl;
import entity.Bridge;
import java.util.List;

/**
 * 桥梁业务逻辑层
 * 负责模块: 张子健
 */
public class BridgeService {
    
    private static BridgeService instance;
    private BridgeDao bridgeDao;
    
    private BridgeService() {
        this.bridgeDao = new BridgeDaoImpl();
    }
    
    public static synchronized BridgeService getInstance() {
        if (instance == null) {
            instance = new BridgeService();
        }
        return instance;
    }
    
    /**
     * 添加桥梁
     */
    public boolean addBridge(Bridge bridge) {
        if (bridgeDao.findByBridgeNo(bridge.getBridgeNo()) != null) {
            return false; // 桥梁编号已存在
        }
        return bridgeDao.add(bridge);
    }
    
    /**
     * 更新桥梁信息
     */
    public boolean updateBridge(Bridge bridge) {
        return bridgeDao.update(bridge);
    }
    
    /**
     * 删除桥梁
     */
    public boolean deleteBridge(int id) {
        return bridgeDao.delete(id);
    }
    
    /**
     * 根据ID查询桥梁
     */
    public Bridge getBridgeById(int id) {
        return bridgeDao.findById(id);
    }
    
    /**
     * 根据编号查询桥梁
     */
    public Bridge getBridgeByNo(String bridgeNo) {
        return bridgeDao.findByBridgeNo(bridgeNo);
    }
    
    /**
     * 查询所有桥梁
     */
    public List<Bridge> getAllBridges() {
        return bridgeDao.findAll();
    }
    
    /**
     * 根据名称模糊查询
     */
    public List<Bridge> searchByName(String name) {
        return bridgeDao.findByName(name);
    }
    
    /**
     * 根据类型查询
     */
    public List<Bridge> searchByType(String type) {
        return bridgeDao.findByType(type);
    }
    
    /**
     * 根据检查等级查询
     */
    public List<Bridge> searchByLevel(String level) {
        return bridgeDao.findByLevel(level);
    }
    
    /**
     * 获取桥梁总数
     */
    public int getBridgeCount() {
        return bridgeDao.count();
    }
}
