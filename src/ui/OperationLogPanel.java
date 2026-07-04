package ui;

import entity.OperationLog;
import entity.User;
import service.OperationLogService;
import ui.common.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 操作日志审计面板
 * 仅管理员可见
 */
public class OperationLogPanel extends JPanel implements RefreshablePanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private SearchTextField searchField;
    private RoundedButton refreshBtn;
    private OutlineButton filterLoginBtn, filterDataBtn, filterExportBtn;
    private LoadingOverlay loadingOverlay;
    private boolean dataLoaded = false;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public OperationLogPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(ThemeColors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        initTable();
        initToolbar();

        loadingOverlay = new LoadingOverlay();
        add(loadingOverlay, 0);
    }

    private void initTable() {
        String[] columns = {"ID", "用户", "操作类型", "操作描述", "IP地址", "操作时间"};
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

    private void initToolbar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);

        searchField = new SearchTextField(18);
        searchField.setDebounceMs(300);
        searchField.setSearchCallback(this::doSearch);

        refreshBtn = new RoundedButton("刷新", ThemeColors.PRIMARY);
        filterLoginBtn = new OutlineButton("登录日志", ThemeColors.INFO);
        filterDataBtn = new OutlineButton("数据操作", ThemeColors.SUCCESS);
        filterExportBtn = new OutlineButton("导出操作", ThemeColors.WARNING);

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

        filterLoginBtn.addActionListener(e -> doFilter("登录"));
        filterDataBtn.addActionListener(e -> doFilter("数据操作"));
        filterExportBtn.addActionListener(e -> doFilter("导出"));

        panel.add(new JLabel("搜索用户:"));
        panel.add(searchField);
        panel.add(refreshBtn);
        panel.add(filterLoginBtn);
        panel.add(filterDataBtn);
        panel.add(filterExportBtn);

        add(panel, BorderLayout.NORTH);
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
        List<OperationLog> logs = OperationLogService.getInstance().getAllLogs();
        refreshTable(logs);
    }

    private void doSearch(String username) {
        if (username.isEmpty()) {
            loadData();
            return;
        }
        List<OperationLog> logs = OperationLogService.getInstance().getLogsByUser(username);
        refreshTable(logs);
    }

    private void doFilter(String type) {
        String operationType;
        switch (type) {
            case "登录": operationType = "登录"; break;
            case "数据操作": operationType = "数据操作"; break;
            case "导出": operationType = "导出"; break;
            default: operationType = type;
        }
        List<OperationLog> logs = OperationLogService.getInstance().getLogsByType(operationType);
        refreshTable(logs);
    }

    private void refreshTable(List<OperationLog> logs) {
        tableModel.setRowCount(0);
        if (logs == null) return;
        for (OperationLog log : logs) {
            tableModel.addRow(new Object[]{
                    log.getId(),
                    log.getUsername(),
                    log.getOperationType(),
                    log.getOperationDesc(),
                    log.getIpAddress() == null ? "-" : log.getIpAddress(),
                    log.getOperationTime() == null ? "" : sdf.format(log.getOperationTime())
            });
        }
    }
}
