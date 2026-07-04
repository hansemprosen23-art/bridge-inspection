package entity;

import java.util.Date;

/**
 * 检查提醒实体类
 * 用于记录即将到期的检查计划
 */
public class CheckReminder {
    private int bridgeId;
    private String bridgeNo;
    private String bridgeName;
    private String checkType;
    private Date plannedDate;
    private int daysRemaining;
    private String urgency;

    public CheckReminder() {}

    public int getBridgeId() { return bridgeId; }
    public void setBridgeId(int bridgeId) { this.bridgeId = bridgeId; }

    public String getBridgeNo() { return bridgeNo; }
    public void setBridgeNo(String bridgeNo) { this.bridgeNo = bridgeNo; }

    public String getBridgeName() { return bridgeName; }
    public void setBridgeName(String bridgeName) { this.bridgeName = bridgeName; }

    public String getCheckType() { return checkType; }
    public void setCheckType(String checkType) { this.checkType = checkType; }

    public Date getPlannedDate() { return plannedDate; }
    public void setPlannedDate(Date plannedDate) { this.plannedDate = plannedDate; }

    public int getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(int daysRemaining) { this.daysRemaining = daysRemaining; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
}
