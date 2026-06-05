package entity;

import java.util.Date;

/**
 * 用户实体类
 * 对应数据库表: user
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String realName;
    private String role;
    private String phone;
    private Date createTime;
    
    public User() {}
    
    public User(int id, String username, String realName, String role) {
        this.id = id;
        this.username = username;
        this.realName = realName;
        this.role = role;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    
    public boolean isAdmin() {
        return "admin".equals(role);
    }
    
    @Override
    public String toString() {
        return realName + "(" + username + ")";
    }
}
