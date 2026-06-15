package util;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

/**
 * BCI计算器单元测试
 * 按 JTG 5120-2021 规范验证
 */
public class BCICalculatorTest {

    @Test
    public void testCalculateBCI_Simple() {
        // 四部分评分均为 90
        double bci = BCICalculator.calculateBCI(90, 90, 90, 90);
        assertEquals(90.0, bci, 0.01);
    }

    @Test
    public void testCalculateBCI_Weighted() {
        // 桥面系100, 上部80, 下部80, 附属100
        // BCI = 100*0.15 + 80*0.35 + 80*0.35 + 100*0.15 = 15 + 28 + 28 + 15 = 86
        double bci = BCICalculator.calculateBCI(100, 80, 80, 100);
        assertEquals(86.0, bci, 0.01);
    }

    @Test
    public void testDetermineTechStatus() {
        assertEquals("1类", BCICalculator.determineTechStatus(95));
        assertEquals("1类", BCICalculator.determineTechStatus(90));
        assertEquals("2类", BCICalculator.determineTechStatus(85));
        assertEquals("2类", BCICalculator.determineTechStatus(80));
        assertEquals("3类", BCICalculator.determineTechStatus(70));
        assertEquals("3类", BCICalculator.determineTechStatus(60));
        assertEquals("4类", BCICalculator.determineTechStatus(50));
        assertEquals("4类", BCICalculator.determineTechStatus(40));
        assertEquals("5类", BCICalculator.determineTechStatus(30));
        assertEquals("5类", BCICalculator.determineTechStatus(0));
    }

    @Test
    public void testCalculatePartBCI() {
        Map<String, Double> scores = new HashMap<>();
        scores.put("桥面铺装", 85.0);
        scores.put("伸缩缝装置", 90.0);
        scores.put("排水系统", 80.0);
        scores.put("护栏/防撞墙", 95.0);
        scores.put("人行道/检修道", 88.0);
        scores.put("栏杆", 92.0);

        double partBCI = BCICalculator.calculatePartBCI(scores, BCICalculator.DECK_COMPONENTS);
        assertTrue(partBCI > 0);
        assertTrue(partBCI <= 100);
    }

    @Test
    public void testGetSuperstructureWeights() {
        // 梁式桥
        Map<String, Double> beam = BCICalculator.getSuperstructureWeights("梁式桥");
        assertTrue(beam.containsKey("主梁"));

        // 拱桥
        Map<String, Double> arch = BCICalculator.getSuperstructureWeights("拱桥");
        assertTrue(arch.containsKey("主拱圈"));

        // 斜拉桥
        Map<String, Double> cable = BCICalculator.getSuperstructureWeights("斜拉桥");
        assertTrue(cable.containsKey("拉索"));

        // 悬索桥
        Map<String, Double> suspension = BCICalculator.getSuperstructureWeights("悬索桥");
        assertTrue(suspension.containsKey("主缆"));
    }

    @Test
    public void testGetAllComponents() {
        Map<String, Map<String, Double>> all = BCICalculator.getAllComponents("梁式桥");
        assertEquals(4, all.size());
        assertTrue(all.containsKey("桥面系"));
        assertTrue(all.containsKey("上部结构"));
        assertTrue(all.containsKey("下部结构"));
        assertTrue(all.containsKey("附属设施"));
    }

    @Test
    public void testFullBCICalculation() {
        Map<String, Double> deckScores = new HashMap<>();
        deckScores.put("桥面铺装", 90.0);
        deckScores.put("伸缩缝装置", 85.0);
        deckScores.put("排水系统", 88.0);
        deckScores.put("护栏/防撞墙", 92.0);
        deckScores.put("人行道/检修道", 90.0);
        deckScores.put("栏杆", 91.0);

        Map<String, Double> superScores = new HashMap<>();
        superScores.put("主梁", 85.0);
        superScores.put("支座", 88.0);
        superScores.put("横向联系", 90.0);
        superScores.put("桥面连续构造", 87.0);
        superScores.put("梁端连接", 89.0);

        Map<String, Double> subScores = new HashMap<>();
        subScores.put("桥墩", 86.0);
        subScores.put("桥台", 88.0);
        subScores.put("基础", 90.0);
        subScores.put("翼墙/耳墙", 85.0);

        Map<String, Double> accessoryScores = new HashMap<>();
        accessoryScores.put("照明系统", 92.0);
        accessoryScores.put("标志标线", 90.0);
        accessoryScores.put("排水设施", 88.0);
        accessoryScores.put("隔音屏", 85.0);
        accessoryScores.put("检修设施", 90.0);
        accessoryScores.put("防撞设施", 87.0);
        accessoryScores.put("其他附属", 89.0);

        double bci = BCICalculator.calculateBCI(deckScores, superScores, subScores, accessoryScores, "梁式桥");
        assertTrue(bci >= 0 && bci <= 100);
        assertEquals("2类", BCICalculator.determineTechStatus(bci));
    }
}
