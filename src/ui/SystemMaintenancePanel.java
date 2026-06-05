package ui;

import service.ReportService;
import ui.common.CardPanel;
import ui.common.RoundedButton;
import ui.common.ThemeColors;
import util.DBUtil;
import util.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 系统维护面板
 * 包含：数据备份、数据恢复、查看日志
 */
public class SystemMaintenancePanel extends JPanel {

    private JTextArea logArea;

    public SystemMaintenancePanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(ThemeColors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        initTopPanel();
        initLogPanel();
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setOpaque(false);

        RoundedButton backupBtn = new RoundedButton("备份数据", ThemeColors.SUCCESS);
        RoundedButton restoreBtn = new RoundedButton("恢复数据", ThemeColors.INFO);
        RoundedButton clearLogBtn = new RoundedButton("清空日志", ThemeColors.DANGER);
        RoundedButton refreshLogBtn = new RoundedButton("刷新日志", ThemeColors.PRIMARY);

        backupBtn.addActionListener(e -> doBackup());
        restoreBtn.addActionListener(e -> doRestore());
        clearLogBtn.addActionListener(e -> doClearLog());
        refreshLogBtn.addActionListener(e -> loadLogs());

        topPanel.add(backupBtn);
        topPanel.add(restoreBtn);
        topPanel.add(refreshLogBtn);
        topPanel.add(clearLogBtn);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initLogPanel() {
        CardPanel card = new CardPanel(new BorderLayout());
        card.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "系统日志",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 15), ThemeColors.TEXT_PRIMARY));

        logArea = new JTextArea();
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setBackground(new Color(250, 250, 250));
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeColors.BORDER));
        card.add(scrollPane, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
        loadLogs();
    }

    private void doBackup() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("bridge_backup_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.endsWith(".csv")) path += ".csv";

            if (ReportService.getInstance().exportBridgesToCSV(path)) {
                JOptionPane.showMessageDialog(this, "数据备份成功！\n保存路径: " + path);
                Logger.info("数据备份完成: " + path);
            } else {
                JOptionPane.showMessageDialog(this, "数据备份失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void doRestore() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV文件", "csv"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            JOptionPane.showMessageDialog(this,
                "数据恢复功能需要配合数据库导入操作。\n" +
                "请使用 SQL Server Management Studio 导入文件:\n" + file.getAbsolutePath() +
                "\n\n或使用 bcp 命令行工具进行批量导入。",
                "数据恢复", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void doClearLog() {
        int result = JOptionPane.showConfirmDialog(this, "确定要清空所有日志文件吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            File logDir = new File("logs");
            if (logDir.exists()) {
                File[] files = logDir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        f.delete();
                    }
                }
            }
            logArea.setText("日志已清空\n");
            Logger.info("日志被清空");
        }
    }

    private void loadLogs() {
        logArea.setText("");
        File logDir = new File("logs");
        if (!logDir.exists() || !logDir.isDirectory()) {
            logArea.append("暂无日志文件\n");
            return;
        }

        File[] files = logDir.listFiles((dir, name) -> name.endsWith(".log"));
        if (files == null || files.length == 0) {
            logArea.append("暂无日志文件\n");
            return;
        }

        // 按修改时间排序，最新的在前
        java.util.Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

        for (File f : files) {
            logArea.append("===== " + f.getName() + " =====\n");
            try {
                String content = new String(Files.readAllBytes(f.toPath()));
                // 只显示最近 100 行
                String[] lines = content.split("\n");
                int start = Math.max(0, lines.length - 100);
                for (int i = start; i < lines.length; i++) {
                    logArea.append(lines[i] + "\n");
                }
            } catch (IOException e) {
                logArea.append("读取失败: " + e.getMessage() + "\n");
            }
            logArea.append("\n");
        }

        // 滚动到底部
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
