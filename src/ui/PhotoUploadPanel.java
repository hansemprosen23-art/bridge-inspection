package ui;

import ui.common.RoundedButton;
import ui.common.ThemeColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import javax.imageio.ImageIO;

/**
 * 照片上传面板
 * 支持选择照片、预览、保存到项目目录
 */
public class PhotoUploadPanel extends JPanel {

    private JLabel frontLabel, leftLabel, rightLabel;
    private String frontPath, leftPath, rightPath;
    private static final String PHOTO_DIR = "photos";
    private static final int PREVIEW_WIDTH = 180;
    private static final int PREVIEW_HEIGHT = 120;

    public PhotoUploadPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        setOpaque(false);

        // 确保照片目录存在
        File dir = new File(PHOTO_DIR);
        if (!dir.exists()) dir.mkdirs();

        frontLabel = createPhotoLabel("正面照片");
        leftLabel = createPhotoLabel("左侧照片");
        rightLabel = createPhotoLabel("右侧照片");

        add(createPhotoCard("正面", frontLabel));
        add(createPhotoCard("左侧", leftLabel));
        add(createPhotoCard("右侧", rightLabel));
    }

    private JPanel createPhotoCard(String title, JLabel label) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(PREVIEW_WIDTH + 20, PREVIEW_HEIGHT + 50));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        titleLabel.setForeground(ThemeColors.TEXT_SECONDARY);
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(label, BorderLayout.CENTER);

        return card;
    }

    private JLabel createPhotoLabel(String defaultText) {
        JLabel label = new JLabel(defaultText, SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT));
        label.setBorder(BorderFactory.createLineBorder(ThemeColors.BORDER));
        label.setBackground(new Color(245, 245, 245));
        label.setOpaque(true);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        label.setForeground(ThemeColors.TEXT_SECONDARY);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                choosePhoto(label);
            }
        });

        return label;
    }

    private void choosePhoto(JLabel targetLabel) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "图片文件 (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            // 检查文件大小（限制 5MB）
            if (file.length() > 5 * 1024 * 1024) {
                JOptionPane.showMessageDialog(this, "图片大小不能超过 5MB！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    Image scaled = img.getScaledInstance(PREVIEW_WIDTH, PREVIEW_HEIGHT, Image.SCALE_SMOOTH);
                    targetLabel.setIcon(new ImageIcon(scaled));
                    targetLabel.setText("");

                    // 保存路径
                    String savedPath = savePhoto(file);
                    if (targetLabel == frontLabel) frontPath = savedPath;
                    else if (targetLabel == leftLabel) leftPath = savedPath;
                    else if (targetLabel == rightLabel) rightPath = savedPath;
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "图片加载失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 保存照片到项目目录
     */
    private String savePhoto(File sourceFile) {
        try {
            String ext = getExtension(sourceFile.getName());
            String fileName = System.currentTimeMillis() + "_" + (int)(Math.random() * 10000) + "." + ext;
            Path target = Paths.get(PHOTO_DIR, fileName);
            Files.copy(sourceFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toAbsolutePath().toString();
        } catch (IOException e) {
            // 保存失败，返回原路径
            return sourceFile.getAbsolutePath();
        }
    }

    private String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(dot + 1) : "jpg";
    }

    /**
     * 加载已有照片
     */
    public void loadPhotos(String front, String left, String right) {
        loadPhoto(frontLabel, front);
        loadPhoto(leftLabel, left);
        loadPhoto(rightLabel, right);
        this.frontPath = front;
        this.leftPath = left;
        this.rightPath = right;
    }

    private void loadPhoto(JLabel label, String path) {
        if (path == null || path.trim().isEmpty()) return;
        try {
            File file = new File(path);
            if (!file.exists()) return;
            BufferedImage img = ImageIO.read(file);
            if (img != null) {
                Image scaled = img.getScaledInstance(PREVIEW_WIDTH, PREVIEW_HEIGHT, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaled));
                label.setText("");
            }
        } catch (IOException e) {
            // 忽略加载失败
        }
    }

    public String getFrontPath() { return frontPath; }
    public String getLeftPath() { return leftPath; }
    public String getRightPath() { return rightPath; }

    public void clear() {
        frontLabel.setIcon(null);
        frontLabel.setText("正面照片");
        leftLabel.setIcon(null);
        leftLabel.setText("左侧照片");
        rightLabel.setIcon(null);
        rightLabel.setText("右侧照片");
        frontPath = null;
        leftPath = null;
        rightPath = null;
    }
}
