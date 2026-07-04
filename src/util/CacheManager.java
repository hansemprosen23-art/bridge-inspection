package util;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 通用内存缓存管理器
 * 用于缓存不频繁变化的数据，如桥梁列表、字典数据等
 * 支持过期时间、最大容量限制
 */
public class CacheManager<K, V> {

    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long defaultTtlMs;
    private final int maxSize;

    public CacheManager(long defaultTtlMs, int maxSize) {
        this.defaultTtlMs = defaultTtlMs;
        this.maxSize = maxSize;

        // 启动定时清理线程
        ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "cache-cleaner");
            t.setDaemon(true);
            return t;
        });
        cleaner.scheduleAtFixedRate(this::cleanup, defaultTtlMs, defaultTtlMs, TimeUnit.MILLISECONDS);
    }

    public void put(K key, V value) {
        put(key, value, defaultTtlMs);
    }

    public void put(K key, V value, long ttlMs) {
        if (cache.size() >= maxSize && !cache.containsKey(key)) {
            // 简单淘汰策略：移除最早放入的
            K oldest = cache.entrySet().stream()
                    .min(Map.Entry.comparingByValue((a, b) -> Long.compare(a.timestamp, b.timestamp)))
                    .map(Map.Entry::getKey).orElse(null);
            if (oldest != null) cache.remove(oldest);
        }
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttlMs, System.currentTimeMillis()));
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) return null;
        if (System.currentTimeMillis() > entry.expireAt) {
            cache.remove(key);
            return null;
        }
        return entry.value;
    }

    public void invalidate(K key) {
        cache.remove(key);
    }

    public void invalidateAll() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }

    private void cleanup() {
        long now = System.currentTimeMillis();
        cache.entrySet().removeIf(entry -> now > entry.getValue().expireAt);
    }

    private static class CacheEntry<V> {
        V value;
        long expireAt;
        long timestamp;

        CacheEntry(V value, long expireAt, long timestamp) {
            this.value = value;
            this.expireAt = expireAt;
            this.timestamp = timestamp;
        }
    }
}
