package ui;

import entity.DictionaryItem;
import service.DictionaryService;
import ui.common.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * 数据字典管理面板
 * 管理员可配置桥梁类型、结构类型、检查等级等下拉选项
 */
public class DictionaryPanel extends JPanel implements RefreshablePanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> typeBox;
    private JTextField codeField, nameField, orderField;
    private RoundedButton addBtn, editBtn, deleteBtn, refreshBtn, initBtn;
    private LoadingOverlay loadingOverlay;
    private boolean dataLoaded = false;

    private List<DictionaryItem> currentList;
    private int selectedId = -1;

    public DictionaryPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(ThemeColors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        initToolbar();
        initTable();
        initFormPanel();

        loadingOverlay = new LoadingOverlay();
        add(loadingOverlay, 0);
    }

    private void initToolbar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);

        typeBox = new JComboBox<>(new String[]{"bridge_type", "structure_type", "check_level"});
        typeBox.setPreferredSize(new Dimension(150, 28));
        typeBox.addActionListener(e -> refreshBtn.doClick());

        addBtn = new RoundedButton("新增", ThemeColors.SUCCESS);
        editBtn = new RoundedButton("修改", ThemeColors.INFO);
        deleteBtn = new RoundedButton("删除", ThemeColors.DANGER);
        refreshBtn = new RoundedButton("刷新", ThemeColors.PRIMARY);
        initBtn = new RoundedButton("初始化默认", ThemeColors.WARNING);

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
        addBtn.addActionListener(e -> doAdd());
        editBtn.addActionListener(e -> doEdit());
        deleteBtn.addActionListener(e -> doDelete());
        initBtn.addActionListener(e -> {
            DictionaryService.getInstance().initDefaultData();
            refreshBtn.doClick();
            JOptionPane.showMessageDialog(this, "默认数据初始化完成！");
        });

        panel.add(new JLabel("字典类型:"));
        panel.add(typeBox);
        panel.add(refreshBtn);
        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(initBtn);

        add(panel, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] columns = {"ID", "字典类型", "编码", "名称", "排序"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new StyledTable();
        table.setModel(tableModel);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0 && currentList != null && row < currentList.size()) {
                    DictionaryItem item = currentList.get(row);
                    selectedId = item.getId();
                    fillForm(item);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initFormPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);

        codeField = new JTextField(10);
        nameField = new JTextField(12);
        orderField = new JTextField(5);
        orderField.setText("0");

        panel.add(new JLabel("编码:"));
        panel.add(codeField);
        panel.add(new JLabel("名称:"));
        panel.add(nameField);
        panel.add(new JLabel("排序:"));
        panel.add(orderField);

        add(panel, BorderLayout.SOUTH);
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
        String type = (String) typeBox.getSelectedItem();
        currentList = DictionaryService.getInstance().getItemsByType(type);
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        if (currentList == null) return;
        for (DictionaryItem item : currentList) {
            tableModel.addRow(new Object[]{
                    item.getId(), item.getDictType(), item.getItemCode(),
                    item.getItemName(), item.getSortOrder()
            });
        }
    }

    private void doAdd() {
        DictionaryItem item = getFormData();
        if (item == null) return;
        if (DictionaryService.getInstance().addItem(item)) {
            JOptionPane.showMessageDialog(this, "添加成功！");
            refreshBtn.doClick();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "添加失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doEdit() {
        if (selectedId <= 0) {
            JOptionPane.showMessageDialog(this, "请先选择要修改的字典项！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DictionaryItem item = getFormData();
        if (item == null) return;
        item.setId(selectedId);
        if (DictionaryService.getInstance().updateItem(item)) {
            JOptionPane.showMessageDialog(this, "修改成功！");
            refreshBtn.doClick();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "修改失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        if (selectedId <= 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的字典项！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, "确定删除该字典项？", "确认", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            if (DictionaryService.getInstance().deleteItem(selectedId)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                refreshBtn.doClick();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private DictionaryItem getFormData() {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        if (code.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "编码和名称不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        DictionaryItem item = new DictionaryItem();
        item.setDictType((String) typeBox.getSelectedItem());
        item.setItemCode(code);
        item.setItemName(name);
        try {
            item.setSortOrder(Integer.parseInt(orderField.getText().trim()));
        } catch (NumberFormatException e) {
            item.setSortOrder(0);
        }
        return item;
    }

    private void fillForm(DictionaryItem item) {
        codeField.setText(item.getItemCode());
        nameField.setText(item.getItemName());
        orderField.setText(String.valueOf(item.getSortOrder()));
    }

    private void clearForm() {
        selectedId = -1;
        codeField.setText("");
        nameField.setText("");
        orderField.setText("0");
    }
}
