package service;

import dao.OperationLogDao;
import dao.OperationLogDaoImpl;
import entity.OperationLog;
import entity.User;
import util.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 操作日志服务
 * 异步记录用户操作，避免影响主业务流程响应速度
 */
public class OperationLogService {

    private static OperationLogService instance;
    private final OperationLogDao logDao;
    private final ExecutorService executor;

    private OperationLogService() {
        this.logDao = new OperationLogDaoImpl();
        // 使用单线程异步写入日志，避免阻塞业务操作
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "operation-log-writer");
            t.setDaemon(true);
            return t;
        });
    }

    public static synchronized OperationLogService getInstance() {
        if (instance == null) {
            instance = new OperationLogService();
        }
        return instance;
    }

    /**
     * 异步记录操作日志
     */
    public void logAsync(User user, String operationType, String operationDesc) {
        if (user == null) return;
        OperationLog log = new OperationLog(user.getId(), user.getUsername(), operationType, operationDesc);
        executor.submit(() -> {
            try {
                logDao.add(log);
            } catch (Exception e) {
                Logger.error("异步写入操作日志失败", e);
            }
        });
    }

    /**
     * 同步记录操作日志
     */
    public boolean log(User user, String operationType, String operationDesc) {
        if (user == null) return false;
        OperationLog log = new OperationLog(user.getId(), user.getUsername(), operationType, operationDesc);
        return logDao.add(log);
    }

    public List<OperationLog> getAllLogs() {
        return logDao.findAll();
    }

    public List<OperationLog> getLogsByUser(String username) {
        return logDao.findByUser(username);
    }

    public List<OperationLog> getLogsByType(String operationType) {
        return logDao.findByType(operationType);
    }

    public List<OperationLog> getRecentLogs(int limit) {
        return logDao.findRecent(limit);
    }

    public int getLogCount() {
        return logDao.count();
    }
}
