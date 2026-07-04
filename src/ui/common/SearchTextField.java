package ui.common;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.concurrent.*;

/**
 * 带防抖功能的搜索文本框
 * 用户停止输入指定毫秒后才触发搜索回调，避免频繁查询数据库
 */
public class SearchTextField extends JTextField {

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "search-debounce");
        t.setDaemon(true);
        return t;
    });

    private ScheduledFuture<?> pendingTask;
    private long debounceMs = 400;
    private SearchCallback callback;

    public SearchTextField(int columns) {
        super(columns);
        init();
    }

    public SearchTextField() {
        super();
        init();
    }

    private void init() {
        setFont(new Font("微软雅黑", Font.PLAIN, 13));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { onChange(); }
            @Override
            public void removeUpdate(DocumentEvent e) { onChange(); }
            @Override
            public void changedUpdate(DocumentEvent e) { onChange(); }
        });
    }

    private void onChange() {
        if (callback == null) return;

        // 取消上一个待执行任务
        if (pendingTask != null && !pendingTask.isDone()) {
            pendingTask.cancel(false);
        }

        // 延迟执行搜索
        pendingTask = scheduler.schedule(() -> {
            SwingUtilities.invokeLater(() -> callback.onSearch(getText().trim()));
        }, debounceMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 设置防抖间隔，默认 400ms
     */
    public void setDebounceMs(long debounceMs) {
        this.debounceMs = debounceMs;
    }

    /**
     * 设置搜索回调
     */
    public void setSearchCallback(SearchCallback callback) {
        this.callback = callback;
    }

    public interface SearchCallback {
        void onSearch(String keyword);
    }
}
