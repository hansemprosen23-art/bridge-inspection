package ui;

import entity.Bridge;
import service.BridgeService;
import ui.common.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * 桥梁基本状况卡片管理面板
 * 负责模块: 张子健
 */
public class BridgeManagePanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private RoundedButton addBtn, editBtn, deleteBtn;
    private OutlineButton searchBtn, refreshBtn;

    private JTextField bridgeNoField, bridgeNameField, routeNameField, routeGradeField;
    private JTextField bridgeTypeField, structureTypeField, spanCombinationField;
    private JTextField totalLengthField, totalWidthField, clearSpanField, designLoadField;
    private JTextField antiSeismicField, designUnitField, constructUnitField, superviseUnitField;
    private JTextField completeDateField, openDateField, manageUnitField, maintainUnitField;
    private JComboBox<String> checkLevelBox;
    private JTextField techStatusField, maintenanceLengthField, longitudeField, latitudeField;
    private JTextArea remarkArea;

    private List<Bridge> currentList;
    private int selectedId = -1;

    public BridgeManagePanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(ThemeColors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        initTable();
        initSearchBar();
        initFormPanel();
        loadData();
    }

    private void initTable() {
        String[] columns = {"ID", "桥梁编号", "桥梁名称", "路线名称", "桥梁类型", "结构类型", "全长(m)", "总宽(m)", "检查等级", "技术状况"};
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
                    Bridge b = currentList.get(row);
                    selectedId = b.getId();
                    fillForm(b);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(0, 280));
        add(scrollPane, BorderLayout.NORTH);
    }

    private void initSearchBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);

        searchField = new JTextField(18);
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

        panel.add(new JLabel("搜索:"));
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

    private void initFormPanel() {
        CardPanel card = new CardPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "桥梁详细信息",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 15), ThemeColors.PRIMARY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 第1行
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("桥梁编号*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        bridgeNoField = new JTextField(14);
        card.add(bridgeNoField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("桥梁名称*"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        bridgeNameField = new JTextField(14);
        card.add(bridgeNameField, gbc);

        // 第2行
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("路线名称"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        routeNameField = new JTextField(14);
        card.add(routeNameField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("路线等级"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        routeGradeField = new JTextField(14);
        card.add(routeGradeField, gbc);

        // 第3行
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("桥梁类型"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        bridgeTypeField = new JTextField(14);
        card.add(bridgeTypeField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("结构类型"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        structureTypeField = new JTextField(14);
        card.add(structureTypeField, gbc);

        // 第4行
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("跨径组合"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        spanCombinationField = new JTextField(14);
        card.add(spanCombinationField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("设计荷载"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        designLoadField = new JTextField(14);
        card.add(designLoadField, gbc);

        // 第5行
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("全长(m)"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        totalLengthField = new JTextField(14);
        card.add(totalLengthField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("总宽(m)"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        totalWidthField = new JTextField(14);
        card.add(totalWidthField, gbc);

        // 第6行
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("净跨径(m)"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        clearSpanField = new JTextField(14);
        card.add(clearSpanField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("抗震烈度"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        antiSeismicField = new JTextField(14);
        card.add(antiSeismicField, gbc);

        // 第7行
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("检查等级"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        checkLevelBox = new JComboBox<>(new String[]{"Ⅰ", "Ⅱ", "Ⅲ"});
        card.add(checkLevelBox, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("技术状况"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        techStatusField = new JTextField(14);
        card.add(techStatusField, gbc);

        // 第8行
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("竣工日期"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        completeDateField = new JTextField(14);
        card.add(completeDateField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("通车日期"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        openDateField = new JTextField(14);
        card.add(openDateField, gbc);

        // 第9行
        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("管理单位"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        manageUnitField = new JTextField(14);
        card.add(manageUnitField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("养护单位"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        maintainUnitField = new JTextField(14);
        card.add(maintainUnitField, gbc);

        // 第10行
        gbc.gridy = 9;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("设计单位"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        designUnitField = new JTextField(14);
        card.add(designUnitField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("施工单位"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        constructUnitField = new JTextField(14);
        card.add(constructUnitField, gbc);

        // 第11行
        gbc.gridy = 10;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("监理单位"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        superviseUnitField = new JTextField(14);
        card.add(superviseUnitField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("养护里程(m)"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        maintenanceLengthField = new JTextField(14);
        card.add(maintenanceLengthField, gbc);

        // 第12行
        gbc.gridy = 11;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("经度"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        longitudeField = new JTextField(14);
        card.add(longitudeField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("纬度"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        latitudeField = new JTextField(14);
        card.add(latitudeField, gbc);

        // 备注
        gbc.gridy = 12;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("备注"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        remarkArea = new JTextArea(3, 30);
        remarkArea.setLineWrap(true);
        remarkArea.setBorder(BorderFactory.createLineBorder(ThemeColors.BORDER));
        JScrollPane remarkScroll = new JScrollPane(remarkArea);
        remarkScroll.setBorder(BorderFactory.createLineBorder(ThemeColors.BORDER));
        card.add(remarkScroll, gbc);

        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(0, 360));
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void loadData() {
        currentList = BridgeService.getInstance().getAllBridges();
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Bridge b : currentList) {
            tableModel.addRow(new Object[]{
                    b.getId(), b.getBridgeNo(), b.getBridgeName(), b.getRouteName(),
                    b.getBridgeType(), b.getStructureType(), b.getTotalLength(),
                    b.getTotalWidth(), b.getCheckLevel(), b.getTechStatus()
            });
        }
    }

    private void doSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        currentList = BridgeService.getInstance().searchByName(keyword);
        refreshTable();
    }

    private void doAdd() {
        Bridge b = getFormData();
        if (b == null) return;
        if (BridgeService.getInstance().addBridge(b)) {
            JOptionPane.showMessageDialog(this, "添加成功！");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "添加失败，桥梁编号可能已存在！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doEdit() {
        if (selectedId <= 0) {
            JOptionPane.showMessageDialog(this, "请先选择要修改的记录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Bridge b = getFormData();
        if (b == null) return;
        b.setId(selectedId);
        if (BridgeService.getInstance().updateBridge(b)) {
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
        int result = JOptionPane.showConfirmDialog(this, "确定要删除该桥梁记录吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            if (BridgeService.getInstance().deleteBridge(selectedId)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Bridge getFormData() {
        String bridgeNo = bridgeNoField.getText().trim();
        String bridgeName = bridgeNameField.getText().trim();
        if (bridgeNo.isEmpty() || bridgeName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "桥梁编号和名称不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        Bridge b = new Bridge();
        b.setBridgeNo(bridgeNo);
        b.setBridgeName(bridgeName);
        b.setRouteName(routeNameField.getText().trim());
        b.setRouteGrade(routeGradeField.getText().trim());
        b.setBridgeType(bridgeTypeField.getText().trim());
        b.setStructureType(structureTypeField.getText().trim());
        b.setSpanCombination(spanCombinationField.getText().trim());
        try {
            b.setTotalLength(Double.parseDouble(totalLengthField.getText().trim().isEmpty() ? "0" : totalLengthField.getText().trim()));
            b.setTotalWidth(Double.parseDouble(totalWidthField.getText().trim().isEmpty() ? "0" : totalWidthField.getText().trim()));
            b.setClearSpan(Double.parseDouble(clearSpanField.getText().trim().isEmpty() ? "0" : clearSpanField.getText().trim()));
            b.setMaintenanceLength(Double.parseDouble(maintenanceLengthField.getText().trim().isEmpty() ? "0" : maintenanceLengthField.getText().trim()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "数值格式错误！", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        b.setDesignLoad(designLoadField.getText().trim());
        b.setAntiSeismic(antiSeismicField.getText().trim());
        b.setDesignUnit(designUnitField.getText().trim());
        b.setConstructUnit(constructUnitField.getText().trim());
        b.setSuperviseUnit(superviseUnitField.getText().trim());
        b.setCompleteDate(completeDateField.getText().trim());
        b.setOpenDate(openDateField.getText().trim());
        b.setManageUnit(manageUnitField.getText().trim());
        b.setMaintainUnit(maintainUnitField.getText().trim());
        b.setCheckLevel((String) checkLevelBox.getSelectedItem());
        try {
            b.setTechStatus(Integer.parseInt(techStatusField.getText().trim().isEmpty() ? "1" : techStatusField.getText().trim()));
        } catch (NumberFormatException e) {
            b.setTechStatus(1);
        }
        b.setLongitude(longitudeField.getText().trim());
        b.setLatitude(latitudeField.getText().trim());
        b.setRemark(remarkArea.getText().trim());
        return b;
    }

    private void fillForm(Bridge b) {
        bridgeNoField.setText(b.getBridgeNo());
        bridgeNameField.setText(b.getBridgeName());
        routeNameField.setText(b.getRouteName());
        routeGradeField.setText(b.getRouteGrade());
        bridgeTypeField.setText(b.getBridgeType());
        structureTypeField.setText(b.getStructureType());
        spanCombinationField.setText(b.getSpanCombination());
        totalLengthField.setText(String.valueOf(b.getTotalLength()));
        totalWidthField.setText(String.valueOf(b.getTotalWidth()));
        clearSpanField.setText(String.valueOf(b.getClearSpan()));
        designLoadField.setText(b.getDesignLoad());
        antiSeismicField.setText(b.getAntiSeismic());
        designUnitField.setText(b.getDesignUnit());
        constructUnitField.setText(b.getConstructUnit());
        superviseUnitField.setText(b.getSuperviseUnit());
        completeDateField.setText(b.getCompleteDate() != null ? b.getCompleteDate() : "");
        openDateField.setText(b.getOpenDate() != null ? b.getOpenDate() : "");
        manageUnitField.setText(b.getManageUnit());
        maintainUnitField.setText(b.getMaintainUnit());
        checkLevelBox.setSelectedItem(b.getCheckLevel());
        techStatusField.setText(String.valueOf(b.getTechStatus()));
        maintenanceLengthField.setText(String.valueOf(b.getMaintenanceLength()));
        longitudeField.setText(b.getLongitude());
        latitudeField.setText(b.getLatitude());
        remarkArea.setText(b.getRemark());
    }

    private void clearForm() {
        selectedId = -1;
        bridgeNoField.setText("");
        bridgeNameField.setText("");
        routeNameField.setText("");
        routeGradeField.setText("");
        bridgeTypeField.setText("");
        structureTypeField.setText("");
        spanCombinationField.setText("");
        totalLengthField.setText("");
        totalWidthField.setText("");
        clearSpanField.setText("");
        designLoadField.setText("");
        antiSeismicField.setText("");
        designUnitField.setText("");
        constructUnitField.setText("");
        superviseUnitField.setText("");
        completeDateField.setText("");
        openDateField.setText("");
        manageUnitField.setText("");
        maintainUnitField.setText("");
        checkLevelBox.setSelectedIndex(1);
        techStatusField.setText("");
        maintenanceLengthField.setText("");
        longitudeField.setText("");
        latitudeField.setText("");
        remarkArea.setText("");
    }
}
