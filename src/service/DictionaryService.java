package service;

import dao.DictionaryDao;
import dao.DictionaryDaoImpl;
import entity.DictionaryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典服务
 * 提供桥梁类型、结构类型、检查等级等可配置下拉项
 */
public class DictionaryService {

    private static DictionaryService instance;
    private final DictionaryDao dictDao;

    private DictionaryService() {
        this.dictDao = new DictionaryDaoImpl();
    }

    public static synchronized DictionaryService getInstance() {
        if (instance == null) instance = new DictionaryService();
        return instance;
    }

    public boolean addItem(DictionaryItem item) {
        return dictDao.add(item);
    }

    public boolean updateItem(DictionaryItem item) {
        return dictDao.update(item);
    }

    public boolean deleteItem(int id) {
        return dictDao.delete(id);
    }

    public List<DictionaryItem> getItemsByType(String dictType) {
        return dictDao.findByType(dictType);
    }

    public List<DictionaryItem> getAllTypes() {
        return dictDao.findAllTypes();
    }

    public List<DictionaryItem> getAllItems() {
        return dictDao.findAll();
    }

    /**
     * 获取字典项名称数组，用于 JComboBox
     */
    public String[] getItemNamesByType(String dictType) {
        List<DictionaryItem> items = getItemsByType(dictType);
        List<String> names = new ArrayList<>();
        for (DictionaryItem item : items) {
            names.add(item.getItemName());
        }
        return names.toArray(new String[0]);
    }

    public void initDefaultData() {
        // 初始化默认字典数据
        String[][] defaults = {
            {"bridge_type", "梁式桥"}, {"bridge_type", "拱桥"},
            {"bridge_type", "斜拉桥"}, {"bridge_type", "悬索桥"},
            {"bridge_type", "刚架拱桥"},
            {"structure_type", "预应力混凝土T梁"}, {"structure_type", "钢筋混凝土箱形拱"},
            {"structure_type", "双塔双索面斜拉桥"}, {"structure_type", "单跨悬索桥"},
            {"check_level", "Ⅰ"}, {"check_level", "Ⅱ"}, {"check_level", "Ⅲ"}
        };

        for (String[] def : defaults) {
            List<DictionaryItem> existing = getItemsByType(def[0]);
            boolean found = false;
            for (DictionaryItem item : existing) {
                if (item.getItemName().equals(def[1])) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                DictionaryItem item = new DictionaryItem();
                item.setDictType(def[0]);
                item.setItemCode(def[1]);
                item.setItemName(def[1]);
                item.setSortOrder(0);
                addItem(item);
            }
        }
    }
}
