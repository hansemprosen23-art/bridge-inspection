# 系统设计模型图

本目录包含公路桥梁初始检查信息系统的全部设计模型图，使用 PlantUML 语法编写。

## 文件说明

| 文件名 | 说明 | 用途 |
|--------|------|------|
| `ER_Diagram.puml` | E-R图（概念模型） | 展示实体关系，用于团队报告 |
| `UseCase_Diagram.puml` | 用例图（功能模型） | 展示用户角色和功能，用于团队报告 |
| `DFD_Level0.puml` | 顶层数据流图 | 展示系统整体数据流向 |
| `DFD_Level1.puml` | 一层数据流图 | 展示各模块详细数据流 |
| `Class_Diagram.puml` | 类图 | 展示系统类结构和关系 |

## 如何生成图片

### 方法1：使用 PlantUML 在线服务器
1. 访问 https://www.plantuml.com/plantuml/uml/
2. 复制 `.puml` 文件内容粘贴到编辑框
3. 自动生成图片，可下载 PNG/SVG

### 方法2：使用 VSCode 插件
1. 安装 "PlantUML" 插件
2. 打开 `.puml` 文件
3. 按 `Alt+D` 预览，右键导出图片

### 方法3：使用本地 Java 运行
```bash
# 下载 plantuml.jar
java -jar plantuml.jar ER_Diagram.puml
# 会在同级目录生成 ER_Diagram.png
```

## 模型说明

### E-R图
- **实体**：用户、桥梁、初始检查记录、定期检查记录、部件评分明细
- **关系**：1对多（桥梁-检查记录）、1对多（定期检查-部件评分）

### 用例图
- **角色**：管理员（全部权限）、检查员（检查相关权限）
- **核心用例**：BCI计算、部件评分、报告生成

### 数据流图
- **顶层**：展示系统与外部实体的交互
- **一层**：展示6大模块（P1-P6）与6个数据存储（D1-D6）的交互

### 类图
- **分层结构**：Entity → DAO → Service → UI
- **工具类**：DBUtil、BCICalculator、PasswordUtil、Logger、ValidationUtil
