package ui;

import entity.Bridge;
import entity.BridgeRegularCheck;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import service.BridgeRegularCheckService;
import service.BridgeService;
import ui.common.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * BCI 历史趋势分析图表面板
 * 展示同一桥梁多次定期检查的 BCI 变化曲线
 */
public class BCITrendChartPanel extends JPanel implements RefreshablePanel {

    private JComboBox<String> bridgeCombo;
    private JPanel chartContainer;
    private LoadingOverlay loadingOverlay;
    private boolean dataLoaded = false;

    public BCITrendChartPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(ThemeColors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        initToolbar();
        initChartArea();

        loadingOverlay = new LoadingOverlay();
        add(loadingOverlay, 0);
    }

    private void initToolbar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);

        bridgeCombo = new JComboBox<>();
        bridgeCombo.setPreferredSize(new Dimension(220, 28));
        bridgeCombo.addActionListener(e -> drawChart());

        RoundedButton refreshBtn = new RoundedButton("刷新", ThemeColors.PRIMARY);
        refreshBtn.addActionListener(e -> refreshBtn.doClick());

        panel.add(new JLabel("选择桥梁:"));
        panel.add(bridgeCombo);
        panel.add(refreshBtn);

        add(panel, BorderLayout.NORTH);
    }

    private void initChartArea() {
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel placeholder = new JLabel("请选择桥梁查看BCI历史趋势", SwingConstants.CENTER);
        placeholder.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        placeholder.setForeground(ThemeColors.TEXT_SECONDARY);
        chartContainer.add(placeholder, BorderLayout.CENTER);

        add(chartContainer, BorderLayout.CENTER);
    }

    @Override
    public void refreshDataIfVisible() {
        if (dataLoaded) return;
        dataLoaded = true;
        setBusy(true);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                loadBridgeCombo();
                return null;
            }
            @Override
            protected void done() {
                setBusy(false);
                if (bridgeCombo.getItemCount() > 0) {
                    drawChart();
                }
            }
        }.execute();
    }

    @Override
    public void setBusy(boolean busy) {
        if (busy) loadingOverlay.showOverlay();
        else loadingOverlay.hideOverlay();
    }

    private void loadBridgeCombo() {
        SwingUtilities.invokeLater(() -> {
            bridgeCombo.removeAllItems();
            List<Bridge> bridges = BridgeService.getInstance().getAllBridges();
            for (Bridge b : bridges) {
                bridgeCombo.addItem(b.getId() + "-" + b.getBridgeName());
            }
        });
    }

    private void drawChart() {
        Object selected = bridgeCombo.getSelectedItem();
        if (selected == null) return;

        int bridgeId = Integer.parseInt(selected.toString().split("-")[0]);
        List<BridgeRegularCheck> checks = BridgeRegularCheckService.getInstance().getChecksByBridgeId(bridgeId);

        chartContainer.removeAll();

        if (checks == null || checks.size() < 2) {
            JLabel label = new JLabel("该桥梁定期检查记录不足2条，无法生成趋势图", SwingConstants.CENTER);
            label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            label.setForeground(ThemeColors.TEXT_SECONDARY);
            chartContainer.add(label, BorderLayout.CENTER);
            chartContainer.revalidate();
            chartContainer.repaint();
            return;
        }

        // 按检查日期排序（从旧到新）
        checks.sort((a, b) -> a.getCheckDate().compareTo(b.getCheckDate()));

        XYSeries series = new XYSeries("BCI指数");
        XYSeries seriesDeck = new XYSeries("桥面系");
        XYSeries seriesSuper = new XYSeries("上部结构");
        XYSeries seriesSub = new XYSeries("下部结构");
        XYSeries seriesAccessory = new XYSeries("附属设施");

        for (int i = 0; i < checks.size(); i++) {
            BridgeRegularCheck c = checks.get(i);
            series.add(i + 1, c.getBci());
            seriesDeck.add(i + 1, c.getDeckScore());
            seriesSuper.add(i + 1, c.getSuperstructureScore());
            seriesSub.add(i + 1, c.getSubstructureScore());
            seriesAccessory.add(i + 1, c.getAccessoryScore());
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(seriesDeck);
        dataset.addSeries(seriesSuper);
        dataset.addSeries(seriesSub);
        dataset.addSeries(seriesAccessory);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "桥梁 BCI 历史趋势分析",
                "检查次数",
                "评分",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // 美化图表
        chart.setBackgroundPaint(Color.WHITE);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(new Color(250, 250, 250));
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setDefaultShapesVisible(true);
        renderer.setDefaultItemLabelsVisible(false);
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(0, 400));
        chartContainer.add(chartPanel, BorderLayout.CENTER);

        // 添加说明
        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        info.setBackground(new Color(245, 245, 245));
        info.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        BridgeRegularCheck latest = checks.get(checks.size() - 1);
        BridgeRegularCheck earliest = checks.get(0);
        double change = latest.getBci() - earliest.getBci();
        String trend = change > 0 ? "上升" : (change < 0 ? "下降" : "持平");

        info.setText(String.format(
            "统计说明：\n" +
            "该桥梁共有 %d 次定期检查记录\n" +
            "首次检查 BCI: %.2f，最新检查 BCI: %.2f\n" +
            "总体趋势：%s %.2f\n" +
            "最新技术状况：%s",
            checks.size(), earliest.getBci(), latest.getBci(),
            trend, Math.abs(change), latest.getTechStatus()
        ));

        chartContainer.add(info, BorderLayout.SOUTH);
        chartContainer.revalidate();
        chartContainer.repaint();
    }
}
