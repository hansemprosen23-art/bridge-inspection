package ui;

import entity.CheckReminder;
import service.ReminderService;
import ui.common.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 检查提醒面板
 * 展示即将到期的桥梁检查计划
 */
public class ReminderPanel extends JPanel implements RefreshablePanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> thresholdBox;
    private RoundedButton refreshBtn;
    private LoadingOverlay loadingOverlay;
    private boolean dataLoaded = false;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public ReminderPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(ThemeColors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        initToolbar();
        initTable();

        loadingOverlay = new LoadingOverlay();
        add(loadingOverlay, 0);
    }

    private void initToolbar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);

        panel.add(new JLabel("提醒范围:"));
        thresholdBox = new JComboBox<>(new String[]{"7天内", "15天内", "30天内", "90天内"});
        thresholdBox.setSelectedIndex(2);
        thresholdBox.addActionListener(e -> refreshBtn.doClick());

        refreshBtn = new RoundedButton("刷新提醒", ThemeColors.WARNING);
        refreshBtn.addActionListener(e -> {
            setBusy(true);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    loadData();
                    return null;
                }
                @Override
                protected void done() {
                    setBusy(false);
                }
            }.execute();
        });

        panel.add(thresholdBox);
        panel.add(refreshBtn);
        add(panel, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] columns = {"桥梁名称", "检查类型", "计划日期", "剩余天数", "紧急程度"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new StyledTable();
        table.setModel(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void refreshDataIfVisible() {
        if (dataLoaded) {
            SwingUtilities.invokeLater(() -> loadData());
            return;
        }
        dataLoaded = true;
        loadData();
    }

    @Override
    public void setBusy(boolean busy) {
        if (busy) loadingOverlay.showOverlay();
        else loadingOverlay.hideOverlay();
    }

    private void loadData() {
        int days = getSelectedDays();
        List<CheckReminder> reminders = ReminderService.getInstance().getUpcomingChecks(days);
        refreshTable(reminders);
    }

    private int getSelectedDays() {
        String selected = (String) thresholdBox.getSelectedItem();
        if (selected == null) return 30;
        return Integer.parseInt(selected.replace("天内", "").trim());
    }

    private void refreshTable(List<CheckReminder> reminders) {
        tableModel.setRowCount(0);
        if (reminders == null) return;
        for (CheckReminder r : reminders) {
            tableModel.addRow(new Object[]{
                    r.getBridgeName(),
                    r.getCheckType(),
                    r.getPlannedDate() == null ? "" : sdf.format(r.getPlannedDate()),
                    r.getDaysRemaining() < 0 ? "已过期 " + (-r.getDaysRemaining()) + " 天" : r.getDaysRemaining() + " 天",
                    r.getUrgency()
            });
        }
    }
}
