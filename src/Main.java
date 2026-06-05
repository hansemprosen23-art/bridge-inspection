import ui.LoginFrame;
import ui.common.ThemeColors;

import javax.swing.*;
import java.awt.*;

/**
 * 系统启动类
 */
public class Main {
    
    public static void main(String[] args) {
        // 设置现代外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 全局UI自定义
        UIManager.put("Panel.background", ThemeColors.BACKGROUND);
        UIManager.put("TextField.font", new Font("微软雅黑", Font.PLAIN, 13));
        UIManager.put("TextArea.font", new Font("微软雅黑", Font.PLAIN, 13));
        UIManager.put("ComboBox.font", new Font("微软雅黑", Font.PLAIN, 13));
        UIManager.put("Label.font", new Font("微软雅黑", Font.PLAIN, 13));
        UIManager.put("TitledBorder.font", new Font("微软雅黑", Font.BOLD, 14));
        
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
