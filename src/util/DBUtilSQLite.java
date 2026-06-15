package util;

import java.sql.*;

/**
 * SQLite数据库工具类（备用方案）
 * 无需安装数据库，零配置，单文件存储
 * 适合课程设计快速演示
 */
public class DBUtilSQLite {

    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String URL = "jdbc:sqlite:bridge_inspection.db";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            Logger.error("SQLite JDBC驱动加载失败", e);
            throw new RuntimeException("SQLite JDBC驱动加载失败，请确保sqlite-jdbc jar包已添加到项目中: " + e.getMessage());
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * 关闭连接资源
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            Logger.error("关闭SQLite数据库资源失败", e);
        }
    }

    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }

    /**
     * 测试数据库连接
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            Logger.error("SQLite数据库连接测试失败", e);
            return false;
        } finally {
            close(conn, null);
        }
    }

    /**
     * 初始化数据库表结构（首次运行时调用）
     */
    public static void initDatabase() {
        String[] sqls = {
            "CREATE TABLE IF NOT EXISTS user (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "username TEXT NOT NULL UNIQUE," +
            "password TEXT NOT NULL," +
            "real_name TEXT," +
            "role TEXT DEFAULT 'inspector'," +
            "phone TEXT," +
            "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")",
            
            "CREATE TABLE IF NOT EXISTS bridge (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "bridge_no TEXT NOT NULL UNIQUE," +
            "bridge_name TEXT NOT NULL," +
            "route_name TEXT," +
            "route_grade TEXT," +
            "bridge_type TEXT," +
            "structure_type TEXT," +
            "span_combination TEXT," +
            "total_length REAL," +
            "total_width REAL," +
            "clear_span REAL," +
            "design_load TEXT," +
            "anti_seismic TEXT," +
            "design_unit TEXT," +
            "construct_unit TEXT," +
            "supervise_unit TEXT," +
            "complete_date TEXT," +
            "open_date TEXT," +
            "manage_unit TEXT," +
            "maintain_unit TEXT," +
            "check_level TEXT DEFAULT 'II'," +
            "tech_status INTEGER DEFAULT 1," +
            "maintenance_length REAL," +
            "longitude TEXT," +
            "latitude TEXT," +
            "photo_front TEXT," +
            "photo_left TEXT," +
            "photo_right TEXT," +
            "remark TEXT," +
            "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")",
            
            "CREATE TABLE IF NOT EXISTS bridge_initial_check (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "bridge_id INTEGER NOT NULL," +
            "check_no TEXT NOT NULL UNIQUE," +
            "check_date TEXT NOT NULL," +
            "checker TEXT," +
            "weather TEXT," +
            "temperature TEXT," +
            "check_content TEXT," +
            "deck_condition TEXT," +
            "superstructure_condition TEXT," +
            "substructure_condition TEXT," +
            "accessory_condition TEXT," +
            "defect_desc TEXT," +
            "defect_photo TEXT," +
            "suggest TEXT," +
            "conclusion TEXT," +
            "next_check_date TEXT," +
            "check_report TEXT," +
            "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (bridge_id) REFERENCES bridge(id) ON DELETE CASCADE" +
            ")",
            
            "CREATE TABLE IF NOT EXISTS bridge_regular_check (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "bridge_id INTEGER NOT NULL," +
            "check_no TEXT NOT NULL UNIQUE," +
            "check_date TEXT NOT NULL," +
            "checker TEXT," +
            "weather TEXT," +
            "temperature TEXT," +
            "check_type TEXT DEFAULT '定期检查'," +
            "deck_score INTEGER," +
            "superstructure_score INTEGER," +
            "substructure_score INTEGER," +
            "accessory_score INTEGER," +
            "bci REAL," +
            "tech_status TEXT DEFAULT '1类'," +
            "defect_desc TEXT," +
            "maintenance_suggest TEXT," +
            "limitation_suggest TEXT," +
            "check_conclusion TEXT," +
            "next_check_date TEXT," +
            "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (bridge_id) REFERENCES bridge(id) ON DELETE CASCADE" +
            ")",
            
            // 插入默认用户
            "INSERT OR IGNORE INTO user (id, username, password, real_name, role, phone) VALUES (1, 'admin', 'admin123', '系统管理员', 'admin', '13800138000')",
            "INSERT OR IGNORE INTO user (id, username, password, real_name, role, phone) VALUES (2, 'zhangzijian', '123456', '张子健', 'inspector', '13800138001')",
            "INSERT OR IGNORE INTO user (id, username, password, real_name, role, phone) VALUES (3, 'zhengsheng', '123456', '郑晟', 'inspector', '13800138002')",
            "INSERT OR IGNORE INTO user (id, username, password, real_name, role, phone) VALUES (4, 'tanronghao', '123456', '谭容昊', 'inspector', '13800138003')",
            "INSERT OR IGNORE INTO user (id, username, password, real_name, role, phone) VALUES (5, 'caochengjun', '123456', '曹城钧', 'inspector', '13800138004')",
            
            // 插入示例桥梁
            "INSERT OR IGNORE INTO bridge (id, bridge_no, bridge_name, route_name, route_grade, bridge_type, structure_type, span_combination, total_length, total_width, design_load, check_level, manage_unit, complete_date) VALUES (1, 'CQ001', '长江大桥', 'G50沪渝高速', '高速公路', '梁式桥', '预应力混凝土T梁', '30m×5', 156.00, 28.50, '公路-Ⅰ级', 'Ⅰ', '重庆高速集团', '2015-06-15')",
            "INSERT OR IGNORE INTO bridge (id, bridge_no, bridge_name, route_name, route_grade, bridge_type, structure_type, span_combination, total_length, total_width, design_load, check_level, manage_unit, complete_date) VALUES (2, 'CQ002', '嘉陵江大桥', 'S203省道', '二级公路', '拱桥', '钢筋混凝土箱形拱', '80m+120m+80m', 285.00, 12.00, '公路-Ⅱ级', 'Ⅱ', '重庆交通局', '2010-09-20')",
            "INSERT OR IGNORE INTO bridge (id, bridge_no, bridge_name, route_name, route_grade, bridge_type, structure_type, span_combination, total_length, total_width, design_load, check_level, manage_unit, complete_date) VALUES (3, 'CQ003', '小南海桥', 'X456县道', '四级公路', '梁式桥', '简支梁桥', '16m×3', 52.00, 8.50, '公路-Ⅱ级', 'Ⅲ', '南岸区公路局', '2018-03-10')",
            "INSERT OR IGNORE INTO bridge (id, bridge_no, bridge_name, route_name, route_grade, bridge_type, structure_type, span_combination, total_length, total_width, design_load, check_level, manage_unit, complete_date) VALUES (4, 'CQ004', '石门大桥', 'G75兰海高速', '高速公路', '斜拉桥', '双塔双索面斜拉桥', '200m+450m+200m', 856.00, 32.00, '公路-Ⅰ级', 'Ⅰ', '重庆高速集团', '2012-12-28')"
        };
        
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            for (String sql : sqls) {
                stmt.execute(sql);
            }
            Logger.info("SQLite数据库初始化完成");
        } catch (SQLException e) {
            Logger.error("SQLite数据库初始化失败", e);
        } finally {
            close(conn, stmt);
        }
    }
}
