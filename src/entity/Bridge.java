package entity;

import java.util.Date;

/**
 * 桥梁基本状况卡片实体类
 * 对应数据库表: bridge
 * 负责模块: 张子健
 */
public class Bridge {
    private int id;
    private String bridgeNo;
    private String bridgeName;
    private String routeName;
    private String routeGrade;
    private String bridgeType;
    private String structureType;
    private String spanCombination;
    private double totalLength;
    private double totalWidth;
    private double clearSpan;
    private String designLoad;
    private String antiSeismic;
    private String designUnit;
    private String constructUnit;
    private String superviseUnit;
    private String completeDate;
    private String openDate;
    private String manageUnit;
    private String maintainUnit;
    private String checkLevel;
    private int techStatus;
    private double maintenanceLength;
    private String longitude;
    private String latitude;
    private String photoFront;
    private String photoLeft;
    private String photoRight;
    private String remark;
    private Date createTime;
    private Date updateTime;
    
    public Bridge() {}
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getBridgeNo() { return bridgeNo; }
    public void setBridgeNo(String bridgeNo) { this.bridgeNo = bridgeNo; }
    
    public String getBridgeName() { return bridgeName; }
    public void setBridgeName(String bridgeName) { this.bridgeName = bridgeName; }
    
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    
    public String getRouteGrade() { return routeGrade; }
    public void setRouteGrade(String routeGrade) { this.routeGrade = routeGrade; }
    
    public String getBridgeType() { return bridgeType; }
    public void setBridgeType(String bridgeType) { this.bridgeType = bridgeType; }
    
    public String getStructureType() { return structureType; }
    public void setStructureType(String structureType) { this.structureType = structureType; }
    
    public String getSpanCombination() { return spanCombination; }
    public void setSpanCombination(String spanCombination) { this.spanCombination = spanCombination; }
    
    public double getTotalLength() { return totalLength; }
    public void setTotalLength(double totalLength) { this.totalLength = totalLength; }
    
    public double getTotalWidth() { return totalWidth; }
    public void setTotalWidth(double totalWidth) { this.totalWidth = totalWidth; }
    
    public double getClearSpan() { return clearSpan; }
    public void setClearSpan(double clearSpan) { this.clearSpan = clearSpan; }
    
    public String getDesignLoad() { return designLoad; }
    public void setDesignLoad(String designLoad) { this.designLoad = designLoad; }
    
    public String getAntiSeismic() { return antiSeismic; }
    public void setAntiSeismic(String antiSeismic) { this.antiSeismic = antiSeismic; }
    
    public String getDesignUnit() { return designUnit; }
    public void setDesignUnit(String designUnit) { this.designUnit = designUnit; }
    
    public String getConstructUnit() { return constructUnit; }
    public void setConstructUnit(String constructUnit) { this.constructUnit = constructUnit; }
    
    public String getSuperviseUnit() { return superviseUnit; }
    public void setSuperviseUnit(String superviseUnit) { this.superviseUnit = superviseUnit; }
    
    public String getCompleteDate() { return completeDate; }
    public void setCompleteDate(String completeDate) { this.completeDate = completeDate; }
    
    public String getOpenDate() { return openDate; }
    public void setOpenDate(String openDate) { this.openDate = openDate; }
    
    public String getManageUnit() { return manageUnit; }
    public void setManageUnit(String manageUnit) { this.manageUnit = manageUnit; }
    
    public String getMaintainUnit() { return maintainUnit; }
    public void setMaintainUnit(String maintainUnit) { this.maintainUnit = maintainUnit; }
    
    public String getCheckLevel() { return checkLevel; }
    public void setCheckLevel(String checkLevel) { this.checkLevel = checkLevel; }
    
    public int getTechStatus() { return techStatus; }
    public void setTechStatus(int techStatus) { this.techStatus = techStatus; }
    
    public double getMaintenanceLength() { return maintenanceLength; }
    public void setMaintenanceLength(double maintenanceLength) { this.maintenanceLength = maintenanceLength; }
    
    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }
    
    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }
    
    public String getPhotoFront() { return photoFront; }
    public void setPhotoFront(String photoFront) { this.photoFront = photoFront; }
    
    public String getPhotoLeft() { return photoLeft; }
    public void setPhotoLeft(String photoLeft) { this.photoLeft = photoLeft; }
    
    public String getPhotoRight() { return photoRight; }
    public void setPhotoRight(String photoRight) { this.photoRight = photoRight; }
    
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
