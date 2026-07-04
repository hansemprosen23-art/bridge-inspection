package entity;

/**
 * 数据字典项实体类
 * 用于动态配置桥梁类型、结构类型、检查等级等下拉选项
 */
public class DictionaryItem {
    private int id;
    private String dictType;
    private String itemCode;
    private String itemName;
    private int sortOrder;
    private String remark;

    public DictionaryItem() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDictType() { return dictType; }
    public void setDictType(String dictType) { this.dictType = dictType; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
