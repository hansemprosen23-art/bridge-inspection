package ui;

import entity.User;
import ui.common.ThemeColors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 系统主界面 - 侧边栏导航风格
 */
public class MainFrame extends JFrame {
    
    private User currentUser;
    private JPanel contentPanel;
    private JLabel[] navLabels;
    private JPanel[] navItems;
    private int selectedIndex = 0;
    
    private final String[] navTitles = {"桥梁卡片", "初始检查", "定期检查", "数据统计", "用户管理", "系统维护"};
    private final Color[] navColors = {
        new Color(41, 98, 255),
        new Color(46, 125, 50),
        new Color(2, 136, 209),
        new Color(123, 31, 162),
        new Color(245, 124, 0),
        new Color(96, 125, 139)
    };
    
    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("公路桥梁初始检查信息系统");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        layoutComponents();
    }
    
    private void initComponents() {
        // 导航项
        int navCount = currentUser.isAdmin() ? 6 : 4;
        navItems = new JPanel[navCount];
        navLabels = new JLabel[navCount];
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // 左侧侧边栏
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(30, 41, 59));
        sidebar.setPreferredSize(new Dimension(200, 0));
        
        // 顶部标题
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 4));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(24, 20, 20, 20));
        
        JLabel appTitle = new JLabel("桥梁检查系统");
        appTitle.setFont(new Font("微软雅黑", Font.BOLD, 18));
        appTitle.setForeground(Color.WHITE);
        header.add(appTitle);
        
        JLabel userInfo = new JLabel(currentUser.getRealName() + " | " + (currentUser.isAdmin() ? "管理员" : "检查员"));
        userInfo.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        userInfo.setForeground(new Color(148, 163, 184));
        header.add(userInfo);
        
        sidebar.add(header, BorderLayout.NORTH);
        
        // 导航菜单
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        
        String[] titles = currentUser.isAdmin() ? navTitles :
            new String[]{"桥梁卡片", "初始检查", "定期检查", "数据统计"};
        Color[] colors = currentUser.isAdmin() ? navColors :
            new Color[]{navColors[0], navColors[1], navColors[2], navColors[3]};

        for (int i = 0; i < titles.length; i++) {
            navItems[i] = createNavItem(titles[i], colors[i], i);
            navPanel.add(navItems[i]);
            navPanel.add(Box.createVerticalStrut(6));
        }
        
        sidebar.add(navPanel, BorderLayout.CENTER);
        
        // 底部退出按钮
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        footer.setOpaque(false);
        JLabel logoutLabel = new JLabel("⟲ 切换用户");
        logoutLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        logoutLabel.setForeground(new Color(148, 163, 184));
        logoutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int r = JOptionPane.showConfirmDialog(MainFrame.this, "确定切换用户？", "确认", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    new LoginFrame().setVisible(true);
                    MainFrame.this.dispose();
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) { logoutLabel.setForeground(Color.WHITE); }
            @Override
            public void mouseExited(MouseEvent e) { logoutLabel.setForeground(new Color(148, 163, 184)); }
        });
        footer.add(logoutLabel);
        sidebar.add(footer, BorderLayout.SOUTH);
        
        add(sidebar, BorderLayout.WEST);
        
        // 右侧内容区
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ThemeColors.BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(contentPanel, BorderLayout.CENTER);
        
        // 默认显示第一个面板
        switchPanel(0);
    }
    
    private JPanel createNavItem(String title, Color accentColor, int index) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        item.setPreferredSize(new Dimension(0, 46));
        
        JLabel label = new JLabel("  " + title);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        label.setForeground(new Color(203, 213, 225));
        label.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        
        // 左侧彩色指示条
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(4, 0));
        indicator.setOpaque(false);
        
        item.add(indicator, BorderLayout.WEST);
        item.add(label, BorderLayout.CENTER);
        
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { switchPanel(index); }
            @Override
            public void mouseEntered(MouseEvent e) {
                if (selectedIndex != index) {
                    item.setBackground(new Color(51, 65, 85));
                    item.setOpaque(true);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (selectedIndex != index) item.setOpaque(false);
            }
        });
        
        navLabels[index] = label;
        return item;
    }
    
    private void switchPanel(int index) {
        selectedIndex = index;

        // 更新导航样式
        String[] titles = currentUser.isAdmin() ? navTitles :
            new String[]{"桥梁卡片", "初始检查", "定期检查", "数据统计"};
        Color[] colors = currentUser.isAdmin() ? navColors :
            new Color[]{navColors[0], navColors[1], navColors[2], navColors[3]};

        for (int i = 0; i < navItems.length; i++) {
            if (i == index) {
                navItems[i].setBackground(new Color(51, 65, 85));
                navItems[i].setOpaque(true);
                navLabels[i].setForeground(colors[i]);
                navLabels[i].setFont(new Font("微软雅黑", Font.BOLD, 14));
                // 设置左侧指示条颜色
                ((JPanel)navItems[i].getComponent(0)).setBackground(colors[i]);
                ((JPanel)navItems[i].getComponent(0)).setOpaque(true);
            } else {
                navItems[i].setOpaque(false);
                navLabels[i].setForeground(new Color(203, 213, 225));
                navLabels[i].setFont(new Font("微软雅黑", Font.PLAIN, 14));
                ((JPanel)navItems[i].getComponent(0)).setOpaque(false);
            }
        }

        // 切换内容面板
        contentPanel.removeAll();
        switch (index) {
            case 0: contentPanel.add(new BridgeManagePanel(), BorderLayout.CENTER); break;
            case 1: contentPanel.add(new BridgeInitialCheckPanel(), BorderLayout.CENTER); break;
            case 2: contentPanel.add(new BridgeRegularCheckPanel(), BorderLayout.CENTER); break;
            case 3: contentPanel.add(new StatisticsPanel(), BorderLayout.CENTER); break;
            case 4: contentPanel.add(new UserManagePanel(), BorderLayout.CENTER); break;
            case 5: contentPanel.add(new SystemMaintenancePanel(), BorderLayout.CENTER); break;
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
