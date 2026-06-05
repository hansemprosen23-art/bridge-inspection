package service;

import entity.Bridge;
import entity.BridgeInitialCheck;
import entity.BridgeRegularCheck;
import util.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 报告生成服务
 * 支持导出 Word 格式报告（使用 HTML 方式生成）
 * 支持导出 Excel 格式报表（使用 CSV 方式生成）
 */
public class ReportService {

    private static ReportService instance;

    private ReportService() {}

    public static synchronized ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }

    /**
     * 生成桥梁技术状况评定报告（Word格式，使用HTML）
     */
    public boolean generateBridgeReport(int bridgeId, String outputPath) {
        try {
            Bridge bridge = BridgeService.getInstance().getBridgeById(bridgeId);
            if (bridge == null) {
                Logger.error("生成报告失败：桥梁不存在 ID=" + bridgeId);
                return false;
            }

            List<BridgeRegularCheck> checks = BridgeRegularCheckService.getInstance().getChecksByBridgeId(bridgeId);
            BridgeRegularCheck latestCheck = checks.isEmpty() ? null : checks.get(0);

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n<html>\n<head>\n");
            html.append("<meta charset=\"UTF-8\">\n");
            html.append("<title>桥梁技术状况评定报告</title>\n");
            html.append("<style>\n");
            html.append("body { font-family: '宋体', SimSun, serif; font-size: 14px; line-height: 1.8; margin: 40px; }\n");
            html.append("h1 { text-align: center; font-size: 22px; font-weight: bold; margin-bottom: 30px; }\n");
            html.append("h2 { font-size: 16px; font-weight: bold; margin-top: 25px; margin-bottom: 10px; border-bottom: 1px solid #333; padding-bottom: 5px; }\n");
            html.append("table { width: 100%; border-collapse: collapse; margin: 15px 0; }\n");
            html.append("th, td { border: 1px solid #333; padding: 8px 12px; text-align: left; }\n");
            html.append("th { background-color: #f0f0f0; font-weight: bold; width: 20%; }\n");
            html.append(".status-1 { color: #2e7d32; font-weight: bold; }\n");
            html.append(".status-2 { color: #689f38; font-weight: bold; }\n");
            html.append(".status-3 { color: #f9a825; font-weight: bold; }\n");
            html.append(".status-4 { color: #ef6c00; font-weight: bold; }\n");
            html.append(".status-5 { color: #c62828; font-weight: bold; }\n");
            html.append(".footer { margin-top: 50px; text-align: right; font-size: 12px; color: #666; }\n");
            html.append("</style>\n</head>\n<body>\n");

            // 标题
            html.append("<h1>公路桥梁技术状况评定报告</h1>\n");

            // 基本信息
            html.append("<h2>一、桥梁基本信息</h2>\n");
            html.append("<table>\n");
            html.append("<tr><th>桥梁编号</th><td>").append(escapeHtml(bridge.getBridgeNo())).append("</td><th>桥梁名称</th><td>").append(escapeHtml(bridge.getBridgeName())).append("</td></tr>\n");
            html.append("<tr><th>路线名称</th><td>").append(escapeHtml(bridge.getRouteName())).append("</td><th>路线等级</th><td>").append(escapeHtml(bridge.getRouteGrade())).append("</td></tr>\n");
            html.append("<tr><th>桥梁类型</th><td>").append(escapeHtml(bridge.getBridgeType())).append("</td><th>结构类型</th><td>").append(escapeHtml(bridge.getStructureType())).append("</td></tr>\n");
            html.append("<tr><th>跨径组合</th><td>").append(escapeHtml(bridge.getSpanCombination())).append("</td><th>全长(m)</th><td>").append(bridge.getTotalLength()).append("</td></tr>\n");
            html.append("<tr><th>总宽(m)</th><td>").append(bridge.getTotalWidth()).append("</td><th>设计荷载</th><td>").append(escapeHtml(bridge.getDesignLoad())).append("</td></tr>\n");
            html.append("<tr><th>管理单位</th><td>").append(escapeHtml(bridge.getManageUnit())).append("</td><th>养护单位</th><td>").append(escapeHtml(bridge.getMaintainUnit())).append("</td></tr>\n");
            html.append("<tr><th>竣工日期</th><td>").append(escapeHtml(bridge.getCompleteDate())).append("</td><th>检查等级</th><td>").append(escapeHtml(bridge.getCheckLevel())).append("</td></tr>\n");
            html.append("</table>\n");

            // 定期检查记录
            html.append("<h2>二、定期检查记录</h2>\n");
            if (latestCheck != null) {
                html.append("<table>\n");
                html.append("<tr><th>检查编号</th><td>").append(escapeHtml(latestCheck.getCheckNo())).append("</td><th>检查日期</th><td>").append(escapeHtml(latestCheck.getCheckDate())).append("</td></tr>\n");
                html.append("<tr><th>检查人</th><td>").append(escapeHtml(latestCheck.getChecker())).append("</td><th>天气</th><td>").append(escapeHtml(latestCheck.getWeather())).append("</td></tr>\n");
                html.append("<tr><th>桥面系评分</th><td>").append(latestCheck.getDeckScore()).append("</td><th>上部结构评分</th><td>").append(latestCheck.getSuperstructureScore()).append("</td></tr>\n");
                html.append("<tr><th>下部结构评分</th><td>").append(latestCheck.getSubstructureScore()).append("</td><th>附属设施评分</th><td>").append(latestCheck.getAccessoryScore()).append("</td></tr>\n");

                String statusClass = getStatusClass(latestCheck.getTechStatus());
                html.append("<tr><th>BCI指数</th><td><strong>").append(String.format("%.2f", latestCheck.getBci())).append("</strong></td>");
                html.append("<th>技术状况等级</th><td class=\"").append(statusClass).append("\">").append(escapeHtml(latestCheck.getTechStatus()));
                html.append(" (").append(BridgeRegularCheckService.getInstance().getTechStatusDesc(latestCheck.getTechStatus())).append(")");
                html.append("</td></tr>\n");
                html.append("</table>\n");

                // 缺损描述
                html.append("<h2>三、主要缺损描述</h2>\n");
                html.append("<p>").append(escapeHtml(latestCheck.getDefectDesc())).append("</p>\n");

                // 养护建议
                html.append("<h2>四、养护建议</h2>\n");
                html.append("<p>").append(escapeHtml(latestCheck.getMaintenanceSuggest())).append("</p>\n");

                // 检查结论
                html.append("<h2>五、检查结论</h2>\n");
                html.append("<p>").append(escapeHtml(latestCheck.getCheckConclusion())).append("</p>\n");
            } else {
                html.append("<p>暂无定期检查记录</p>\n");
            }

            // BCI 计算说明
            html.append("<h2>六、BCI计算方法说明</h2>\n");
            html.append("<p>本系统按照《JTG 5120-2021 公路桥涵养护规范》进行BCI计算：</p>\n");
            html.append("<p>BCI = BCI₁×0.15 + BCI₂×0.35 + BCI₃×0.35 + BCI₄×0.15</p>\n");
            html.append("<p>其中：BCI₁为桥面系评分，BCI₂为上部结构评分，BCI₃为下部结构评分，BCI₄为附属设施评分。</p>\n");
            html.append("<p>技术状况等级划分：1类(≥90)、2类(≥80)、3类(≥60)、4类(≥40)、5类(&lt;40)</p>\n");

            // 页脚
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            html.append("<div class=\"footer\">\n");
            html.append("<p>报告生成时间：").append(sdf.format(new Date())).append("</p>\n");
            html.append("<p>公路桥梁初始检查信息系统自动生成</p>\n");
            html.append("</div>\n");

            html.append("</body>\n</html>");

            // 写入文件
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputPath), "UTF-8"))) {
                writer.write(html.toString());
            }

            Logger.info("报告生成成功: " + outputPath);
            return true;

        } catch (Exception e) {
            Logger.error("生成报告失败", e);
            return false;
        }
    }

    /**
     * 导出定期检查记录为 CSV（Excel兼容）
     */
    public boolean exportRegularChecksToCSV(String outputPath) {
        try {
            List<BridgeRegularCheck> checks = BridgeRegularCheckService.getInstance().getAllChecks();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputPath), "GBK"))) { // Excel中文兼容

                // CSV 表头
                writer.write("检查编号,桥梁名称,检查日期,检查人,天气,温度,检查类型,");
                writer.write("桥面系评分,上部结构评分,下部结构评分,附属设施评分,BCI,技术状况,");
                writer.write("缺损描述,养护建议,限制使用建议,检查结论,下次检查日期");
                writer.newLine();

                // 数据行
                for (BridgeRegularCheck c : checks) {
                    writer.write(escapeCsv(c.getCheckNo()) + ",");
                    writer.write(escapeCsv(c.getBridgeName()) + ",");
                    writer.write(escapeCsv(c.getCheckDate()) + ",");
                    writer.write(escapeCsv(c.getChecker()) + ",");
                    writer.write(escapeCsv(c.getWeather()) + ",");
                    writer.write(escapeCsv(c.getTemperature()) + ",");
                    writer.write(escapeCsv(c.getCheckType()) + ",");
                    writer.write(c.getDeckScore() + ",");
                    writer.write(c.getSuperstructureScore() + ",");
                    writer.write(c.getSubstructureScore() + ",");
                    writer.write(c.getAccessoryScore() + ",");
                    writer.write(String.format("%.2f", c.getBci()) + ",");
                    writer.write(escapeCsv(c.getTechStatus()) + ",");
                    writer.write(escapeCsv(c.getDefectDesc()) + ",");
                    writer.write(escapeCsv(c.getMaintenanceSuggest()) + ",");
                    writer.write(escapeCsv(c.getLimitationSuggest()) + ",");
                    writer.write(escapeCsv(c.getCheckConclusion()) + ",");
                    writer.write(escapeCsv(c.getNextCheckDate()));
                    writer.newLine();
                }
            }

            Logger.info("CSV导出成功: " + outputPath);
            return true;

        } catch (Exception e) {
            Logger.error("CSV导出失败", e);
            return false;
        }
    }

    /**
     * 导出桥梁列表为 CSV
     */
    public boolean exportBridgesToCSV(String outputPath) {
        try {
            List<Bridge> bridges = BridgeService.getInstance().getAllBridges();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputPath), "GBK"))) {

                writer.write("桥梁编号,桥梁名称,路线名称,路线等级,桥梁类型,结构类型,");
                writer.write("跨径组合,全长(m),总宽(m),设计荷载,检查等级,技术状况,");
                writer.write("管理单位,养护单位,竣工日期,经度,纬度");
                writer.newLine();

                for (Bridge b : bridges) {
                    writer.write(escapeCsv(b.getBridgeNo()) + ",");
                    writer.write(escapeCsv(b.getBridgeName()) + ",");
                    writer.write(escapeCsv(b.getRouteName()) + ",");
                    writer.write(escapeCsv(b.getRouteGrade()) + ",");
                    writer.write(escapeCsv(b.getBridgeType()) + ",");
                    writer.write(escapeCsv(b.getStructureType()) + ",");
                    writer.write(escapeCsv(b.getSpanCombination()) + ",");
                    writer.write(b.getTotalLength() + ",");
                    writer.write(b.getTotalWidth() + ",");
                    writer.write(escapeCsv(b.getDesignLoad()) + ",");
                    writer.write(escapeCsv(b.getCheckLevel()) + ",");
                    writer.write(b.getTechStatus() + ",");
                    writer.write(escapeCsv(b.getManageUnit()) + ",");
                    writer.write(escapeCsv(b.getMaintainUnit()) + ",");
                    writer.write(escapeCsv(b.getCompleteDate()) + ",");
                    writer.write(escapeCsv(b.getLongitude()) + ",");
                    writer.write(escapeCsv(b.getLatitude()));
                    writer.newLine();
                }
            }

            Logger.info("桥梁CSV导出成功: " + outputPath);
            return true;

        } catch (Exception e) {
            Logger.error("桥梁CSV导出失败", e);
            return false;
        }
    }

    /**
     * 导出 JTable 为 CSV
     */
    public boolean exportTableToCSV(JTable table, String outputPath) {
        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            int rowCount = model.getRowCount();
            int colCount = model.getColumnCount();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputPath), "GBK"))) {

                // 表头
                for (int c = 0; c < colCount; c++) {
                    writer.write(escapeCsv(model.getColumnName(c)));
                    if (c < colCount - 1) writer.write(",");
                }
                writer.newLine();

                // 数据
                for (int r = 0; r < rowCount; r++) {
                    for (int c = 0; c < colCount; c++) {
                        Object val = model.getValueAt(r, c);
                        writer.write(escapeCsv(val != null ? val.toString() : ""));
                        if (c < colCount - 1) writer.write(",");
                    }
                    writer.newLine();
                }
            }

            Logger.info("表格导出成功: " + outputPath);
            return true;

        } catch (Exception e) {
            Logger.error("表格导出失败", e);
            return false;
        }
    }

    // ========== 辅助方法 ==========

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }

    private String escapeCsv(String text) {
        if (text == null) return "";
        text = text.replace("\"", "\"\"");
        if (text.contains(",") || text.contains("\n") || text.contains("\"")) {
            text = "\"" + text + "\"";
        }
        return text;
    }

    private String getStatusClass(String status) {
        switch (status) {
            case "1类": return "status-1";
            case "2类": return "status-2";
            case "3类": return "status-3";
            case "4类": return "status-4";
            case "5类": return "status-5";
            default: return "";
        }
    }
}
