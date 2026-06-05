package ui;

import entity.User;
import service.UserService;
import ui.common.CardPanel;
import ui.common.RoundedButton;
import ui.common.ThemeColors;
import util.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 登录界面 - 现代卡片式风格
 */
public class LoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private RoundedButton loginButton;
    private JLabel statusLabel;
    
    public LoginFrame() {
        setTitle("公路桥梁初始检查信息系统");
        setSize(500, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponents();
        layoutComponents();
        bindEvents();
        
        if (!DBUtil.testConnection()) {
            JOptionPane.showMessageDialog(this,
                "无法连接到SQL Server数据库，请检查:\n"
                + "1. SQL Server服务是否已启动\n"
                + "2. 数据库 bridge_inspection 是否已创建\n"
                + "3. mssql-jdbc 驱动jar包是否已添加到项目依赖",
                "数据库连接失败", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void initComponents() {
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ThemeColors.BORDER),
            BorderFactory.createEmptyBorder(8, 5, 8, 5)
        ));
        usernameField.setBackground(ThemeColors.BACKGROUND);
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ThemeColors.BORDER),
            BorderFactory.createEmptyBorder(8, 5, 8, 5)
        ));
        passwordField.setBackground(ThemeColors.BACKGROUND);
        passwordField.setEchoChar('●');
        
        loginButton = new RoundedButton("登 录", ThemeColors.PRIMARY);
        loginButton.setPreferredSize(new Dimension(280, 42));
        loginButton.setFont(new Font("微软雅黑", Font.BOLD, 15));
        
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusLabel.setForeground(ThemeColors.DANGER);
    }
    
    private void layoutComponents() {
        JPanel bgPanel = new JPanel(new GridBagLayout());
        bgPanel.setBackground(ThemeColors.BACKGROUND);
        
        CardPanel card = new CardPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(380, 340));
        card.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 标题
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel title = new JLabel("用户登录", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 24));
        title.setForeground(ThemeColors.TEXT_PRIMARY);
        card.add(title, gbc);
        
        gbc.gridy = 1;
        JLabel sub = new JLabel("公路桥梁初始检查信息系统", SwingConstants.CENTER);
        sub.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        sub.setForeground(ThemeColors.TEXT_SECONDARY);
        card.add(sub, gbc);
        
        // 用户名
        gbc.gridy = 2; gbc.insets = new Insets(20, 20, 4, 20);
        JLabel userLabel = new JLabel("用户名");
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        userLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        card.add(userLabel, gbc);
        
        gbc.gridy = 3; gbc.insets = new Insets(0, 20, 8, 20);
        card.add(usernameField, gbc);
        
        // 密码
        gbc.gridy = 4; gbc.insets = new Insets(8, 20, 4, 20);
        JLabel pwdLabel = new JLabel("密码");
        pwdLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        pwdLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        card.add(pwdLabel, gbc);
        
        gbc.gridy = 5; gbc.insets = new Insets(0, 20, 8, 20);
        card.add(passwordField, gbc);
        
        // 状态
        gbc.gridy = 6;
        card.add(statusLabel, gbc);
        
        // 登录按钮
        gbc.gridy = 7; gbc.insets = new Insets(12, 20, 8, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(loginButton, gbc);
        
        // 提示
        gbc.gridy = 8;
        JLabel tip = new JLabel("默认账号: admin  密码: admin123", SwingConstants.CENTER);
        tip.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        tip.setForeground(ThemeColors.TEXT_SECONDARY);
        card.add(tip, gbc);
        
        bgPanel.add(card);
        setContentPane(bgPanel);
    }
    
    private void bindEvents() {
        loginButton.addActionListener(e -> doLogin());
        
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        });
        
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) passwordField.requestFocus();
            }
        });
    }
    
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty()) {
            statusLabel.setText("请输入用户名");
            usernameField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            statusLabel.setText("请输入密码");
            passwordField.requestFocus();
            return;
        }
        
        User user = UserService.getInstance().login(username, password);
        if (user != null) {
            statusLabel.setText("登录成功，正在进入系统...");
            statusLabel.setForeground(ThemeColors.SUCCESS);
            SwingUtilities.invokeLater(() -> {
                new MainFrame(user).setVisible(true);
                LoginFrame.this.dispose();
            });
        } else {
            statusLabel.setText("用户名或密码错误");
            statusLabel.setForeground(ThemeColors.DANGER);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
}
