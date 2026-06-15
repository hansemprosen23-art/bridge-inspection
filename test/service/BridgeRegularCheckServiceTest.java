package service;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 桥梁定期检查记录服务层单元测试
 * 重点验证 BCI 计算代理逻辑
 */
public class BridgeRegularCheckServiceTest {

    @Test
    public void testCalculateBCI_Simple() {
        double bci = BridgeRegularCheckService.getInstance().calculateBCI(90, 90, 90, 90);
        assertEquals(90.0, bci, 0.01);
    }

    @Test
    public void testCalculateBCI_Weighted() {
        // 桥面系100, 上部80, 下部80, 附属100
        // BCI = 100*0.15 + 80*0.35 + 80*0.35 + 100*0.15 = 86
        double bci = BridgeRegularCheckService.getInstance().calculateBCI(100, 80, 80, 100);
        assertEquals(86.0, bci, 0.01);
    }

    @Test
    public void testDetermineTechStatus() {
        assertEquals("1类", BridgeRegularCheckService.getInstance().determineTechStatus(95));
        assertEquals("2类", BridgeRegularCheckService.getInstance().determineTechStatus(85));
        assertEquals("3类", BridgeRegularCheckService.getInstance().determineTechStatus(70));
        assertEquals("4类", BridgeRegularCheckService.getInstance().determineTechStatus(50));
        assertEquals("5类", BridgeRegularCheckService.getInstance().determineTechStatus(30));
    }

    @Test
    public void testGetTechStatusDesc() {
        assertEquals("完好/良好状态", BridgeRegularCheckService.getInstance().getTechStatusDesc("1类"));
        assertEquals("危险状态", BridgeRegularCheckService.getInstance().getTechStatusDesc("5类"));
    }

    @Test
    public void testCalculateBCIFromComponents() {
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

        double bci = BridgeRegularCheckService.getInstance()
                .calculateBCIFromComponents(deckScores, superScores, subScores, accessoryScores, "梁式桥");

        assertTrue("BCI 应在 0-100 之间", bci >= 0 && bci <= 100);
        assertEquals("2类", BridgeRegularCheckService.getInstance().determineTechStatus(bci));
    }

    @Test
    public void testCheckTemplateByBridgeType() {
        Map<String, Map<String, Double>> beam = BridgeRegularCheckService.getInstance().getCheckTemplate("梁式桥");
        assertNotNull(beam);
        assertTrue(beam.containsKey("桥面系"));
        assertTrue(beam.get("上部结构").containsKey("主梁"));

        Map<String, Map<String, Double>> arch = BridgeRegularCheckService.getInstance().getCheckTemplate("拱桥");
        assertTrue(arch.get("上部结构").containsKey("主拱圈"));

        Map<String, Map<String, Double>> cable = BridgeRegularCheckService.getInstance().getCheckTemplate("斜拉桥");
        assertTrue(cable.get("上部结构").containsKey("拉索"));
    }
}
