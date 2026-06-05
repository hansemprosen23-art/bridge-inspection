package util;

import java.util.HashMap;
import java.util.Map;

/**
 * BCI (Bridge Condition Index) 计算器
 * 按照《JTG 5120-2021 公路桥涵养护规范》实现
 *
 * 规范要点：
 * 1. 全桥 BCI = BCI₁×ω₁ + BCI₂×ω₂ + BCI₃×ω₃ + BCI₄×ω₄
 *    - BCI₁: 桥面系评分, ω₁=0.15
 *    - BCI₂: 上部结构评分, ω₂=0.35
 *    - BCI₃: 下部结构评分, ω₃=0.35
 *    - BCI₄: 附属设施评分, ω₄=0.15
 *
 * 2. 各部分 BCI = Σ(各部件得分 × 部件权重)
 *    部件得分采用 100 分制，根据缺损情况扣分
 *
 * 3. 技术状况等级：
 *    1类: BCI ≥ 90  (完好/良好)
 *    2类: 80 ≤ BCI < 90  (较好)
 *    3类: 60 ≤ BCI < 80  (较差)
 *    4类: 40 ≤ BCI < 60  (差)
 *    5类: BCI < 40  (危险)
 */
public class BCICalculator {

    // 全桥各部分权重（规范推荐值）
    public static final double WEIGHT_DECK = 0.15;
    public static final double WEIGHT_SUPERSTRUCTURE = 0.35;
    public static final double WEIGHT_SUBSTRUCTURE = 0.35;
    public static final double WEIGHT_ACCESSORY = 0.15;

    // ====== 桥面系部件及权重 ======
    public static final Map<String, Double> DECK_COMPONENTS = new HashMap<>();
    static {
        DECK_COMPONENTS.put("桥面铺装", 0.30);
        DECK_COMPONENTS.put("伸缩缝装置", 0.20);
        DECK_COMPONENTS.put("排水系统", 0.15);
        DECK_COMPONENTS.put("护栏/防撞墙", 0.15);
        DECK_COMPONENTS.put("人行道/检修道", 0.10);
        DECK_COMPONENTS.put("栏杆", 0.10);
    }

    // ====== 上部结构部件及权重（梁式桥） ======
    public static final Map<String, Double> SUPERSTRUCTURE_BEAM = new HashMap<>();
    static {
        SUPERSTRUCTURE_BEAM.put("主梁", 0.40);
        SUPERSTRUCTURE_BEAM.put("支座", 0.20);
        SUPERSTRUCTURE_BEAM.put("横向联系", 0.15);
        SUPERSTRUCTURE_BEAM.put("桥面连续构造", 0.10);
        SUPERSTRUCTURE_BEAM.put("梁端连接", 0.15);
    }

    // ====== 上部结构部件及权重（拱桥） ======
    public static final Map<String, Double> SUPERSTRUCTURE_ARCH = new HashMap<>();
    static {
        SUPERSTRUCTURE_ARCH.put("主拱圈", 0.45);
        SUPERSTRUCTURE_ARCH.put("拱上建筑", 0.20);
        SUPERSTRUCTURE_ARCH.put("横向联系", 0.15);
        SUPERSTRUCTURE_ARCH.put("支座", 0.10);
        SUPERSTRUCTURE_ARCH.put("桥面连续构造", 0.10);
    }

    // ====== 上部结构部件及权重（斜拉桥） ======
    public static final Map<String, Double> SUPERSTRUCTURE_CABLE = new HashMap<>();
    static {
        SUPERSTRUCTURE_CABLE.put("主梁", 0.30);
        SUPERSTRUCTURE_CABLE.put("索塔", 0.25);
        SUPERSTRUCTURE_CABLE.put("拉索", 0.25);
        SUPERSTRUCTURE_CABLE.put("锚固系统", 0.10);
        SUPERSTRUCTURE_CABLE.put("支座", 0.10);
    }

    // ====== 上部结构部件及权重（悬索桥） ======
    public static final Map<String, Double> SUPERSTRUCTURE_SUSPENSION = new HashMap<>();
    static {
        SUPERSTRUCTURE_SUSPENSION.put("主梁", 0.25);
        SUPERSTRUCTURE_SUSPENSION.put("主缆", 0.25);
        SUPERSTRUCTURE_SUSPENSION.put("吊索", 0.15);
        SUPERSTRUCTURE_SUSPENSION.put("索塔", 0.15);
        SUPERSTRUCTURE_SUSPENSION.put("锚碇", 0.10);
        SUPERSTRUCTURE_SUSPENSION.put("支座", 0.10);
    }

    // ====== 下部结构部件及权重 ======
    public static final Map<String, Double> SUBSTRUCTURE_COMPONENTS = new HashMap<>();
    static {
        SUBSTRUCTURE_COMPONENTS.put("桥墩", 0.35);
        SUBSTRUCTURE_COMPONENTS.put("桥台", 0.30);
        SUBSTRUCTURE_COMPONENTS.put("基础", 0.25);
        SUBSTRUCTURE_COMPONENTS.put("翼墙/耳墙", 0.10);
    }

    // ====== 附属设施部件及权重 ======
    public static final Map<String, Double> ACCESSORY_COMPONENTS = new HashMap<>();
    static {
        ACCESSORY_COMPONENTS.put("照明系统", 0.20);
        ACCESSORY_COMPONENTS.put("标志标线", 0.20);
        ACCESSORY_COMPONENTS.put("排水设施", 0.15);
        ACCESSORY_COMPONENTS.put("隔音屏", 0.10);
        ACCESSORY_COMPONENTS.put("检修设施", 0.15);
        ACCESSORY_COMPONENTS.put("防撞设施", 0.10);
        ACCESSORY_COMPONENTS.put("其他附属", 0.10);
    }

    /**
     * 计算某一部分的 BCI（加权平均）
     * @param scores 各部件得分 Map<部件名, 得分>
     * @param weights 各部件权重 Map<部件名, 权重>
     * @return 该部分 BCI 得分
     */
    public static double calculatePartBCI(Map<String, Double> scores, Map<String, Double> weights) {
        if (scores == null || scores.isEmpty()) return 0;
        double totalScore = 0;
        double totalWeight = 0;
        for (Map.Entry<String, Double> entry : weights.entrySet()) {
            String component = entry.getKey();
            double weight = entry.getValue();
            double score = scores.getOrDefault(component, 0.0);
            totalScore += score * weight;
            totalWeight += weight;
        }
        return totalWeight > 0 ? totalScore / totalWeight : 0;
    }

    /**
     * 计算全桥 BCI（简化版 - 使用四部分总分直接计算）
     * 适用于已有桥面系/上部/下部/附属总体评分的情况
     */
    public static double calculateBCI(double deckScore, double superstructureScore,
                                       double substructureScore, double accessoryScore) {
        return deckScore * WEIGHT_DECK
                + superstructureScore * WEIGHT_SUPERSTRUCTURE
                + substructureScore * WEIGHT_SUBSTRUCTURE
                + accessoryScore * WEIGHT_ACCESSORY;
    }

    /**
     * 计算全桥 BCI（完整版 - 使用各部件详细评分）
     */
    public static double calculateBCI(Map<String, Double> deckScores,
                                       Map<String, Double> superScores,
                                       Map<String, Double> subScores,
                                       Map<String, Double> accessoryScores,
                                       String bridgeType) {
        double bci1 = calculatePartBCI(deckScores, DECK_COMPONENTS);
        double bci2 = calculatePartBCI(superScores, getSuperstructureWeights(bridgeType));
        double bci3 = calculatePartBCI(subScores, SUBSTRUCTURE_COMPONENTS);
        double bci4 = calculatePartBCI(accessoryScores, ACCESSORY_COMPONENTS);
        return calculateBCI(bci1, bci2, bci3, bci4);
    }

    /**
     * 根据 BCI 确定技术状况等级
     */
    public static String determineTechStatus(double bci) {
        if (bci >= 90) return "1类";
        if (bci >= 80) return "2类";
        if (bci >= 60) return "3类";
        if (bci >= 40) return "4类";
        return "5类";
    }

    /**
     * 获取技术状况等级描述
     */
    public static String getTechStatusDesc(String status) {
        switch (status) {
            case "1类": return "完好/良好状态";
            case "2类": return "较好状态";
            case "3类": return "较差状态";
            case "4类": return "差的状态";
            case "5类": return "危险状态";
            default: return "未知";
        }
    }

    /**
     * 根据桥型获取上部结构权重
     */
    public static Map<String, Double> getSuperstructureWeights(String bridgeType) {
        if (bridgeType == null) return SUPERSTRUCTURE_BEAM;
        String type = bridgeType.trim();
        if (type.contains("拱")) return SUPERSTRUCTURE_ARCH;
        if (type.contains("斜拉")) return SUPERSTRUCTURE_CABLE;
        if (type.contains("悬索")) return SUPERSTRUCTURE_SUSPENSION;
        return SUPERSTRUCTURE_BEAM; // 默认梁式桥
    }

    /**
     * 获取某桥型的所有检查部件列表
     */
    public static Map<String, Map<String, Double>> getAllComponents(String bridgeType) {
        Map<String, Map<String, Double>> result = new HashMap<>();
        result.put("桥面系", DECK_COMPONENTS);
        result.put("上部结构", getSuperstructureWeights(bridgeType));
        result.put("下部结构", SUBSTRUCTURE_COMPONENTS);
        result.put("附属设施", ACCESSORY_COMPONENTS);
        return result;
    }
}
