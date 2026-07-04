package ui.common;

import javax.swing.*;
import java.awt.*;

/**
 * 通用分页控制面板
 * 提供 首页/上一页/页码/下一页/尾页 和 每页条数选择
 */
public class PaginationPanel extends JPanel {

    private int currentPage = 1;
    private int pageSize = 20;
    private int totalCount = 0;
    private int totalPages = 1;

    private final JLabel pageInfoLabel;
    private final RoundedButton firstBtn, prevBtn, nextBtn, lastBtn;
    private final JComboBox<Integer> pageSizeBox;
    private PageChangeListener listener;

    public PaginationPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        setOpaque(false);

        firstBtn = new RoundedButton("首页", ThemeColors.INFO);
        prevBtn = new RoundedButton("上一页", ThemeColors.INFO);
        nextBtn = new RoundedButton("下一页", ThemeColors.INFO);
        lastBtn = new RoundedButton("尾页", ThemeColors.INFO);

        pageInfoLabel = new JLabel("第 1 / 1 页，共 0 条");
        pageInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        pageInfoLabel.setForeground(ThemeColors.TEXT_SECONDARY);

        pageSizeBox = new JComboBox<>(new Integer[]{10, 20, 50, 100});
        pageSizeBox.setSelectedItem(20);
        pageSizeBox.addActionListener(e -> {
            pageSize = (Integer) pageSizeBox.getSelectedItem();
            currentPage = 1;
            updateButtons();
            firePageChange();
        });

        firstBtn.addActionListener(e -> { currentPage = 1; updateButtons(); firePageChange(); });
        prevBtn.addActionListener(e -> { if (currentPage > 1) { currentPage--; updateButtons(); firePageChange(); } });
        nextBtn.addActionListener(e -> { if (currentPage < totalPages) { currentPage++; updateButtons(); firePageChange(); } });
        lastBtn.addActionListener(e -> { currentPage = totalPages; updateButtons(); firePageChange(); });

        add(firstBtn);
        add(prevBtn);
        add(pageInfoLabel);
        add(nextBtn);
        add(lastBtn);
        add(new JLabel("每页"));
        add(pageSizeBox);
        add(new JLabel("条"));

        updateButtons();
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        this.totalPages = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / pageSize);
        if (currentPage > totalPages) currentPage = totalPages;
        if (currentPage < 1) currentPage = 1;
        updateButtons();
    }

    public void setPageChangeListener(PageChangeListener listener) {
        this.listener = listener;
    }

    private void firePageChange() {
        if (listener != null) {
            listener.onPageChange(currentPage, pageSize);
        }
    }

    private void updateButtons() {
        pageInfoLabel.setText(String.format("第 %d / %d 页，共 %d 条", currentPage, totalPages, totalCount));
        firstBtn.setEnabled(currentPage > 1);
        prevBtn.setEnabled(currentPage > 1);
        nextBtn.setEnabled(currentPage < totalPages);
        lastBtn.setEnabled(currentPage < totalPages);
    }

    public int getCurrentPage() { return currentPage; }
    public int getPageSize() { return pageSize; }
    public int getTotalCount() { return totalCount; }

    public interface PageChangeListener {
        void onPageChange(int page, int pageSize);
    }
}
