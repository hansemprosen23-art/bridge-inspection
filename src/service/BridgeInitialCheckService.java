package service;

import dao.BridgeInitialCheckDao;
import dao.BridgeInitialCheckDaoImpl;
import entity.BridgeInitialCheck;
import java.util.List;

/**
 * 桥梁初始检查记录业务逻辑层
 * 负责模块: 郑晟
 */
public class BridgeInitialCheckService {
    
    private static BridgeInitialCheckService instance;
    private BridgeInitialCheckDao checkDao;
    
    private BridgeInitialCheckService() {
        this.checkDao = new BridgeInitialCheckDaoImpl();
    }
    
    public static synchronized BridgeInitialCheckService getInstance() {
        if (instance == null) {
            instance = new BridgeInitialCheckService();
        }
        return instance;
    }
    
    /**
     * 添加初始检查记录
     */
    public boolean addCheck(BridgeInitialCheck check) {
        return checkDao.add(check);
    }
    
    /**
     * 更新检查记录
     */
    public boolean updateCheck(BridgeInitialCheck check) {
        return checkDao.update(check);
    }
    
    /**
     * 删除检查记录
     */
    public boolean deleteCheck(int id) {
        return checkDao.delete(id);
    }
    
    /**
     * 根据ID查询
     */
    public BridgeInitialCheck getCheckById(int id) {
        return checkDao.findById(id);
    }
    
    /**
     * 查询所有记录
     */
    public List<BridgeInitialCheck> getAllChecks() {
        return checkDao.findAll();
    }
    
    /**
     * 根据桥梁ID查询
     */
    public List<BridgeInitialCheck> getChecksByBridgeId(int bridgeId) {
        return checkDao.findByBridgeId(bridgeId);
    }
    
    /**
     * 根据桥梁名称查询
     */
    public List<BridgeInitialCheck> searchByBridgeName(String bridgeName) {
        return checkDao.findByBridgeName(bridgeName);
    }
    
    /**
     * 根据日期范围查询
     */
    public List<BridgeInitialCheck> searchByDateRange(String startDate, String endDate) {
        return checkDao.findByDateRange(startDate, endDate);
    }
    
    /**
     * 获取记录总数
     */
    public int getCheckCount() {
        return checkDao.count();
    }
}
