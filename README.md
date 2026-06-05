# 公路桥梁初始检查信息系统（SQL Server 版）

## 项目简介

本项目是《程序设计综合实践II》课程设计成果，参照《公路桥涵养护规范 JTG 5120－2021》编制，实现公路桥梁养护管理信息化。

## 开发团队

| 姓名 | 角色 | 负责模块 |
|------|------|---------|
| 张子健 | 组长 | 桥梁基本状况卡片管理模块 |
| 郑晟 | 成员 | 桥梁初始检查记录管理模块 |
| 谭容昊 | 成员 | 桥梁定期检查记录管理模块 |
| 曹城钧 | 成员 | 用户管理与数据统计查询模块 |

## 技术栈

- **开发语言**：Java 11+
- **界面框架**：Java Swing
- **数据库**：SQL Server 2019+
- **JDBC 驱动**：mssql-jdbc-12.8.1.jre11.jar
- **IDE**：IntelliJ IDEA
- **版本控制**：Git

## 环境准备

### 1. 确认 SQL Server 已就绪

确保你的电脑上：
- SQL Server 服务正在运行
- SQL Server 身份验证已启用（sa 账户可用）
- SSMS 可以正常连接

### 2. 创建数据库

打开 **SSMS**，执行 `sql/bridge_inspection_v2.sql` 脚本：

```sql
-- 文件位置: BridgeInspectionSystem/sql/bridge_inspection_v2.sql
-- 在 SSMS 中打开并执行，会自动创建数据库、表和初始数据
```

> **注意**：V2 版本数据库脚本增加了 `bridge_component_score` 部件评分明细表，密码字段长度增加到 200，并增加了数据库索引。

### 3. 配置数据库连接

打开 `src/util/DBUtil.java`，根据你的实际情况修改：

```java
// 如果你的 sa 密码不是 123456，请修改这里：
private static final String PASSWORD = "你的sa密码";

// 如果 SQL Server 不是默认实例或改了端口，修改 URL：
private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=bridge_inspection;encrypt=false";
```

### 4. 添加 JDBC 驱动到 IDEA

1. 打开 IDEA → `File` → `Project Structure`（`Ctrl+Alt+Shift+S`）
2. 左侧选择 `Modules` → 你的项目 → `Dependencies` 标签页
3. 点击右侧 `+` 号 → `JARs or Directories...`
4. 选择：`BridgeInspectionSystem/lib/mssql-jdbc-12.8.1.jre11.jar`
5. 点击 `Apply` → `OK`
6. 右键 `src` 文件夹 → `Mark Directory as` → `Sources Root`
7. 右键 `test` 文件夹 → `Mark Directory as` → `Test Sources Root`

### 5. 编译运行

运行 `Main.java` 中的 `main` 方法即可启动系统。

## 默认登录账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| `admin` | `admin123` | 管理员（全部权限）|
| `zhangzijian` | `123456` | 检查员 |
| `zhengsheng` | `123456` | 检查员 |
| `tanronghao` | `123456` | 检查员 |
| `caochengjun` | `123456` | 检查员 |

> **安全说明**：用户密码采用 SHA-256 + 盐值 加密存储，兼容旧版明文密码过渡。

## 系统功能

### 核心功能
1. **桥梁基本状况卡片管理** — 桥梁信息的增删改查 + 地图定位 + 照片上传 + 导出报表
2. **桥梁初始检查记录管理** — 初始检查记录的增删改查
3. **桥梁定期检查记录管理** — 定期检查记录 + BCI 自动计算（按 JTG 5120-2021 规范）+ 部件评分模板 + 报告导出
4. **数据统计查询** — 按类型、等级、技术状况等多维度统计
5. **用户管理** — 仅管理员可见，密码加密存储
6. **系统维护** — 数据备份、日志查看（仅管理员可见）

### 新增亮点功能
- **GIS 地图集成** — 在桥梁管理中点击「地图定位」，可在地图中查看桥梁位置
- **报告导出** — 支持导出 HTML 格式技术状况评定报告、CSV 格式数据报表
- **照片上传** — 支持桥梁正面/左侧/右侧照片上传和预览
- **按桥型分类检查模板** — 梁式桥/拱桥/斜拉桥/悬索桥各有不同部件权重
- **BCI 规范算法** — 按 JTG 5120-2021 分层加权计算，非简单平均
- **数据校验** — 日期格式、经纬度范围、评分范围自动校验
- **事务控制** — 关键操作使用数据库事务保证数据一致性
- **日志记录** — 系统自动记录操作日志到 `logs/` 目录

## 项目结构

```
BridgeInspectionSystem/
├── src/
│   ├── Main.java                    # 系统入口
│   ├── entity/                      # 实体层 (5个)
│   │   ├── Bridge.java
│   │   ├── BridgeInitialCheck.java
│   │   ├── BridgeRegularCheck.java
│   │   ├── BridgeComponentScore.java    # 新增：部件评分
│   │   └── User.java
│   ├── dao/                         # 数据访问层 (10个)
│   │   ├── BridgeDao.java / BridgeDaoImpl.java
│   │   ├── BridgeInitialCheckDao.java / BridgeInitialCheckDaoImpl.java
│   │   ├── BridgeRegularCheckDao.java / BridgeRegularCheckDaoImpl.java
│   │   ├── BridgeComponentScoreDao.java / BridgeComponentScoreDaoImpl.java  # 新增
│   │   └── UserDao.java / UserDaoImpl.java
│   ├── service/                     # 业务逻辑层 (6个)
│   │   ├── BridgeService.java
│   │   ├── BridgeInitialCheckService.java
│   │   ├── BridgeRegularCheckService.java   # BCI规范算法
│   │   ├── StatisticsService.java
│   │   ├── UserService.java                 # 密码加密
│   │   └── ReportService.java               # 新增：报告导出
│   ├── ui/                          # 界面层 (10个)
│   │   ├── LoginFrame.java
│   │   ├── MainFrame.java
│   │   ├── BridgeManagePanel.java           # 新增：地图+导出
│   │   ├── BridgeInitialCheckPanel.java
│   │   ├── BridgeRegularCheckPanel.java     # 新增：模板+报告
│   │   ├── StatisticsPanel.java
│   │   ├── UserManagePanel.java
│   │   ├── MapPreviewFrame.java             # 新增：GIS地图
│   │   ├── PhotoUploadPanel.java            # 新增：照片上传
│   │   └── SystemMaintenancePanel.java      # 新增：系统维护
│   └── util/                        # 工具类
│       ├── DBUtil.java              # 数据库工具（含事务控制）
│       ├── BCICalculator.java       # 新增：BCI规范计算器
│       ├── PasswordUtil.java        # 新增：密码加密
│       ├── Logger.java              # 新增：日志工具
│       └── ValidationUtil.java      # 新增：数据校验
├── test/                            # 单元测试
│   └── util/
│       ├── BCICalculatorTest.java
│       ├── PasswordUtilTest.java
│       └── ValidationUtilTest.java
├── lib/
│   └── mssql-jdbc-12.8.1.jre11.jar  # SQL Server JDBC 驱动
├── sql/
│   ├── bridge_inspection.sql        # 原始数据库脚本
│   └── bridge_inspection_v2.sql     # 优化版数据库脚本（推荐使用）
├── doc/
│   ├── diagrams/                    # 新增：设计模型图
│   │   ├── ER_Diagram.puml          # E-R图
│   │   ├── UseCase_Diagram.puml     # 用例图
│   │   ├── DFD_Level0.puml          # 顶层数据流图
│   │   ├── DFD_Level1.puml          # 一层数据流图
│   │   ├── Class_Diagram.puml       # 类图
│   │   └── README.md                # 图表使用说明
│   ├── 团队设计报告.docx
│   ├── 张子健-个人设计报告.docx
│   ├── 郑晟-个人设计报告.docx
│   ├── 谭容昊-个人设计报告.docx
│   └── 曹城钧-个人设计报告.docx
├── logs/                            # 运行时自动生成：日志目录
├── photos/                          # 运行时自动生成：照片目录
└── README.md
```

## Git 使用说明

本项目已使用 Git 进行版本控制。

```bash
# 查看提交历史
git log --oneline --graph

# 查看当前状态
git status

# 创建功能分支
git checkout -b feature-xxx

# 合并分支
git checkout master
git merge feature-xxx
```

## 单元测试运行

在 IDEA 中：
1. 右键 `test/` 目录 → `Run 'All Tests'`
2. 或右键单个测试文件 → `Run 'xxxTest'`

测试覆盖：
- `BCICalculatorTest` — BCI 计算算法验证
- `PasswordUtilTest` — 密码加密验证
- `ValidationUtilTest` — 数据校验验证

## 常见问题

**Q: 提示 "SQL Server JDBC驱动加载失败"**
> A: 没有正确添加 mssql-jdbc jar 包到项目依赖。请按步骤4操作。

**Q: 提示 "无法连接到SQL Server数据库"**
> A: 检查以下几点：
> 1. SQL Server 服务是否启动（ services.msc 中查看 SQL Server (MSSQLSERVER) ）
> 2. 数据库 `bridge_inspection` 是否已创建（SSMS 中查看）
> 3. `DBUtil.java` 中的用户名密码是否正确
> 4. SQL Server 是否启用了 TCP/IP（SQL Server 配置管理器中查看）
> 5. 防火墙是否放行了 1433 端口

**Q: 登录时提示密码错误**
> A: 系统已升级为密码加密存储。如果数据库中仍是明文密码，系统会自动兼容。建议重新执行 V2 数据库脚本以使用加密密码。

**Q: 地图功能无法加载**
> A: 地图使用 OpenStreetMap 静态图 API，需要联网。如果加载失败，可使用「在浏览器中打开」按钮在浏览器中查看。

**Q: 报告导出后如何打开**
> A: 导出的 HTML 报告可用任何浏览器打开；CSV 文件可用 Excel 打开。

## 规范依据

- 《JTG 5120-2021 公路桥涵养护规范》
- BCI 计算采用规范推荐权重：桥面系 15%、上部结构 35%、下部结构 35%、附属设施 15%
