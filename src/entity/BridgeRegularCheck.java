package entity;

import java.util.Date;

/**
 * 桥梁定期检查记录实体类
 * 对应数据库表: bridge_regular_check
 * 负责模块: 谭容昊
 */
public class BridgeRegularCheck {
    private int id;
    private int bridgeId;
    private String bridgeName; // 关联桥梁名称
    private String checkNo;
    private String checkDate;
    private String checker;
    private String weather;
    private String temperature;
    private String checkType;
    private int deckScore;
    private int superstructureScore;
    private int substructureScore;
    private int accessoryScore;
    private double bci;
    private String techStatus;
    private String defectDesc;
    private String maintenanceSuggest;
    private String limitationSuggest;
    private String checkConclusion;
    private String nextCheckDate;
    private Date createTime;
    private Date updateTime;
    
    public BridgeRegularCheck() {}
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getBridgeId() { return bridgeId; }
    public void setBridgeId(int bridgeId) { this.bridgeId = bridgeId; }
    
    public String getBridgeName() { return bridgeName; }
    public void setBridgeName(String bridgeName) { this.bridgeName = bridgeName; }
    
    public String getCheckNo() { return checkNo; }
    public void setCheckNo(String checkNo) { this.checkNo = checkNo; }
    
    public String getCheckDate() { return checkDate; }
    public void setCheckDate(String checkDate) { this.checkDate = checkDate; }
    
    public String getChecker() { return checker; }
    public void setChecker(String checker) { this.checker = checker; }
    
    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }
    
    public String getTemperature() { return temperature; }
    public void setTemperature(String temperature) { this.temperature = temperature; }
    
    public String getCheckType() { return checkType; }
    public void setCheckType(String checkType) { this.checkType = checkType; }
    
    public int getDeckScore() { return deckScore; }
    public void setDeckScore(int deckScore) { this.deckScore = deckScore; }
    
    public int getSuperstructureScore() { return superstructureScore; }
    public void setSuperstructureScore(int superstructureScore) { this.superstructureScore = superstructureScore; }
    
    public int getSubstructureScore() { return substructureScore; }
    public void setSubstructureScore(int substructureScore) { this.substructureScore = substructureScore; }
    
    public int getAccessoryScore() { return accessoryScore; }
    public void setAccessoryScore(int accessoryScore) { this.accessoryScore = accessoryScore; }
    
    public double getBci() { return bci; }
    public void setBci(double bci) { this.bci = bci; }
    
    public String getTechStatus() { return techStatus; }
    public void setTechStatus(String techStatus) { this.techStatus = techStatus; }
    
    public String getDefectDesc() { return defectDesc; }
    public void setDefectDesc(String defectDesc) { this.defectDesc = defectDesc; }
    
    public String getMaintenanceSuggest() { return maintenanceSuggest; }
    public void setMaintenanceSuggest(String maintenanceSuggest) { this.maintenanceSuggest = maintenanceSuggest; }
    
    public String getLimitationSuggest() { return limitationSuggest; }
    public void setLimitationSuggest(String limitationSuggest) { this.limitationSuggest = limitationSuggest; }
    
    public String getCheckConclusion() { return checkConclusion; }
    public void setCheckConclusion(String checkConclusion) { this.checkConclusion = checkConclusion; }
    
    public String getNextCheckDate() { return nextCheckDate; }
    public void setNextCheckDate(String nextCheckDate) { this.nextCheckDate = nextCheckDate; }
    
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
