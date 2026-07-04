package entity;

import java.util.Date;

/**
 * 操作日志实体类
 * 记录用户在系统中的关键操作，用于审计追踪
 */
public class OperationLog {
    private int id;
    private int userId;
    private String username;
    private String operationType;
    private String operationDesc;
    private String ipAddress;
    private Date operationTime;

    public OperationLog() {}

    public OperationLog(int userId, String username, String operationType, String operationDesc) {
        this.userId = userId;
        this.username = username;
        this.operationType = operationType;
        this.operationDesc = operationDesc;
        this.operationTime = new Date();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public String getOperationDesc() { return operationDesc; }
    public void setOperationDesc(String operationDesc) { this.operationDesc = operationDesc; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Date getOperationTime() { return operationTime; }
    public void setOperationTime(Date operationTime) { this.operationTime = operationTime; }
}
