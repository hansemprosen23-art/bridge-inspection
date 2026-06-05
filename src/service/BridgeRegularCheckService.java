package service;

import dao.*;
import entity.BridgeComponentScore;
import entity.BridgeRegularCheck;
import util.BCICalculator;
import util.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 桥梁定期检查记录业务逻辑层
 * 负责模块: 谭容昊
 * 已按 JTG 5120-2021 规范重构 BCI 计算
 */
public class BridgeRegularCheckService {

    private static BridgeRegularCheckService instance;
    private BridgeRegularCheckDao checkDao;
    private BridgeComponentScoreDao componentScoreDao;

    private BridgeRegularCheckService() {
        this.checkDao = new BridgeRegularCheckDaoImpl();
        this.componentScoreDao = new BridgeComponentScoreDaoImpl();
    }

    public static synchronized BridgeRegularCheckService getInstance() {
        if (instance == null) {
            instance = new BridgeRegularCheckService();
        }
        return instance;
    }

    /**
     * 添加定期检查记录（含部件评分）
     */
    public boolean addCheck(BridgeRegularCheck check, List<BridgeComponentScore> componentScores) {
        Connection conn = null;
        try {
            conn = util.DBUtil.getConnection();
            conn.setAutoCommit(false);

            boolean success = checkDao.add(check);
            if (!success) {
                conn.rollback();
                return false;
            }

            // 获取刚插入的记录ID
            int checkId = getLatestCheckId(check.getBridgeId(), check.getCheckNo());
            if (checkId > 0 && componentScores != null) {
                for (BridgeComponentScore score : componentScores) {
                    score.setRegularCheckId(checkId);
                    componentScoreDao.add(score);
                }
            }

            conn.commit();
            Logger.info("添加定期检查记录成功: " + check.getCheckNo());
            return true;
        } catch (Exception e) {
            Logger.error("添加定期检查记录失败", e);
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { Logger.error("回滚失败", ex); }
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    /**
     * 添加定期检查记录（简化版，无部件评分）
     */
    public boolean addCheck(BridgeRegularCheck check) {
        return addCheck(check, null);
    }

    /**
     * 更新检查记录
     */
    public boolean updateCheck(BridgeRegularCheck check) {
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
    public BridgeRegularCheck getCheckById(int id) {
        return checkDao.findById(id);
    }

    /**
     * 查询所有记录
     */
    public List<BridgeRegularCheck> getAllChecks() {
        return checkDao.findAll();
    }

    /**
     * 根据桥梁ID查询
     */
    public List<BridgeRegularCheck> getChecksByBridgeId(int bridgeId) {
        return checkDao.findByBridgeId(bridgeId);
    }

    /**
     * 根据桥梁名称查询
     */
    public List<BridgeRegularCheck> searchByBridgeName(String bridgeName) {
        return checkDao.findByBridgeName(bridgeName);
    }

    /**
     * 根据技术状况等级查询
     */
    public List<BridgeRegularCheck> searchByTechStatus(String techStatus) {
        return checkDao.findByTechStatus(techStatus);
    }

    /**
     * 根据日期范围查询
     */
    public List<BridgeRegularCheck> searchByDateRange(String startDate, String endDate) {
        return checkDao.findByDateRange(startDate, endDate);
    }

    /**
     * 获取记录总数
     */
    public int getCheckCount() {
        return checkDao.count();
    }

    /**
     * 获取某检查记录的所有部件评分
     */
    public List<BridgeComponentScore> getComponentScores(int checkId) {
        return componentScoreDao.findByCheckId(checkId);
    }

    /**
     * 计算BCI指数（简化版 - 四部分总体评分）
     * 按 JTG 5120-2021 规范加权计算
     */
    public double calculateBCI(int deck, int superstructure, int substructure, int accessory) {
        return BCICalculator.calculateBCI(deck, superstructure, substructure, accessory);
    }

    /**
     * 计算BCI指数（完整版 - 部件详细评分）
     * 按 JTG 5120-2021 规范分层加权计算
     */
    public double calculateBCIFromComponents(Map<String, Double> deckScores,
                                              Map<String, Double> superScores,
                                              Map<String, Double> subScores,
                                              Map<String, Double> accessoryScores,
                                              String bridgeType) {
        return BCICalculator.calculateBCI(deckScores, superScores, subScores, accessoryScores, bridgeType);
    }

    /**
     * 根据BCI确定技术状况等级
     */
    public String determineTechStatus(double bci) {
        return BCICalculator.determineTechStatus(bci);
    }

    /**
     * 获取技术状况等级描述
     */
    public String getTechStatusDesc(String status) {
        return BCICalculator.getTechStatusDesc(status);
    }

    /**
     * 获取某桥型的检查部件模板
     */
    public Map<String, Map<String, Double>> getCheckTemplate(String bridgeType) {
        return BCICalculator.getAllComponents(bridgeType);
    }

    private int getLatestCheckId(int bridgeId, String checkNo) {
        List<BridgeRegularCheck> list = checkDao.findByBridgeId(bridgeId);
        for (BridgeRegularCheck c : list) {
            if (c.getCheckNo().equals(checkNo)) {
                return c.getId();
            }
        }
        return -1;
    }
}
