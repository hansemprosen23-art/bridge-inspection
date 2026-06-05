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

## 环境准备

### 1. 确认 SQL Server 已就绪

确保你的电脑上：
- SQL Server 服务正在运行
- SQL Server 身份验证已启用（sa 账户可用）
- SSMS 可以正常连接

### 2. 创建数据库

打开 **SSMS**，执行 `sql/bridge_inspection.sql` 脚本：

```sql
-- 文件位置: BridgeInspectionSystem/sql/bridge_inspection.sql
-- 在 SSMS 中打开并执行，会自动创建数据库、表和初始数据
```

### 3. 配置数据库连接

打开 `src/util/DBUtil.java`，根据你的实际情况修改：

```java
// 如果你的 sa 密码不是 123456，请修改这里：
private static final String PASSWORD = "你的sa密码";

// 如果 SQL Server 不是默认实例或改了端口，修改 URL：
private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=bridge_inspection;encrypt=false";
```

**如果使用 Windows 身份验证**，把 DBUtil.java 中注释掉的 Windows 认证代码取消注释，并注释掉 sa 认证那部分。

### 4. 添加 JDBC 驱动到 IDEA

1. 打开 IDEA → `File` → `Project Structure`（`Ctrl+Alt+Shift+S`）
2. 左侧选择 `Modules` → 你的项目 → `Dependencies` 标签页
3. 点击右侧 `+` 号 → `JARs or Directories...`
4. 选择：`BridgeInspectionSystem/lib/mssql-jdbc-12.8.1.jre11.jar`
5. 点击 `Apply` → `OK`
6. 右键 `src` 文件夹 → `Mark Directory as` → `Sources Root`

### 5. 编译运行

运行 `Main.java` 中的 `main` 方法即可启动系统。

## 默认登录账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| `admin` | `admin123` | 管理员（能看到用户管理）|
| `zhangzijian` | `123456` | 检查员 |
| `zhengsheng` | `123456` | 检查员 |
| `tanronghao` | `123456` | 检查员 |
| `caochengjun` | `123456` | 检查员 |

## 系统功能

1. **桥梁基本状况卡片管理** — 桥梁信息的增删改查
2. **桥梁初始检查记录管理** — 初始检查记录的增删改查
3. **桥梁定期检查记录管理** — 定期检查记录 + BCI 自动计算
4. **数据统计查询** — 按类型、等级、技术状况等多维度统计
5. **用户管理** — 仅管理员可见

## 项目结构

```
BridgeInspectionSystem/
├── src/
│   ├── Main.java                    # 系统入口
│   ├── entity/                      # 实体层 (4个)
│   ├── dao/                         # 数据访问层 (8个接口+实现)
│   ├── service/                     # 业务逻辑层 (5个)
│   ├── ui/                          # 界面层 (7个)
│   └── util/                        # 工具类
│       ├── DBUtil.java              # SQL Server 数据库工具（当前使用）
│       ├── DBUtilSQLServer.java     # 同 DBUtil.java（备份）
│       └── DBUtilSQLite.java        # SQLite 版本（备用）
├── lib/
│   └── mssql-jdbc-12.8.1.jre11.jar  # SQL Server JDBC 驱动
├── sql/
│   └── bridge_inspection.sql        # SQL Server 建库脚本
├── doc/
│   ├── 团队设计报告.docx
│   ├── 张子健-个人设计报告.docx
│   ├── 郑晟-个人设计报告.docx
│   ├── 谭容昊-个人设计报告.docx
│   └── 曹城钧-个人设计报告.docx
└── README.md
```

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

**Q: 想用 Windows 身份验证登录 SQL Server**
> A: 修改 `DBUtil.java` 中 URL 为 `integratedSecurity=true`，并确保 sqljdbc_auth.dll 在系统 PATH 中。

**Q: 如果 SQL Server 报告中说要用 sqlserver，但我已经做好 sqlite 了**
> A: 本系统默认就是 SQL Server 版本。SQLite 版本仅在 `DBUtilSQLite.java` 中作为备用保留，不影响当前运行。
