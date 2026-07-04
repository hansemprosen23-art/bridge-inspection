package ui.common;

/**
 * 可刷新面板接口
 * 主界面使用 CardLayout 缓存面板实例，切换时调用 refreshDataIfVisible 异步加载数据
 */
public interface RefreshablePanel {

    /**
     * 面板变为可见时调用，应使用 SwingWorker 异步加载数据，避免阻塞 EDT
     */
    void refreshDataIfVisible();

    /**
     * 设置面板是否处于忙状态（显示/隐藏加载遮罩）
     */
    void setBusy(boolean busy);
}
