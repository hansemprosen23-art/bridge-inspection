package dao;

import entity.DictionaryItem;
import java.util.List;

public interface DictionaryDao {
    boolean add(DictionaryItem item);
    boolean update(DictionaryItem item);
    boolean delete(int id);
    List<DictionaryItem> findByType(String dictType);
    List<DictionaryItem> findAllTypes();
    List<DictionaryItem> findAll();
}
