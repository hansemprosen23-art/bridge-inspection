package util;

import entity.Bridge;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV桥梁数据导入工具类
 * 用于从CSV文件中读取桥梁数据并转换为Bridge实体对象列表
 *
 * CSV文件格式要求：
 * 1. 第一行为表头（会被自动跳过）
 * 2. 字段顺序与Bridge实体类一致
 * 3. 编码：UTF-8（兼容Excel导出的带BOM格式）
 * 4. 数值类型字段为空时自动填充默认值0
 */
public class CsvBridgeImporter {

    /**
     * 从CSV文件导入桥梁数据
     *
     * @param filePath CSV文件路径
     * @return Bridge对象列表
     */
    public static List<Bridge> importFromCSV(String filePath) {
        List<Bridge> bridges = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            Logger.error("CSV文件不存在: " + filePath);
            return bridges;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            // 跳过UTF-8 BOM（如果存在）
            reader.mark(3);
            char[] bom = new char[3];
            int read = reader.read(bom);
            if (read < 3 || bom[0] != '\uFEFF') {
                reader.reset(); // 没有BOM，回退
            }

            String line;
            boolean isFirstLine = true;
            int lineNum = 0;

            while ((line = reader.readLine()) != null) {
                lineNum++;

                // 跳过空行
                if (line.trim().isEmpty()) continue;

                // 跳过表头行
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                try {
                    Bridge bridge = parseLine(line);
                    if (bridge != null) {
                        bridges.add(bridge);
                    }
                } catch (Exception e) {
                    Logger.error("解析第" + lineNum + "行数据失败: " + line, e);
                }
            }

            Logger.info("CSV导入完成，共导入 " + bridges.size() + " 条记录");

        } catch (IOException e) {
            Logger.error("读取CSV文件失败: " + filePath, e);
        }

        return bridges;
    }

    /**
     * 解析单行CSV数据
     *
     * @param line CSV行数据
     * @return Bridge对象
     */
    private static Bridge parseLine(String line) {
        List<String> fields = splitCsvLine(line);
        if (fields.size() < 25) {
            Logger.warn("CSV字段数量不足，跳过该行");
            return null;
        }

        Bridge b = new Bridge();

        // id: 字段0 - 如果为空则设为0（数据库自增）
        b.setId(parseInt(getField(fields, 0), 0));

        // 基本字符串字段
        b.setBridgeNo(getField(fields, 1));
        b.setBridgeName(getField(fields, 2));
        b.setRouteName(getField(fields, 3));
        b.setRouteGrade(getField(fields, 4));
        b.setBridgeType(getField(fields, 5));
        b.setStructureType(getField(fields, 6));
        b.setSpanCombination(getField(fields, 7));

        // 数值字段
        b.setTotalLength(parseDouble(getField(fields, 8), 0.0));
        b.setTotalWidth(parseDouble(getField(fields, 9), 0.0));
        b.setClearSpan(parseDouble(getField(fields, 10), 0.0));

        // 字符串字段
        b.setDesignLoad(getField(fields, 11));
        b.setAntiSeismic(getField(fields, 12));
        b.setDesignUnit(getField(fields, 13));
        b.setConstructUnit(getField(fields, 14));
        b.setSuperviseUnit(getField(fields, 15));
        b.setCompleteDate(getField(fields, 16));
        b.setOpenDate(getField(fields, 17));
        b.setManageUnit(getField(fields, 18));
        b.setMaintainUnit(getField(fields, 19));
        b.setCheckLevel(getField(fields, 20));

        // 技术状况（整数）
        b.setTechStatus(parseInt(getField(fields, 21), 1));

        // 养护里程
        b.setMaintenanceLength(parseDouble(getField(fields, 22), 0.0));

        // 经纬度
        b.setLongitude(getField(fields, 23));
        b.setLatitude(getField(fields, 24));

        // 照片路径（可选）
        b.setPhotoFront(getField(fields, 25));
        b.setPhotoLeft(getField(fields, 26));
        b.setPhotoRight(getField(fields, 27));

        // 备注（可选，可能包含逗号）
        b.setRemark(getField(fields, 28));

        return b;
    }

    /**
     * 分割CSV行（处理引号内的逗号）
     */
    private static List<String> splitCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // 检查是否是转义引号 ("")
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++; // 跳过下一个引号
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        // 添加最后一个字段
        result.add(current.toString().trim());

        return result;
    }

    /**
     * 安全获取字段值
     */
    private static String getField(List<String> fields, int index) {
        if (index < fields.size()) {
            String value = fields.get(index);
            return value.isEmpty() ? null : value;
        }
        return null;
    }

    /**
     * 解析整数
     */
    private static int parseInt(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 解析浮点数
     */
    private static double parseDouble(String value, double defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 测试方法：验证CSV导入功能
     */
    public static void main(String[] args) {
        String csvPath = "bridge_data_50.csv";
        List<Bridge> bridges = importFromCSV(csvPath);

        System.out.println("成功导入桥梁数量: " + bridges.size());

        if (!bridges.isEmpty()) {
            Bridge first = bridges.get(0);
            System.out.println("\n第一条记录示例:");
            System.out.println("  编号: " + first.getBridgeNo());
            System.out.println("  名称: " + first.getBridgeName());
            System.out.println("  路线: " + first.getRouteName());
            System.out.println("  类型: " + first.getBridgeType());
            System.out.println("  结构: " + first.getStructureType());
            System.out.println("  全长: " + first.getTotalLength() + "m");
            System.out.println("  总宽: " + first.getTotalWidth() + "m");
            System.out.println("  设计荷载: " + first.getDesignLoad());
            System.out.println("  检查等级: " + first.getCheckLevel());
            System.out.println("  技术状况: " + first.getTechStatus());
            System.out.println("  经度: " + first.getLongitude());
            System.out.println("  纬度: " + first.getLatitude());
        }
    }
}
