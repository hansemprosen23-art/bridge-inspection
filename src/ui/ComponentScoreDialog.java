package ui;

import entity.BridgeComponentScore;
import service.BridgeRegularCheckService;
import ui.common.CardPanel;
import ui.common.RoundedButton;
import ui.common.ThemeColors;
import util.BCICalculator;
import util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * BCI 部件评分明细录入对话框
 * 按《JTG 5120-2021》规范，对各桥型部件分别评分并自动计算 BCI
 */
public class ComponentScoreDialog extends JDialog {

    private boolean confirmed = false;
    private Result result;

    private final String bridgeType;
    private final Map<String, Map<String, JTextField>> scoreFields = new LinkedHashMap<>();
    private final Map<String, JLabel> partBciFields = new LinkedHashMap<>();
    private JLabel totalBciLabel;
    private JLabel statusLabel;

    public ComponentScoreDialog(Window owner, String bridgeType) {
        super(owner, "BCI部件评分录入", Dialog.ModalityType.APPLICATION_MODAL);
        this.bridgeType = bridgeType;
        initComponents();
        pack();
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension(700, 500));
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        content.setBackground(ThemeColors.BACKGROUND);

        // 顶部说明
        JLabel tipLabel = new JLabel(String.format("<html>当前桥型：<b>%s</b>，请对各部件进行评分（0-100），系统按规范权重自动计算 BCI。</html>", bridgeType));
        tipLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        tipLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        content.add(tipLabel, BorderLayout.NORTH);

        // 中间：各分类评分面板
        JPanel categoriesPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        categoriesPanel.setOpaque(false);

        Map<String, Map<String, Double>> template = BridgeRegularCheckService.getInstance().getCheckTemplate(bridgeType);
        for (Map.Entry<String, Map<String, Double>> entry : template.entrySet()) {
            categoriesPanel.add(createCategoryCard(entry.getKey(), entry.getValue()));
        }

        JScrollPane scrollPane = new JScrollPane(categoriesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        content.add(scrollPane, BorderLayout.CENTER);

        // 底部：BCI 计算结果 + 按钮
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);

        CardPanel resultCard = new CardPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        totalBciLabel = new JLabel("全桥 BCI: --");
        totalBciLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        totalBciLabel.setForeground(ThemeColors.PRIMARY);

        statusLabel = new JLabel("技术状况: --");
        statusLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        statusLabel.setForeground(ThemeColors.SUCCESS);

        resultCard.add(totalBciLabel);
        resultCard.add(statusLabel);
        bottomPanel.add(resultCard, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        RoundedButton calcBtn = new RoundedButton("计算BCI", ThemeColors.INFO);
        RoundedButton okBtn = new RoundedButton("确定", ThemeColors.SUCCESS);
        RoundedButton cancelBtn = new RoundedButton("取消", ThemeColors.TEXT_SECONDARY);

        calcBtn.addActionListener(e -> calculate());
        okBtn.addActionListener(e -> {
            calculate();
            if (result != null) {
                confirmed = true;
                dispose();
            }
        });
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(calcBtn);
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        content.add(bottomPanel, BorderLayout.SOUTH);
        add(content);
    }

    private JPanel createCategoryCard(String category, Map<String, Double> components) {
        CardPanel card = new CardPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER),
                category,
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 13),
                ThemeColors.INFO));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Map<String, JTextField> fields = new LinkedHashMap<>();
        scoreFields.put(category, fields);

        int row = 0;
        for (Map.Entry<String, Double> comp : components.entrySet()) {
            gbc.gridy = row;
            gbc.gridx = 0;
            gbc.weightx = 1;
            JLabel nameLabel = new JLabel(String.format("%s (%.0f%%)", comp.getKey(), comp.getValue() * 100));
            nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            card.add(nameLabel, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0;
            JTextField field = new JTextField("100", 5);
            field.setHorizontalAlignment(JTextField.CENTER);
            field.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            card.add(field, gbc);
            fields.put(comp.getKey(), field);

            row++;
        }

        // 该分类 BCI
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel partLabel = new JLabel("分类 BCI: --");
        partLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        partLabel.setForeground(ThemeColors.PRIMARY);
        partBciFields.put(category, partLabel);
        card.add(partLabel, gbc);

        return card;
    }

    private void calculate() {
        Map<String, Map<String, Double>> template = BridgeRegularCheckService.getInstance().getCheckTemplate(bridgeType);

        Map<String, Double> deckScores = new HashMap<>();
        Map<String, Double> superScores = new HashMap<>();
        Map<String, Double> subScores = new HashMap<>();
        Map<String, Double> accessoryScores = new HashMap<>();

        List<BridgeComponentScore> componentScores = new ArrayList<>();

        for (Map.Entry<String, Map<String, JTextField>> categoryEntry : scoreFields.entrySet()) {
            String category = categoryEntry.getKey();
            Map<String, Double> weights = template.get(category);
            Map<String, Double> scores = new HashMap<>();

            for (Map.Entry<String, JTextField> compEntry : categoryEntry.getValue().entrySet()) {
                String compName = compEntry.getKey();
                String text = compEntry.getValue().getText().trim();
                int score;
                try {
                    score = Integer.parseInt(text.isEmpty() ? "0" : text);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        String.format("[%s-%s] 评分必须为数字！", category, compName),
                        "输入错误", JOptionPane.ERROR_MESSAGE);
                    result = null;
                    return;
                }
                if (!ValidationUtil.isValidScore(score)) {
                    JOptionPane.showMessageDialog(this,
                        String.format("[%s-%s] 评分必须在 0-100 之间！", category, compName),
                        "输入错误", JOptionPane.ERROR_MESSAGE);
                    result = null;
                    return;
                }

                double weight = weights.getOrDefault(compName, 0.0);
                scores.put(compName, (double) score);

                BridgeComponentScore cs = new BridgeComponentScore();
                cs.setCategory(category);
                cs.setComponentName(compName);
                cs.setScore(score);
                cs.setWeight(weight);
                componentScores.add(cs);
            }

            double partBci = BCICalculator.calculatePartBCI(scores, weights);
            partBciFields.get(category).setText(String.format("分类 BCI: %.2f", partBci));

            switch (category) {
                case "桥面系": deckScores = scores; break;
                case "上部结构": superScores = scores; break;
                case "下部结构": subScores = scores; break;
                case "附属设施": accessoryScores = scores; break;
            }
        }

        double bci = BCICalculator.calculateBCI(deckScores, superScores, subScores, accessoryScores, bridgeType);
        String status = BridgeRegularCheckService.getInstance().determineTechStatus(bci);
        String desc = BridgeRegularCheckService.getInstance().getTechStatusDesc(status);

        totalBciLabel.setText(String.format("全桥 BCI: %.2f", bci));
        statusLabel.setText(String.format("技术状况: %s (%s)", status, desc));

        result = new Result(deckScores, superScores, subScores, accessoryScores,
                            componentScores, bci, status,
                            (int) Math.round(BCICalculator.calculatePartBCI(deckScores, template.get("桥面系"))),
                            (int) Math.round(BCICalculator.calculatePartBCI(superScores, template.get("上部结构"))),
                            (int) Math.round(BCICalculator.calculatePartBCI(subScores, template.get("下部结构"))),
                            (int) Math.round(BCICalculator.calculatePartBCI(accessoryScores, template.get("附属设施"))));
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Result getResult() {
        return result;
    }

    /**
     * 评分结果封装
     */
    public static class Result {
        public final Map<String, Double> deckScores;
        public final Map<String, Double> superScores;
        public final Map<String, Double> subScores;
        public final Map<String, Double> accessoryScores;
        public final List<BridgeComponentScore> componentScores;
        public final double bci;
        public final String techStatus;
        public final int deckBci;
        public final int superBci;
        public final int subBci;
        public final int accessoryBci;

        public Result(Map<String, Double> deckScores, Map<String, Double> superScores,
                      Map<String, Double> subScores, Map<String, Double> accessoryScores,
                      List<BridgeComponentScore> componentScores, double bci, String techStatus,
                      int deckBci, int superBci, int subBci, int accessoryBci) {
            this.deckScores = deckScores;
            this.superScores = superScores;
            this.subScores = subScores;
            this.accessoryScores = accessoryScores;
            this.componentScores = componentScores;
            this.bci = bci;
            this.techStatus = techStatus;
            this.deckBci = deckBci;
            this.superBci = superBci;
            this.subBci = subBci;
            this.accessoryBci = accessoryBci;
        }
    }
}
