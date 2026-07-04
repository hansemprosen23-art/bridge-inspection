package service;

import dao.BridgeDao;
import dao.BridgeDaoImpl;
import entity.Bridge;
import util.CacheManager;
import java.util.List;

/**
 * 桥梁业务逻辑层
 * 负责模块: 张子健
 */
public class BridgeService {

    private static BridgeService instance;
    private BridgeDao bridgeDao;

    // 桥梁列表缓存，有效期60秒，最大100条缓存项
    private final CacheManager<String, List<Bridge>> bridgeListCache = new CacheManager<>(60_000, 10);
    private final CacheManager<Integer, Bridge> bridgeCache = new CacheManager<>(60_000, 200);

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
        boolean success = bridgeDao.add(bridge);
        if (success) {
            bridgeListCache.invalidateAll();
        }
        return success;
    }

    /**
     * 更新桥梁信息
     */
    public boolean updateBridge(Bridge bridge) {
        boolean success = bridgeDao.update(bridge);
        if (success) {
            bridgeCache.invalidate(bridge.getId());
            bridgeListCache.invalidateAll();
        }
        return success;
    }

    /**
     * 删除桥梁
     */
    public boolean deleteBridge(int id) {
        boolean success = bridgeDao.delete(id);
        if (success) {
            bridgeCache.invalidate(id);
            bridgeListCache.invalidateAll();
        }
        return success;
    }

    /**
     * 根据ID查询桥梁（带缓存）
     */
    public Bridge getBridgeById(int id) {
        Bridge bridge = bridgeCache.get(id);
        if (bridge == null) {
            bridge = bridgeDao.findById(id);
            if (bridge != null) {
                bridgeCache.put(id, bridge);
            }
        }
        return bridge;
    }

    /**
     * 根据编号查询桥梁（带缓存）
     */
    public Bridge getBridgeByNo(String bridgeNo) {
        Bridge bridge = bridgeCache.get(bridgeNo.hashCode());
        if (bridge == null || !bridge.getBridgeNo().equals(bridgeNo)) {
            bridge = bridgeDao.findByBridgeNo(bridgeNo);
            if (bridge != null) {
                bridgeCache.put(bridge.getId(), bridge);
            }
        }
        return bridge;
    }

    /**
     * 查询所有桥梁（带缓存）
     */
    public List<Bridge> getAllBridges() {
        List<Bridge> list = bridgeListCache.get("all");
        if (list == null) {
            list = bridgeDao.findAll();
            bridgeListCache.put("all", list);
        }
        return list;
    }

    /**
     * 根据名称模糊查询
     */
    public List<Bridge> searchByName(String name) {
        String key = "name:" + name;
        List<Bridge> list = bridgeListCache.get(key);
        if (list == null) {
            list = bridgeDao.findByName(name);
            bridgeListCache.put(key, list);
        }
        return list;
    }

    /**
     * 根据类型查询
     */
    public List<Bridge> searchByType(String type) {
        String key = "type:" + type;
        List<Bridge> list = bridgeListCache.get(key);
        if (list == null) {
            list = bridgeDao.findByType(type);
            bridgeListCache.put(key, list);
        }
        return list;
    }

    /**
     * 根据检查等级查询
     */
    public List<Bridge> searchByLevel(String level) {
        String key = "level:" + level;
        List<Bridge> list = bridgeListCache.get(key);
        if (list == null) {
            list = bridgeDao.findByLevel(level);
            bridgeListCache.put(key, list);
        }
        return list;
    }

    /**
     * 获取桥梁总数
     */
    public int getBridgeCount() {
        return bridgeDao.count();
    }

    /**
     * 手动刷新缓存
     */
    public void refreshCache() {
        bridgeListCache.invalidateAll();
        bridgeCache.invalidateAll();
    }
}
