package service;

import entity.BridgeRegularCheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能养护方案推荐服务
 * 根据 BCI 指数和技术状况等级，推荐养护措施和检查周期
 */
public class MaintenanceRecommendationService {

    private static MaintenanceRecommendationService instance;

    private MaintenanceRecommendationService() {}

    public static synchronized MaintenanceRecommendationService getInstance() {
        if (instance == null) instance = new MaintenanceRecommendationService();
        return instance;
    }

    /**
     * 根据技术状况等级推荐养护方案
     */
    public Recommendation getRecommendation(String techStatus) {
        Recommendation rec = new Recommendation();
        switch (techStatus) {
            case "1类":
                rec.setLevel("日常养护");
                rec.setMeasures("保持正常养护，定期清扫桥面，疏通排水系统，检查伸缩缝工作状态。");
                rec.setIntervalMonths(12);
                rec.setBudgetLevel("低");
                rec.setPriority("低");
                break;
            case "2类":
                rec.setLevel("预防性养护");
                rec.setMeasures("对轻微缺损进行小修保养，如局部裂缝封闭、护栏油漆、伸缩缝清理更换。");
                rec.setIntervalMonths(6);
                rec.setBudgetLevel("中低");
                rec.setPriority("中");
                break;
            case "3类":
                rec.setLevel("修复性养护");
                rec.setMeasures("对明显病害进行中修，包括桥面铺装修补、支座维护、裂缝灌浆、结构加固。");
                rec.setIntervalMonths(3);
                rec.setBudgetLevel("中");
                rec.setPriority("高");
                break;
            case "4类":
                rec.setLevel("结构性修复");
                rec.setMeasures("进行大修或加固改造，必要时限制通行荷载，对关键构件进行检测评估。");
                rec.setIntervalMonths(1);
                rec.setBudgetLevel("高");
                rec.setPriority("紧急");
                break;
            case "5类":
                rec.setLevel("应急处治/改造");
                rec.setMeasures("立即封闭交通或限载通行，进行专项检测，制定改造或拆除重建方案。");
                rec.setIntervalMonths(0);
                rec.setBudgetLevel("很高");
                rec.setPriority("非常紧急");
                break;
            default:
                rec.setLevel("未知");
                rec.setMeasures("暂无推荐方案");
                rec.setIntervalMonths(12);
                rec.setBudgetLevel("未知");
                rec.setPriority("未知");
        }
        return rec;
    }

    /**
     * 根据定期检查记录推荐养护方案
     */
    public Recommendation getRecommendation(BridgeRegularCheck check) {
        if (check == null) return getRecommendation("未知");
        return getRecommendation(check.getTechStatus());
    }

    /**
     * 统计各类技术状况桥梁数量及建议养护投入
     */
    public Map<String, Object> analyzeMaintenancePlan() {
        Map<String, Object> result = new HashMap<>();
        List<BridgeRegularCheck> checks = BridgeRegularCheckService.getInstance().getAllChecks();

        int[] counts = new int[5];
        double[] weights = {1.0, 1.5, 3.0, 6.0, 10.0}; // 养护投入权重
        double totalInvestmentIndex = 0;

        for (BridgeRegularCheck check : checks) {
            String status = check.getTechStatus();
            int index = statusToIndex(status);
            if (index >= 0) {
                counts[index]++;
                totalInvestmentIndex += weights[index];
            }
        }

        result.put("totalChecks", checks.size());
        result.put("statusCounts", counts);
        result.put("investmentIndex", totalInvestmentIndex);
        result.put("avgInvestmentIndex", checks.isEmpty() ? 0 : totalInvestmentIndex / checks.size());
        return result;
    }

    private int statusToIndex(String status) {
        switch (status) {
            case "1类": return 0;
            case "2类": return 1;
            case "3类": return 2;
            case "4类": return 3;
            case "5类": return 4;
            default: return -1;
        }
    }

    public static class Recommendation {
        private String level;
        private String measures;
        private int intervalMonths;
        private String budgetLevel;
        private String priority;

        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }

        public String getMeasures() { return measures; }
        public void setMeasures(String measures) { this.measures = measures; }

        public int getIntervalMonths() { return intervalMonths; }
        public void setIntervalMonths(int intervalMonths) { this.intervalMonths = intervalMonths; }

        public String getBudgetLevel() { return budgetLevel; }
        public void setBudgetLevel(String budgetLevel) { this.budgetLevel = budgetLevel; }

        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }

        public String toHtml() {
            return String.format(
                "<html>" +
                "<h3>养护等级: %s</h3>" +
                "<p><b>推荐措施:</b> %s</p>" +
                "<p><b>建议检查周期:</b> %s</p>" +
                "<p><b>预算等级:</b> %s</p>" +
                "<p><b>优先级:</b> %s</p>" +
                "</html>",
                level, measures,
                intervalMonths == 0 ? "立即处理" : intervalMonths + " 个月",
                budgetLevel, priority
            );
        }
    }
}
