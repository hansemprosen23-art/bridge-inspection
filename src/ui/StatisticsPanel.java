package ui;

import service.StatisticsService;
import ui.common.*;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * 数据统计查询面板
 * 负责模块: 曹城钧
 */
public class StatisticsPanel extends JPanel implements RefreshablePanel {

    private JPanel totalBridgeLabel, totalInitialLabel, totalRegularLabel;
    private JTextArea typeStatArea, levelStatArea, statusStatArea, yearStatArea;
    private RoundedButton refreshBtn;
    private boolean dataLoaded = false;
    private LoadingOverlay loadingOverlay;

    public StatisticsPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(ThemeColors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initSummaryPanel();
        initDetailPanel();

        loadingOverlay = new LoadingOverlay();
        add(loadingOverlay, 0);
    }
    
    private void initSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);
        
        totalBridgeLabel = createStatCard("桥梁总数", "0", new Color(41, 98, 255), "🌉");
        totalInitialLabel = createStatCard("初始检查", "0", new Color(46, 125, 50), "📋");
        totalRegularLabel = createStatCard("定期检查", "0", new Color(211, 47, 47), "📊");
        
        refreshBtn = new RoundedButton("⟳ 刷新数据", ThemeColors.PRIMARY);
        refreshBtn.setPreferredSize(new Dimension(140, 46));
        refreshBtn.addActionListener(e -> {
            setBusy(true);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    refreshData();
                    return null;
                }
                @Override
                protected void done() {
                    setBusy(false);
                }
            }.execute();
        });
        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setOpaque(false);
        btnPanel.add(refreshBtn);
        
        panel.add(totalBridgeLabel);
        panel.add(totalInitialLabel);
        panel.add(totalRegularLabel);
        panel.add(btnPanel);
        
        add(panel, BorderLayout.NORTH);
    }
    
    private JPanel createStatCard(String title, String value, Color color, String icon) {
        CardPanel card = new CardPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("微软雅黑", Font.BOLD, 36));
        valueLabel.setForeground(color);
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        titleLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        
        JPanel center = new JPanel(new GridLayout(2, 1, 0, 4));
        center.setOpaque(false);
        center.add(valueLabel);
        center.add(titleLabel);
        
        card.add(iconLabel, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);
        
        // 底部彩色条
        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(0, 4));
        bar.setBackground(color);
        bar.setOpaque(true);
        card.add(bar, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void initDetailPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setOpaque(false);
        
        typeStatArea = createStatArea();
        levelStatArea = createStatArea();
        statusStatArea = createStatArea();
        yearStatArea = createStatArea();
        
        panel.add(wrapInCard(typeStatArea, "按桥梁类型统计", ThemeColors.PRIMARY));
        panel.add(wrapInCard(levelStatArea, "按检查等级统计", ThemeColors.INFO));
        panel.add(wrapInCard(statusStatArea, "按技术状况等级统计", ThemeColors.WARNING));
        panel.add(wrapInCard(yearStatArea, "按年份统计定期检查", ThemeColors.SUCCESS));
        
        add(panel, BorderLayout.CENTER);
    }
    
    private JTextArea createStatArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        area.setBackground(Color.WHITE);
        area.setForeground(ThemeColors.TEXT_PRIMARY);
        return area;
    }
    
    private JPanel wrapInCard(JTextArea area, String title, Color accentColor) {
        CardPanel card = new CardPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("  " + title);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        titleLabel.setForeground(accentColor);
        titleLabel.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor));
        titleLabel.setPreferredSize(new Dimension(0, 40));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(area, BorderLayout.CENTER);
        return card;
    }
    
    @Override
    public void refreshDataIfVisible() {
        if (dataLoaded) {
            SwingUtilities.invokeLater(() -> refreshData());
            return;
        }
        dataLoaded = true;
        refreshData();
    }

    @Override
    public void setBusy(boolean busy) {
        if (busy) {
            loadingOverlay.showOverlay();
        } else {
            loadingOverlay.hideOverlay();
        }
    }

    private void refreshData() {
        // 使用合并查询，一次数据库连接获取所有统计
        StatisticsService.StatisticsResult stats = StatisticsService.getInstance().getAllStatistics();

        updateCardValue(totalBridgeLabel, String.valueOf(stats.totalBridges));
        updateCardValue(totalInitialLabel, String.valueOf(stats.totalInitialChecks));
        updateCardValue(totalRegularLabel, String.valueOf(stats.totalRegularChecks));

        typeStatArea.setText(formatMap(stats.byBridgeType, " 座"));
        levelStatArea.setText(formatMap(stats.byCheckLevel, " 座"));
        statusStatArea.setText(formatMap(stats.byTechStatus, " 条记录"));
        yearStatArea.setText(formatMap(stats.byYear, " 次检查"));
    }
    
    private void updateCardValue(JPanel card, String value) {
        Component center = ((BorderLayout)card.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (center instanceof JPanel) {
            Component valueComp = ((JPanel)center).getComponent(0);
            if (valueComp instanceof JLabel) ((JLabel)valueComp).setText(value);
        }
    }
    
    private String formatMap(Map<String, Integer> map, String suffix) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            sb.append("• ").append(entry.getKey()).append(" : ").append(entry.getValue()).append(suffix).append("\n");
        }
        return sb.toString();
    }
}
