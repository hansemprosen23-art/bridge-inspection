package ui;

import entity.Bridge;
import entity.BridgeInitialCheck;
import service.BridgeInitialCheckService;
import service.BridgeService;
import ui.common.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * 桥梁初始检查记录管理面板
 * 负责模块: 郑晟
 */
public class BridgeInitialCheckPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private RoundedButton addBtn, editBtn, deleteBtn;
    private OutlineButton searchBtn, refreshBtn;

    private JComboBox<String> bridgeCombo;
    private JTextField checkNoField, checkDateField, checkerField;
    private JTextField weatherField, temperatureField;
    private JTextArea checkContentArea;
    private JComboBox<String> deckConditionBox, superstructureConditionBox, substructureConditionBox, accessoryConditionBox;
    private JTextArea defectDescArea, suggestArea, conclusionArea;
    private JTextField nextCheckDateField;

    private List<BridgeInitialCheck> currentList;
    private int selectedId = -1;

    public BridgeInitialCheckPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(ThemeColors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        initTable();
        initSearchBar();
        initFormPanel();
        loadData();
        loadBridgeCombo();
    }

    private void initTable() {
        String[] columns = {"ID", "检查编号", "桥梁名称", "检查日期", "检查人", "天气", "桥面系", "上部结构", "下部结构", "附属设施"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new StyledTable();
        table.setModel(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0 && currentList != null && row < currentList.size()) {
                    BridgeInitialCheck c = currentList.get(row);
                    selectedId = c.getId();
                    fillForm(c);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(0, 260));
        add(scrollPane, BorderLayout.NORTH);
    }

    private void initSearchBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);

        searchField = new JTextField(16);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        searchBtn = new OutlineButton("搜索", ThemeColors.PRIMARY);
        refreshBtn = new OutlineButton("刷新", ThemeColors.TEXT_SECONDARY);
        addBtn = new RoundedButton("新增", ThemeColors.SUCCESS);
        editBtn = new RoundedButton("修改", ThemeColors.INFO);
        deleteBtn = new RoundedButton("删除", ThemeColors.DANGER);

        panel.add(new JLabel("搜索桥梁:"));
        panel.add(searchField);
        panel.add(searchBtn);
        panel.add(refreshBtn);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);

        searchBtn.addActionListener(e -> doSearch());
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadData();
            clearForm();
        });
        addBtn.addActionListener(e -> doAdd());
        editBtn.addActionListener(e -> doEdit());
        deleteBtn.addActionListener(e -> doDelete());

        add(panel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        lbl.setForeground(ThemeColors.TEXT_SECONDARY);
        return lbl;
    }

    private void addTextArea(JPanel card, GridBagConstraints gbc, int gy, String text, JTextArea area) {
        gbc.gridy = gy;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel(text), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        area.setLineWrap(true);
        area.setBorder(BorderFactory.createLineBorder(ThemeColors.BORDER));
        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(BorderFactory.createLineBorder(ThemeColors.BORDER));
        card.add(sp, gbc);
        gbc.gridwidth = 1;
    }

    private void initFormPanel() {
        CardPanel card = new CardPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "初始检查记录详情",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 15), ThemeColors.SUCCESS));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] conditionOptions = {"完好", "轻微缺损", "中等缺损", "严重缺损", "危险"};

        // 第1行
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("选择桥梁*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        bridgeCombo = new JComboBox<>();
        bridgeCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        card.add(bridgeCombo, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("检查编号*"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        checkNoField = new JTextField(14);
        card.add(checkNoField, gbc);

        // 第2行
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("检查日期*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        checkDateField = new JTextField(14);
        card.add(checkDateField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("检查人"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        checkerField = new JTextField(14);
        card.add(checkerField, gbc);

        // 第3行
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("天气"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        weatherField = new JTextField(14);
        card.add(weatherField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("温度"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        temperatureField = new JTextField(14);
        card.add(temperatureField, gbc);

        // 第4行
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("桥面系状况"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        deckConditionBox = new JComboBox<>(conditionOptions);
        card.add(deckConditionBox, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("上部结构状况"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        superstructureConditionBox = new JComboBox<>(conditionOptions);
        card.add(superstructureConditionBox, gbc);

        // 第5行
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("下部结构状况"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        substructureConditionBox = new JComboBox<>(conditionOptions);
        card.add(substructureConditionBox, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("附属设施状况"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        accessoryConditionBox = new JComboBox<>(conditionOptions);
        card.add(accessoryConditionBox, gbc);

        // 文本域
        checkContentArea = new JTextArea(2, 30);
        addTextArea(card, gbc, 5, "检查内容", checkContentArea);

        defectDescArea = new JTextArea(2, 30);
        addTextArea(card, gbc, 6, "缺损描述", defectDescArea);

        suggestArea = new JTextArea(2, 30);
        addTextArea(card, gbc, 7, "处理建议", suggestArea);

        conclusionArea = new JTextArea(2, 30);
        addTextArea(card, gbc, 8, "检查结论", conclusionArea);

        // 下次检查日期
        gbc.gridy = 9;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("下次检查日期"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        nextCheckDateField = new JTextField(14);
        card.add(nextCheckDateField, gbc);

        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(0, 380));
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void loadBridgeCombo() {
        bridgeCombo.removeAllItems();
        List<Bridge> bridges = BridgeService.getInstance().getAllBridges();
        for (Bridge b : bridges) bridgeCombo.addItem(b.getId() + "-" + b.getBridgeName());
    }

    private void loadData() {
        currentList = BridgeInitialCheckService.getInstance().getAllChecks();
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (BridgeInitialCheck c : currentList) {
            tableModel.addRow(new Object[]{
                    c.getId(), c.getCheckNo(), c.getBridgeName(), c.getCheckDate(),
                    c.getChecker(), c.getWeather(), c.getDeckCondition(),
                    c.getSuperstructureCondition(), c.getSubstructureCondition(), c.getAccessoryCondition()
            });
        }
    }

    private void doSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        currentList = BridgeInitialCheckService.getInstance().searchByBridgeName(keyword);
        refreshTable();
    }

    private void doAdd() {
        BridgeInitialCheck c = getFormData();
        if (c == null) return;
        if (BridgeInitialCheckService.getInstance().addCheck(c)) {
            JOptionPane.showMessageDialog(this, "添加成功！");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "添加失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doEdit() {
        if (selectedId <= 0) {
            JOptionPane.showMessageDialog(this, "请先选择要修改的记录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BridgeInitialCheck c = getFormData();
        if (c == null) return;
        c.setId(selectedId);
        if (BridgeInitialCheckService.getInstance().updateCheck(c)) {
            JOptionPane.showMessageDialog(this, "修改成功！");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "修改失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        if (selectedId <= 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的记录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, "确定要删除该检查记录吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            if (BridgeInitialCheckService.getInstance().deleteCheck(selectedId)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private BridgeInitialCheck getFormData() {
        if (bridgeCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "请选择桥梁！", "提示", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String bridgeItem = (String) bridgeCombo.getSelectedItem();
        int bridgeId = Integer.parseInt(bridgeItem.split("-")[0]);

        String checkNo = checkNoField.getText().trim();
        String checkDate = checkDateField.getText().trim();
        if (checkNo.isEmpty() || checkDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "检查编号和日期不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        BridgeInitialCheck c = new BridgeInitialCheck();
        c.setBridgeId(bridgeId);
        c.setCheckNo(checkNo);
        c.setCheckDate(checkDate);
        c.setChecker(checkerField.getText().trim());
        c.setWeather(weatherField.getText().trim());
        c.setTemperature(temperatureField.getText().trim());
        c.setCheckContent(checkContentArea.getText().trim());
        c.setDeckCondition((String) deckConditionBox.getSelectedItem());
        c.setSuperstructureCondition((String) superstructureConditionBox.getSelectedItem());
        c.setSubstructureCondition((String) substructureConditionBox.getSelectedItem());
        c.setAccessoryCondition((String) accessoryConditionBox.getSelectedItem());
        c.setDefectDesc(defectDescArea.getText().trim());
        c.setSuggest(suggestArea.getText().trim());
        c.setConclusion(conclusionArea.getText().trim());
        c.setNextCheckDate(nextCheckDateField.getText().trim());
        return c;
    }

    private void fillForm(BridgeInitialCheck c) {
        for (int i = 0; i < bridgeCombo.getItemCount(); i++) {
            if (bridgeCombo.getItemAt(i).startsWith(c.getBridgeId() + "-")) {
                bridgeCombo.setSelectedIndex(i);
                break;
            }
        }
        checkNoField.setText(c.getCheckNo());
        checkDateField.setText(c.getCheckDate());
        checkerField.setText(c.getChecker());
        weatherField.setText(c.getWeather());
        temperatureField.setText(c.getTemperature());
        checkContentArea.setText(c.getCheckContent());
        deckConditionBox.setSelectedItem(c.getDeckCondition());
        superstructureConditionBox.setSelectedItem(c.getSuperstructureCondition());
        substructureConditionBox.setSelectedItem(c.getSubstructureCondition());
        accessoryConditionBox.setSelectedItem(c.getAccessoryCondition());
        defectDescArea.setText(c.getDefectDesc());
        suggestArea.setText(c.getSuggest());
        conclusionArea.setText(c.getConclusion());
        nextCheckDateField.setText(c.getNextCheckDate());
    }

    private void clearForm() {
        selectedId = -1;
        if (bridgeCombo.getItemCount() > 0) bridgeCombo.setSelectedIndex(0);
        checkNoField.setText("");
        checkDateField.setText("");
        checkerField.setText("");
        weatherField.setText("");
        temperatureField.setText("");
        checkContentArea.setText("");
        deckConditionBox.setSelectedIndex(0);
        superstructureConditionBox.setSelectedIndex(0);
        substructureConditionBox.setSelectedIndex(0);
        accessoryConditionBox.setSelectedIndex(0);
        defectDescArea.setText("");
        suggestArea.setText("");
        conclusionArea.setText("");
        nextCheckDateField.setText("");
    }
}
