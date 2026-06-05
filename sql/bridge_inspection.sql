-- =================================================================
-- 公路桥梁初始检查信息系统 - SQL Server 数据库脚本
-- 请在 SSMS 中执行此脚本创建数据库和表
-- =================================================================

-- 第一步：创建数据库（如果尚未创建）
IF DB_ID('bridge_inspection') IS NULL
BEGIN
    CREATE DATABASE bridge_inspection
        COLLATE Chinese_PRC_CI_AS;
    PRINT '数据库 bridge_inspection 创建成功';
END
ELSE
BEGIN
    PRINT '数据库 bridge_inspection 已存在';
END
GO

USE bridge_inspection;
GO

-- =================================================================
-- 用户表
-- =================================================================
IF OBJECT_ID('dbo.[user]', 'U') IS NOT NULL
    DROP TABLE dbo.[user];
GO

CREATE TABLE dbo.[user] (
    id              INT IDENTITY(1,1) PRIMARY KEY,
    username        NVARCHAR(50) NOT NULL UNIQUE,
    password        NVARCHAR(100) NOT NULL,
    real_name       NVARCHAR(50),
    role            NVARCHAR(20) DEFAULT 'inspector',
    phone           NVARCHAR(20),
    create_time     DATETIME DEFAULT GETDATE()
);
GO

-- =================================================================
-- 桥梁基本状况卡片表
-- =================================================================
IF OBJECT_ID('dbo.bridge', 'U') IS NOT NULL
    DROP TABLE dbo.bridge;
GO

CREATE TABLE dbo.bridge (
    id                  INT IDENTITY(1,1) PRIMARY KEY,
    bridge_no           NVARCHAR(50) NOT NULL UNIQUE,
    bridge_name         NVARCHAR(100) NOT NULL,
    route_name          NVARCHAR(100),
    route_grade         NVARCHAR(20),
    bridge_type         NVARCHAR(50),
    structure_type      NVARCHAR(50),
    span_combination    NVARCHAR(100),
    total_length        DECIMAL(10,2),
    total_width         DECIMAL(10,2),
    clear_span          DECIMAL(10,2),
    design_load         NVARCHAR(50),
    anti_seismic        NVARCHAR(50),
    design_unit         NVARCHAR(100),
    construct_unit      NVARCHAR(100),
    supervise_unit      NVARCHAR(100),
    complete_date       DATE,
    open_date           DATE,
    manage_unit         NVARCHAR(100),
    maintain_unit       NVARCHAR(100),
    check_level         NVARCHAR(10) DEFAULT 'II',
    tech_status         INT DEFAULT 1,
    maintenance_length  DECIMAL(10,2),
    longitude           NVARCHAR(50),
    latitude            NVARCHAR(50),
    photo_front         NVARCHAR(255),
    photo_left          NVARCHAR(255),
    photo_right         NVARCHAR(255),
    remark              NVARCHAR(MAX),
    create_time         DATETIME DEFAULT GETDATE(),
    update_time         DATETIME DEFAULT GETDATE()
);
GO

-- =================================================================
-- 桥梁初始检查记录表
-- =================================================================
IF OBJECT_ID('dbo.bridge_initial_check', 'U') IS NOT NULL
    DROP TABLE dbo.bridge_initial_check;
GO

CREATE TABLE dbo.bridge_initial_check (
    id                          INT IDENTITY(1,1) PRIMARY KEY,
    bridge_id                   INT NOT NULL,
    check_no                    NVARCHAR(50) NOT NULL UNIQUE,
    check_date                  DATE NOT NULL,
    checker                     NVARCHAR(50),
    weather                     NVARCHAR(20),
    temperature                 NVARCHAR(20),
    check_content               NVARCHAR(MAX),
    deck_condition              NVARCHAR(20),
    superstructure_condition    NVARCHAR(20),
    substructure_condition      NVARCHAR(20),
    accessory_condition         NVARCHAR(20),
    defect_desc                 NVARCHAR(MAX),
    defect_photo                NVARCHAR(255),
    suggest                     NVARCHAR(MAX),
    conclusion                  NVARCHAR(500),
    next_check_date             DATE,
    check_report                NVARCHAR(255),
    create_time                 DATETIME DEFAULT GETDATE(),
    update_time                 DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_initial_bridge FOREIGN KEY (bridge_id) REFERENCES dbo.bridge(id) ON DELETE CASCADE
);
GO

-- =================================================================
-- 桥梁定期检查记录表
-- =================================================================
IF OBJECT_ID('dbo.bridge_regular_check', 'U') IS NOT NULL
    DROP TABLE dbo.bridge_regular_check;
GO

CREATE TABLE dbo.bridge_regular_check (
    id                      INT IDENTITY(1,1) PRIMARY KEY,
    bridge_id               INT NOT NULL,
    check_no                NVARCHAR(50) NOT NULL UNIQUE,
    check_date              DATE NOT NULL,
    checker                 NVARCHAR(50),
    weather                 NVARCHAR(20),
    temperature             NVARCHAR(20),
    check_type              NVARCHAR(20) DEFAULT '定期检查',
    deck_score              INT,
    superstructure_score    INT,
    substructure_score      INT,
    accessory_score         INT,
    bci                     DECIMAL(5,2),
    tech_status             NVARCHAR(10) DEFAULT '1类',
    defect_desc             NVARCHAR(MAX),
    maintenance_suggest     NVARCHAR(MAX),
    limitation_suggest      NVARCHAR(MAX),
    check_conclusion        NVARCHAR(MAX),
    next_check_date         DATE,
    create_time             DATETIME DEFAULT GETDATE(),
    update_time             DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_regular_bridge FOREIGN KEY (bridge_id) REFERENCES dbo.bridge(id) ON DELETE CASCADE
);
GO

-- =================================================================
-- 插入默认管理员和团队成员数据
-- =================================================================
INSERT INTO dbo.[user] (username, password, real_name, role, phone) VALUES
('admin', 'admin123', '系统管理员', 'admin', '13800138000'),
('zhangzijian', '123456', '张子健', 'inspector', '13800138001'),
('zhengsheng', '123456', '郑晟', 'inspector', '13800138002'),
('tanronghao', '123456', '谭容昊', 'inspector', '13800138003'),
('caochengjun', '123456', '曹城钧', 'inspector', '13800138004');
GO

-- =================================================================
-- 插入示例桥梁数据
-- =================================================================
INSERT INTO dbo.bridge (bridge_no, bridge_name, route_name, route_grade, bridge_type, structure_type, span_combination, total_length, total_width, design_load, check_level, manage_unit, complete_date) VALUES
('CQ001', '长江大桥', 'G50沪渝高速', '高速公路', '梁式桥', '预应力混凝土T梁', '30m×5', 156.00, 28.50, '公路-Ⅰ级', 'I', '重庆高速集团', '2015-06-15'),
('CQ002', '嘉陵江大桥', 'S203省道', '二级公路', '拱桥', '钢筋混凝土箱形拱', '80m+120m+80m', 285.00, 12.00, '公路-Ⅱ级', 'II', '重庆交通局', '2010-09-20'),
('CQ003', '小南海桥', 'X456县道', '四级公路', '梁式桥', '简支梁桥', '16m×3', 52.00, 8.50, '公路-Ⅱ级', 'III', '南岸区公路局', '2018-03-10'),
('CQ004', '石门大桥', 'G75兰海高速', '高速公路', '斜拉桥', '双塔双索面斜拉桥', '200m+450m+200m', 856.00, 32.00, '公路-Ⅰ级', 'I', '重庆高速集团', '2012-12-28'),
('CQ005', '鹅公岩大桥', '九龙坡区主干道', '城市快速路', '悬索桥', '单跨悬索桥', '600m', 1022.00, 35.50, '城-A级', 'I', '重庆市城投公司', '2000-12-28'),
('CQ006', '黄花园大桥', '渝中区滨江路', '城市主干路', '刚架拱桥', '钢筋混凝土刚架拱', '45m×3', 142.00, 24.00, '城-B级', 'II', '渝中区市政局', '2005-06-18'),
('CQ007', '菜园坝大桥', '重庆内环快速', '城市快速路', '钢-混凝土组合拱桥', '中承式钢管混凝土拱桥', '120m+280m+120m', 1866.00, 30.50, '城-A级', 'I', '重庆市建委', '2007-10-29');
GO

PRINT '所有表创建完成，初始数据插入成功！';
GO
