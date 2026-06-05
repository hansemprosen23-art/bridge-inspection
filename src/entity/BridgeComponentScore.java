package entity;

import java.util.Date;

/**
 * 桥梁部件评分明细实体类
 * 对应数据库表: bridge_component_score
 * 用于按JTG 5120-2021规范进行部件级评分
 */
public class BridgeComponentScore {
    private int id;
    private int regularCheckId;      // 关联定期检查记录ID
    private String category;         // 分类: 桥面系/上部结构/下部结构/附属设施
    private String componentName;    // 部件名称
    private double score;            // 得分 0-100
    private double weight;           // 权重
    private String defectDesc;       // 缺损描述
    private String deductReason;     // 扣分原因
    private Date createTime;
    private Date updateTime;

    public BridgeComponentScore() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRegularCheckId() { return regularCheckId; }
    public void setRegularCheckId(int regularCheckId) { this.regularCheckId = regularCheckId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getComponentName() { return componentName; }
    public void setComponentName(String componentName) { this.componentName = componentName; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public String getDefectDesc() { return defectDesc; }
    public void setDefectDesc(String defectDesc) { this.defectDesc = defectDesc; }

    public String getDeductReason() { return deductReason; }
    public void setDeductReason(String deductReason) { this.deductReason = deductReason; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
