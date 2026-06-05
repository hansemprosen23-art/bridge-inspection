package service;

import dao.UserDao;
import dao.UserDaoImpl;
import entity.User;
import util.Logger;
import util.PasswordUtil;

import java.util.List;

/**
 * 用户业务逻辑层
 * 负责模块: 曹城钧
 * 已增加密码加密支持
 */
public class UserService {

    private static UserService instance;
    private UserDao userDao;

    private UserService() {
        this.userDao = new UserDaoImpl();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * 用户登录验证（支持加密密码）
     */
    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user == null) return null;

        // 兼容旧版明文密码和新版加密密码
        String storedPassword = user.getPassword();
        boolean valid;
        if (storedPassword.contains(":")) {
            // 新格式: salt:hashedPassword
            String[] parts = storedPassword.split(":");
            valid = PasswordUtil.verify(password, parts[0], parts[1]);
        } else {
            // 旧格式: 明文密码（兼容过渡）
            valid = storedPassword.equals(password);
        }

        if (valid) {
            Logger.info("用户登录成功: " + username);
            return user;
        }
        Logger.warn("用户登录失败: " + username);
        return null;
    }

    /**
     * 添加用户（自动加密密码）
     */
    public boolean addUser(User user) {
        if (userDao.findByUsername(user.getUsername()) != null) {
            return false; // 用户名已存在
        }
        // 加密密码
        String salt = PasswordUtil.generateSalt();
        String hashed = PasswordUtil.encrypt(user.getPassword(), salt);
        user.setPassword(salt + ":" + hashed);
        return userDao.add(user);
    }

    /**
     * 更新用户信息
     */
    public boolean updateUser(User user) {
        return userDao.update(user);
    }

    /**
     * 删除用户
     */
    public boolean deleteUser(int id) {
        return userDao.delete(id);
    }

    /**
     * 修改密码（新密码自动加密）
     */
    public boolean changePassword(int id, String oldPassword, String newPassword) {
        User user = userDao.findById(id);
        if (user == null) return false;

        String storedPassword = user.getPassword();
        boolean valid;
        if (storedPassword.contains(":")) {
            String[] parts = storedPassword.split(":");
            valid = PasswordUtil.verify(oldPassword, parts[0], parts[1]);
        } else {
            valid = storedPassword.equals(oldPassword);
        }

        if (valid) {
            String salt = PasswordUtil.generateSalt();
            String hashed = PasswordUtil.encrypt(newPassword, salt);
            return userDao.updatePassword(id, salt + ":" + hashed);
        }
        return false;
    }

    /**
     * 重置密码为默认密码（管理员功能）
     */
    public boolean resetPassword(int id, String defaultPassword) {
        String salt = PasswordUtil.generateSalt();
        String hashed = PasswordUtil.encrypt(defaultPassword, salt);
        return userDao.updatePassword(id, salt + ":" + hashed);
    }

    /**
     * 查询所有用户
     */
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    /**
     * 根据ID查询用户
     */
    public User getUserById(int id) {
        return userDao.findById(id);
    }
}
