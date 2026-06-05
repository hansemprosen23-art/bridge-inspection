package dao;

import entity.User;
import java.util.List;

/**
 * 用户数据访问接口
 * 负责模块: 曹城钧
 */
public interface UserDao {
    
    User findByUsername(String username);
    
    User findById(int id);
    
    List<User> findAll();
    
    boolean add(User user);
    
    boolean update(User user);
    
    boolean delete(int id);
    
    boolean updatePassword(int id, String newPassword);
}
