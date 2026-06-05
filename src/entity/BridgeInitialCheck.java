package entity;

import java.util.Date;

/**
 * 桥梁初始检查记录实体类
 * 对应数据库表: bridge_initial_check
 * 负责模块: 郑晟
 */
public class BridgeInitialCheck {
    private int id;
    private int bridgeId;
    private String bridgeName; // 关联桥梁名称
    private String checkNo;
    private String checkDate;
    private String checker;
    private String weather;
    private String temperature;
    private String checkContent;
    private String deckCondition;
    private String superstructureCondition;
    private String substructureCondition;
    private String accessoryCondition;
    private String defectDesc;
    private String defectPhoto;
    private String suggest;
    private String conclusion;
    private String nextCheckDate;
    private String checkReport;
    private Date createTime;
    private Date updateTime;
    
    public BridgeInitialCheck() {}
    
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
    
    public String getCheckContent() { return checkContent; }
    public void setCheckContent(String checkContent) { this.checkContent = checkContent; }
    
    public String getDeckCondition() { return deckCondition; }
    public void setDeckCondition(String deckCondition) { this.deckCondition = deckCondition; }
    
    public String getSuperstructureCondition() { return superstructureCondition; }
    public void setSuperstructureCondition(String superstructureCondition) { this.superstructureCondition = superstructureCondition; }
    
    public String getSubstructureCondition() { return substructureCondition; }
    public void setSubstructureCondition(String substructureCondition) { this.substructureCondition = substructureCondition; }
    
    public String getAccessoryCondition() { return accessoryCondition; }
    public void setAccessoryCondition(String accessoryCondition) { this.accessoryCondition = accessoryCondition; }
    
    public String getDefectDesc() { return defectDesc; }
    public void setDefectDesc(String defectDesc) { this.defectDesc = defectDesc; }
    
    public String getDefectPhoto() { return defectPhoto; }
    public void setDefectPhoto(String defectPhoto) { this.defectPhoto = defectPhoto; }
    
    public String getSuggest() { return suggest; }
    public void setSuggest(String suggest) { this.suggest = suggest; }
    
    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }
    
    public String getNextCheckDate() { return nextCheckDate; }
    public void setNextCheckDate(String nextCheckDate) { this.nextCheckDate = nextCheckDate; }
    
    public String getCheckReport() { return checkReport; }
    public void setCheckReport(String checkReport) { this.checkReport = checkReport; }
    
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
