# Bridge Inspection System

<p align="center">
  <b>公路桥梁初始检查信息系统 — 桌面版</b>
</p>

## 项目概述

本项目是一款面向**公路桥梁管理部门**的桌面信息管理系统，采用 **Java + Swing** 技术栈开发。系统覆盖桥梁管理的完整业务流程，包括**桥梁档案管理、初始检查、定期检查、BCI 趋势分析、检查提醒、数据统计、用户权限管理**等核心模块，支持管理员和检查员两种角色的分级操作。

项目实现了完整的 **MVC 三层架构**，包含 67 个 Java 源文件，代码结构清晰、功能完备，是 Swing GUI 开发的优秀实践案例。

---

## 技术栈

| 技术 | 用途 |
|------|------|
| **Java 8+** | 核心开发语言 |
| **Swing** | GUI 框架，自定义现代 UI 组件 |
| **JDBC** | SQL Server 数据库访问 |
| **SQL Server / SQLite** | 主数据库（支持 SQLite 备用） |
| **连接池** | 自定义 `ConnectionPool`，管理数据库连接复用 |
| **密码加密** | `PasswordUtil` — MD5 + Salt 哈希 |

---

## 功能模块

### 1. 用户认证与权限管理
- **双角色系统**：管理员（全部功能）/ 检查员（前 6 项功能）
- 登录界面采用**现代卡片式设计**（CardPanel、RoundedButton）
- MD5 + Salt 密码加密存储
- 操作日志全程记录（`OperationLogService`）

### 2. 桥梁档案管理
- 桥梁基本状况卡片（24+ 字段）：编号、名称、路线、类型、结构、荷载等
- 照片上传：正面、左侧、右侧多角度照片
- CSV 批量导入（`CsvBridgeImporter`）
- 经纬度定位，支持地图预览

### 3. 初始检查
- 初始检查表录入（外观、结构、材料等）
- 部件评分系统（`BridgeComponentScore`）
- **BCI 计算器**（`BCICalculator`）：自动计算桥梁状况指数

### 4. 定期检查
- 周期性检查记录
- 检查历史追踪
- 与前次检查结果对比

### 5. BCI 趋势分析
- 历史 BCI 数据可视化趋势图
- 桥梁技术状况变化曲线
- 养护建议自动生成（`MaintenanceRecommendationService`）

### 6. 检查提醒
- 基于检查周期的智能提醒
- 逾期未检桥梁预警
- 提醒状态跟踪

### 7. 数据统计
- 桥梁数量统计
- 技术等级分布
- 检查完成率
- 图表可视化展示

### 8. 系统维护
- 数据字典管理
- 数据备份与恢复
- 缓存清理（`CacheManager`）
- 日志查看

---

## 项目结构（MVC 架构）

```
BridgeInspectionSystem/
├── src/
│   ├── Main.java                         # 程序入口
│   ├── entity/                           # 实体层（Model）
│   │   ├── Bridge.java                   # 桥梁实体
│   │   ├── BridgeComponentScore.java     # 部件评分
│   │   ├── BridgeInitialCheck.java       # 初始检查
│   │   ├── BridgeRegularCheck.java       # 定期检查
│   │   ├── CheckReminder.java            # 检查提醒
│   │   ├── DictionaryItem.java           # 数据字典
│   │   ├── OperationLog.java             # 操作日志
│   │   └── User.java                     # 用户实体
│   ├── dao/                              # 数据访问层
│   │   ├── BridgeDao.java / BridgeDaoImpl.java
│   │   ├── BridgeInitialCheckDao.java / ...Impl.java
│   │   ├── BridgeRegularCheckDao.java / ...Impl.java
│   │   ├── BridgeComponentScoreDao.java / ...Impl.java
│   │   ├── UserDao.java / UserDaoImpl.java
│   │   ├── DictionaryDao.java / ...Impl.java
│   │   └── OperationLogDao.java / ...Impl.java
│   ├── service/                          # 业务逻辑层
│   │   ├── BridgeService.java
│   │   ├── BridgeInitialCheckService.java
│   │   ├── BridgeRegularCheckService.java
│   │   ├── UserService.java
│   │   ├── DictionaryService.java
│   │   ├── ReportService.java            # 报告生成
│   │   ├── StatisticsService.java        # 统计分析
│   │   ├── ReminderService.java          # 提醒管理
│   │   └── OperationLogService.java      # 日志服务
│   ├── ui/                               # 视图层
│   │   ├── LoginFrame.java               # 登录界面（卡片式）
│   │   ├── MainFrame.java                # 主界面（侧边栏导航）
│   │   ├── BridgeManagePanel.java        # 桥梁管理面板
│   │   ├── BridgeInitialCheckPanel.java  # 初始检查面板
│   │   ├── BridgeRegularCheckPanel.java  # 定期检查面板
│   │   ├── BCITrendChartPanel.java       # BCI 趋势图
│   │   ├── ReminderPanel.java            # 提醒面板
│   │   ├── StatisticsPanel.java          # 统计面板
│   │   ├── UserManagePanel.java          # 用户管理
│   │   ├── SystemMaintenancePanel.java   # 系统维护
│   │   ├── MapPreviewFrame.java          # 地图预览
│   │   ├── PhotoUploadPanel.java         # 照片上传
│   │   └── common/                       # 通用 UI 组件
│   │       ├── CardPanel.java            # 卡片容器
│   │       ├── RoundedButton.java        # 圆角按钮
│   │       ├── StyledTable.java          # 样式表格
│   │       ├── SearchTextField.java      # 搜索框
│   │       ├── PaginationPanel.java      # 分页组件
│   │       ├── LoadingOverlay.java       # 加载遮罩
│   │       └── ThemeColors.java          # 主题配色
│   └── util/                             # 工具类
│       ├── DBUtil.java                   # JDBC 工具（SQL Server）
│       ├── DBUtilSQLite.java             # SQLite 备用
│       ├── ConnectionPool.java           # 数据库连接池
│       ├── CacheManager.java             # 数据缓存
│       ├── PasswordUtil.java             # 密码加密
│       ├── BCICalculator.java            # BCI 计算器
│       ├── CsvBridgeImporter.java        # CSV 导入
│       ├── ValidationUtil.java           # 数据校验
│       ├── PaginationUtil.java           # 分页工具
│       └── Logger.java                   # 日志工具
├── lib/                                  # 第三方依赖（JDBC 驱动等）
└── .gitignore
```

---

## UI 设计亮点

### 现代卡片式登录界面
- `CardPanel` 阴影圆角卡片
- `RoundedButton` 圆角渐变按钮
- 底部提示文字：默认账号密码
- 回车键快捷登录

### 侧边栏导航系统（MainFrame）
```
┌─────────────────┬──────────────────────────────┐
│ 桥梁检查系统     │                              │
│ 张三 | 管理员    │    [当前选中面板内容]         │
│ ─────────────── │                              │
│ ▌ 桥梁卡片      │                              │
│   初始检查      │                              │
│   定期检查      │                              │
│   BCI趋势       │                              │
│   检查提醒      │                              │
│   数据统计      │                              │
│   用户管理      │                              │
│   系统维护      │                              │
│ ─────────────── │                              │
│ ⟲ 切换用户      │                              │
└─────────────────┴──────────────────────────────┘
```

- 8 个功能模块，每个配独立主题色
- 管理员 / 检查员角色自动过滤菜单
- **CardLayout 面板缓存**：避免重复创建
- **SwingWorker 异步加载**：切换面板不卡顿
- **后台预加载**：登录后空闲时间预加载常用数据

---

## 性能优化

| 优化点 | 实现方式 | 效果 |
|--------|----------|------|
| 连接池 | `ConnectionPool` 复用连接 | 减少频繁创建开销 |
| 数据缓存 | `CacheManager` 内存缓存 | 避免重复查询 |
| 异步加载 | `SwingWorker` 后台线程 | UI 不卡顿 |
| 面板缓存 | `CardLayout` + 实例复用 | 快速切换 |
| 分页查询 | `PaginationUtil` | 大数据量不卡 |

---

## 运行方式

### 环境要求
- JDK 8+
- SQL Server 2019+（或 SQLite 备用）
- mssql-jdbc 驱动 jar

### 数据库配置
在 `util/DBUtil.java` 中配置连接信息：
```java
private static final String URL = "jdbc:sqlserver://localhost;databaseName=bridge_inspection";
private static final String USER = "sa";
private static final String PASSWORD = "your_password";
```

### 启动步骤
```bash
# 1. 确保 SQL Server 已启动，数据库已创建
# 2. 添加 mssql-jdbc-*.jar 到项目依赖
# 3. 编译运行
javac -cp "lib/*;src" src/Main.java
java -cp "lib/*;src" Main
```

---

## 截图预览

> （待补充：登录界面、主界面、桥梁管理、BCI 趋势图）

---

## 待改进方向

- [ ] 引入 Maven 管理依赖
- [ ] 添加 JUnit 单元测试
- [ ] 导出 PDF / Excel 报告
- [ ] 集成百度地图 API 实现真实地图定位
- [ ] 添加数据导入向导界面

---

> 本项目为课程设计实践项目，代码仅供学习参考。
