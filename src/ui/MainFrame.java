package ui;

import entity.User;
import ui.common.LoadingOverlay;
import ui.common.RefreshablePanel;
import ui.common.ThemeColors;
import util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 系统主界面 - 侧边栏导航风格
 * 优化点：
 * 1. 使用 CardLayout 缓存面板实例，避免切换时重复创建
 * 2. 面板数据加载使用 SwingWorker 异步执行，不阻塞 EDT，导航按钮颜色切换流畅
 * 3. 首次切换到某个面板时异步加载数据，后续切换直接使用缓存
 */
public class MainFrame extends JFrame {

    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel[] navLabels;
    private JPanel[] navItems;
    private JPanel[] navIndicators;
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

    // 缓存面板实例
    private final JComponent[] cachedPanels;

    public MainFrame(User user) {
        this.currentUser = user;
        this.cachedPanels = new JComponent[navTitles.length];

        setTitle("公路桥梁初始检查信息系统");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        int navCount = currentUser.isAdmin() ? 6 : 4;
        navItems = new JPanel[navCount];
        navLabels = new JLabel[navCount];
        navIndicators = new JPanel[navCount];
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

        // 右侧内容区 - 使用 CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ThemeColors.BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(contentPanel, BorderLayout.CENTER);

        // 默认显示第一个面板
        SwingUtilities.invokeLater(() -> switchPanel(0));

        // 后台预加载其他面板数据，提升后续切换响应速度
        preloadPanelData();
    }

    /**
     * 后台预加载其他面板数据
     * 登录后利用空闲时间提前加载常用数据到缓存
     */
    private void preloadPanelData() {
        SwingWorker<Void, Void> preloader = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    service.BridgeService.getInstance().getAllBridges();
                    service.BridgeInitialCheckService.getInstance().getAllChecks();
                    service.BridgeRegularCheckService.getInstance().getAllChecks();
                    service.UserService.getInstance().getAllUsers();
                    Logger.info("后台预加载完成");
                } catch (Exception e) {
                    Logger.error("后台预加载失败", e);
                }
                return null;
            }
        };
        preloader.execute();
    }

    private JPanel createNavItem(String title, Color accentColor, int index) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        item.setPreferredSize(new Dimension(0, 46));

        // 左侧彩色指示条
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(4, 0));
        indicator.setOpaque(false);
        navIndicators[index] = indicator;

        JLabel label = new JLabel("  " + title);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        label.setForeground(new Color(203, 213, 225));
        label.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        navLabels[index] = label;

        item.add(indicator, BorderLayout.WEST);
        item.add(label, BorderLayout.CENTER);

        // 统一事件监听
        MouseAdapter hoverAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedIndex != index) {
                    switchPanel(index);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (selectedIndex != index) {
                    updateNavItemStyle(index, true, false);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (selectedIndex != index) {
                    updateNavItemStyle(index, false, false);
                }
            }
        };
        item.addMouseListener(hoverAdapter);
        label.addMouseListener(hoverAdapter);
        indicator.addMouseListener(hoverAdapter);

        return item;
    }

    /**
     * 更新单个导航项样式
     */
    private void updateNavItemStyle(int index, boolean hovered, boolean selected) {
        Color[] colors = currentUser.isAdmin() ? navColors :
            new Color[]{navColors[0], navColors[1], navColors[2], navColors[3]};

        if (selected) {
            navItems[index].setBackground(new Color(51, 65, 85));
            navItems[index].setOpaque(true);
            navLabels[index].setForeground(colors[index]);
            navLabels[index].setFont(new Font("微软雅黑", Font.BOLD, 14));
            navIndicators[index].setBackground(colors[index]);
            navIndicators[index].setOpaque(true);
        } else if (hovered) {
            navItems[index].setBackground(new Color(51, 65, 85));
            navItems[index].setOpaque(true);
            navLabels[index].setForeground(new Color(226, 232, 240));
            navLabels[index].setFont(new Font("微软雅黑", Font.PLAIN, 14));
            navIndicators[index].setOpaque(false);
        } else {
            navItems[index].setOpaque(false);
            navLabels[index].setForeground(new Color(203, 213, 225));
            navLabels[index].setFont(new Font("微软雅黑", Font.PLAIN, 14));
            navIndicators[index].setOpaque(false);
        }

        navItems[index].revalidate();
        navItems[index].repaint();
    }

    private void switchPanel(int index) {
        int previousIndex = selectedIndex;
        selectedIndex = index;

        // 1. 立即更新导航样式（在 EDT 上同步执行，保证按钮颜色切换无卡顿）
        for (int i = 0; i < navItems.length; i++) {
            updateNavItemStyle(i, false, i == index);
        }

        // 2. 使用 CardLayout 切换面板（几乎瞬间完成）
        String cardName = "card" + index;
        JComponent panel = cachedPanels[index];
        if (panel == null) {
            panel = createPanel(index);
            cachedPanels[index] = panel;
            contentPanel.add(panel, cardName);
        }
        cardLayout.show(contentPanel, cardName);

        // 3. 异步加载数据（不阻塞 UI 渲染）
        if (panel instanceof RefreshablePanel) {
            RefreshablePanel refreshable = (RefreshablePanel) panel;
            refreshable.setBusy(true);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    refreshable.refreshDataIfVisible();
                    return null;
                }
                @Override
                protected void done() {
                    refreshable.setBusy(false);
                }
            }.execute();
        }
    }

    private JComponent createPanel(int index) {
        switch (index) {
            case 0: return new BridgeManagePanel();
            case 1: return new BridgeInitialCheckPanel();
            case 2: return new BridgeRegularCheckPanel();
            case 3: return new StatisticsPanel();
            case 4: return new UserManagePanel();
            case 5: return new SystemMaintenancePanel();
            default: return new JPanel();
        }
    }
}
