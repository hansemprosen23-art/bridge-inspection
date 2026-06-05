package ui;

import entity.Bridge;
import entity.BridgeRegularCheck;
import service.BridgeRegularCheckService;
import service.BridgeService;
import service.ReportService;
import ui.common.*;
import util.BCICalculator;
import util.Logger;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 桥梁定期检查记录管理面板
 * 负责模块: 谭容昊
 */
public class BridgeRegularCheckPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private RoundedButton addBtn, editBtn, deleteBtn, calcBtn, templateBtn, reportBtn;
    private OutlineButton searchBtn, refreshBtn;

    private JComboBox<String> bridgeCombo;
    private JTextField checkNoField, checkDateField, checkerField;
    private JTextField weatherField, temperatureField;
    private JComboBox<String> checkTypeBox;
    private JTextField deckScoreField, superstructureScoreField, substructureScoreField, accessoryScoreField;
    private JTextField bciField;
    private JComboBox<String> techStatusBox;
    private JTextArea defectDescArea, maintenanceSuggestArea, limitationSuggestArea, checkConclusionArea;
    private JTextField nextCheckDateField;

    private List<BridgeRegularCheck> currentList;
    private int selectedId = -1;

    public BridgeRegularCheckPanel() {
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
        String[] columns = {"ID", "检查编号", "桥梁名称", "检查日期", "类型", "检查人", "BCI", "技术状况", "下次检查"};
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
                    BridgeRegularCheck c = currentList.get(row);
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
        calcBtn = new RoundedButton("计算BCI", new Color(123, 31, 162));
        templateBtn = new RoundedButton("检查模板", new Color(0, 131, 143));
        reportBtn = new RoundedButton("导出报告", new Color(0, 105, 92));

        panel.add(new JLabel("搜索桥梁:"));
        panel.add(searchField);
        panel.add(searchBtn);
        panel.add(refreshBtn);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(templateBtn);
        panel.add(calcBtn);
        panel.add(reportBtn);
        panel.add(Box.createHorizontalStrut(10));
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
        calcBtn.addActionListener(e -> doCalcBCI());
        templateBtn.addActionListener(e -> doShowTemplate());
        reportBtn.addActionListener(e -> doExportReport());

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
        card.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "定期检查记录详情",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 15), ThemeColors.INFO));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

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
        card.add(createLabel("检查类型"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        checkTypeBox = new JComboBox<>(new String[]{"经常检查", "定期检查"});
        card.add(checkTypeBox, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("下次检查日期"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        nextCheckDateField = new JTextField(14);
        card.add(nextCheckDateField, gbc);

        // 第5行
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("桥面系评分"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        deckScoreField = new JTextField(14);
        card.add(deckScoreField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("上部结构评分"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        superstructureScoreField = new JTextField(14);
        card.add(superstructureScoreField, gbc);

        // 第6行
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("下部结构评分"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        substructureScoreField = new JTextField(14);
        card.add(substructureScoreField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("附属设施评分"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        accessoryScoreField = new JTextField(14);
        card.add(accessoryScoreField, gbc);

        // 第7行 - BCI
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("BCI指数"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        bciField = new JTextField(14);
        bciField.setEditable(false);
        bciField.setBackground(new Color(240, 240, 240));
        card.add(bciField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("技术状况等级"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        techStatusBox = new JComboBox<>(new String[]{"1类", "2类", "3类", "4类", "5类"});
        card.add(techStatusBox, gbc);

        // 文本域
        defectDescArea = new JTextArea(2, 30);
        addTextArea(card, gbc, 7, "主要缺损描述", defectDescArea);

        maintenanceSuggestArea = new JTextArea(2, 30);
        addTextArea(card, gbc, 8, "养护建议", maintenanceSuggestArea);

        limitationSuggestArea = new JTextArea(2, 30);
        addTextArea(card, gbc, 9, "限制使用建议", limitationSuggestArea);

        checkConclusionArea = new JTextArea(2, 30);
        addTextArea(card, gbc, 10, "检查结论", checkConclusionArea);

        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void loadBridgeCombo() {
        bridgeCombo.removeAllItems();
        List<Bridge> bridges = BridgeService.getInstance().getAllBridges();
        for (Bridge b : bridges) bridgeCombo.addItem(b.getId() + "-" + b.getBridgeName());
    }

    private void loadData() {
        currentList = BridgeRegularCheckService.getInstance().getAllChecks();
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (BridgeRegularCheck c : currentList) {
            tableModel.addRow(new Object[]{
                    c.getId(), c.getCheckNo(), c.getBridgeName(), c.getCheckDate(),
                    c.getCheckType(), c.getChecker(), c.getBci(), c.getTechStatus(), c.getNextCheckDate()
            });
        }
    }

    private void doSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        currentList = BridgeRegularCheckService.getInstance().searchByBridgeName(keyword);
        refreshTable();
    }

    private void doCalcBCI() {
        try {
            int deck = Integer.parseInt(deckScoreField.getText().trim().isEmpty() ? "0" : deckScoreField.getText().trim());
            int sup = Integer.parseInt(superstructureScoreField.getText().trim().isEmpty() ? "0" : superstructureScoreField.getText().trim());
            int sub = Integer.parseInt(substructureScoreField.getText().trim().isEmpty() ? "0" : substructureScoreField.getText().trim());
            int acc = Integer.parseInt(accessoryScoreField.getText().trim().isEmpty() ? "0" : accessoryScoreField.getText().trim());
            if (!ValidationUtil.isValidScore(deck) || !ValidationUtil.isValidScore(sup)
                    || !ValidationUtil.isValidScore(sub) || !ValidationUtil.isValidScore(acc)) {
                JOptionPane.showMessageDialog(this, "评分必须在0-100之间！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double bci = BridgeRegularCheckService.getInstance().calculateBCI(deck, sup, sub, acc);
            String status = BridgeRegularCheckService.getInstance().determineTechStatus(bci);
            String desc = BridgeRegularCheckService.getInstance().getTechStatusDesc(status);
            bciField.setText(String.format("%.2f", bci));
            techStatusBox.setSelectedItem(status);
            JOptionPane.showMessageDialog(this,
                String.format("BCI = %.2f\n技术状况等级: %s\n(%s)\n\n计算说明:\n桥面系权重15%% + 上部结构权重35%% + 下部结构权重35%% + 附属设施权重15%%",
                bci, status, desc),
                "BCI计算结果", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "评分必须为数字！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doShowTemplate() {
        String bridgeItem = (String) bridgeCombo.getSelectedItem();
        String bridgeType = "梁式桥";
        if (bridgeItem != null) {
            int bridgeId = Integer.parseInt(bridgeItem.split("-")[0]);
            Bridge bridge = BridgeService.getInstance().getBridgeById(bridgeId);
            if (bridge != null && bridge.getBridgeType() != null) {
                bridgeType = bridge.getBridgeType();
            }
        }

        Map<String, Map<String, Double>> template = BridgeRegularCheckService.getInstance().getCheckTemplate(bridgeType);

        StringBuilder sb = new StringBuilder();
        sb.append("桥型: ").append(bridgeType).append("\n\n");
        for (Map.Entry<String, Map<String, Double>> entry : template.entrySet()) {
            sb.append("【").append(entry.getKey()).append("】\n");
            for (Map.Entry<String, Double> comp : entry.getValue().entrySet()) {
                sb.append("  - ").append(comp.getKey()).append(" (权重").append(String.format("%.1f%%", comp.getValue() * 100)).append(")\n");
            }
            sb.append("\n");
        }
        sb.append("注：按《JTG 5120-2021》规范，各部分权重为:\n");
        sb.append(String.format("桥面系 %.0f%% + 上部结构 %.0f%% + 下部结构 %.0f%% + 附属设施 %.0f%%",
            BCICalculator.WEIGHT_DECK * 100,
            BCICalculator.WEIGHT_SUPERSTRUCTURE * 100,
            BCICalculator.WEIGHT_SUBSTRUCTURE * 100,
            BCICalculator.WEIGHT_ACCESSORY * 100));

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "检查项目模板", JOptionPane.INFORMATION_MESSAGE);
    }

    private void doExportReport() {
        if (selectedId <= 0) {
            JOptionPane.showMessageDialog(this, "请先选择要导出的检查记录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BridgeRegularCheck check = BridgeRegularCheckService.getInstance().getCheckById(selectedId);
        if (check == null) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("桥梁评定报告_" + check.getCheckNo() + ".html"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.endsWith(".html")) path += ".html";
            if (ReportService.getInstance().generateBridgeReport(check.getBridgeId(), path)) {
                JOptionPane.showMessageDialog(this, "报告生成成功！\n保存路径: " + path);
            } else {
                JOptionPane.showMessageDialog(this, "报告生成失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void doAdd() {
        BridgeRegularCheck c = getFormData();
        if (c == null) return;
        if (BridgeRegularCheckService.getInstance().addCheck(c)) {
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
        BridgeRegularCheck c = getFormData();
        if (c == null) return;
        c.setId(selectedId);
        if (BridgeRegularCheckService.getInstance().updateCheck(c)) {
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
            if (BridgeRegularCheckService.getInstance().deleteCheck(selectedId)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private BridgeRegularCheck getFormData() {
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

        // 日期格式校验
        if (!ValidationUtil.isValidDate(checkDate)) {
            JOptionPane.showMessageDialog(this, "检查日期格式错误，应为 yyyy-MM-dd！", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        String nextCheckDate = nextCheckDateField.getText().trim();
        if (!nextCheckDate.isEmpty() && !ValidationUtil.isValidDate(nextCheckDate)) {
            JOptionPane.showMessageDialog(this, "下次检查日期格式错误，应为 yyyy-MM-dd！", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        BridgeRegularCheck c = new BridgeRegularCheck();
        c.setBridgeId(bridgeId);
        c.setCheckNo(checkNo);
        c.setCheckDate(checkDate);
        c.setChecker(checkerField.getText().trim());
        c.setWeather(weatherField.getText().trim());
        c.setTemperature(temperatureField.getText().trim());
        c.setCheckType((String) checkTypeBox.getSelectedItem());
        try {
            int deck = Integer.parseInt(deckScoreField.getText().trim().isEmpty() ? "0" : deckScoreField.getText().trim());
            int sup = Integer.parseInt(superstructureScoreField.getText().trim().isEmpty() ? "0" : superstructureScoreField.getText().trim());
            int sub = Integer.parseInt(substructureScoreField.getText().trim().isEmpty() ? "0" : substructureScoreField.getText().trim());
            int acc = Integer.parseInt(accessoryScoreField.getText().trim().isEmpty() ? "0" : accessoryScoreField.getText().trim());
            if (!ValidationUtil.isValidScore(deck) || !ValidationUtil.isValidScore(sup)
                    || !ValidationUtil.isValidScore(sub) || !ValidationUtil.isValidScore(acc)) {
                JOptionPane.showMessageDialog(this, "评分必须在0-100之间！", "错误", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            c.setDeckScore(deck);
            c.setSuperstructureScore(sup);
            c.setSubstructureScore(sub);
            c.setAccessoryScore(acc);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "评分必须为数字！", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        String bciText = bciField.getText().trim();
        c.setBci(bciText.isEmpty() ? 0 : Double.parseDouble(bciText));
        c.setTechStatus((String) techStatusBox.getSelectedItem());
        c.setDefectDesc(defectDescArea.getText().trim());
        c.setMaintenanceSuggest(maintenanceSuggestArea.getText().trim());
        c.setLimitationSuggest(limitationSuggestArea.getText().trim());
        c.setCheckConclusion(checkConclusionArea.getText().trim());
        c.setNextCheckDate(nextCheckDate);
        return c;
    }

    private void fillForm(BridgeRegularCheck c) {
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
        checkTypeBox.setSelectedItem(c.getCheckType());
        deckScoreField.setText(String.valueOf(c.getDeckScore()));
        superstructureScoreField.setText(String.valueOf(c.getSuperstructureScore()));
        substructureScoreField.setText(String.valueOf(c.getSubstructureScore()));
        accessoryScoreField.setText(String.valueOf(c.getAccessoryScore()));
        bciField.setText(String.format("%.2f", c.getBci()));
        techStatusBox.setSelectedItem(c.getTechStatus());
        defectDescArea.setText(c.getDefectDesc());
        maintenanceSuggestArea.setText(c.getMaintenanceSuggest());
        limitationSuggestArea.setText(c.getLimitationSuggest());
        checkConclusionArea.setText(c.getCheckConclusion());
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
        checkTypeBox.setSelectedIndex(0);
        deckScoreField.setText("");
        superstructureScoreField.setText("");
        substructureScoreField.setText("");
        accessoryScoreField.setText("");
        bciField.setText("");
        techStatusBox.setSelectedIndex(0);
        defectDescArea.setText("");
        maintenanceSuggestArea.setText("");
        limitationSuggestArea.setText("");
        checkConclusionArea.setText("");
        nextCheckDateField.setText("");
    }
}
