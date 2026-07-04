package dao;

import entity.OperationLog;
import java.util.List;

public interface OperationLogDao {
    boolean add(OperationLog log);
    List<OperationLog> findAll();
    List<OperationLog> findByUser(String username);
    List<OperationLog> findByType(String operationType);
    List<OperationLog> findRecent(int limit);
    int count();
}
