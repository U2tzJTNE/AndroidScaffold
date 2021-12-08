package com.u2tzjtne.android.scaffold.thread;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * detail: 线程池管理工具类
 *
 * @author Ttt
 */
public final class ThreadManager {

    private ThreadManager() {
    }
    /**
     * 默认通用线程池 ( 通过 CPU 自动处理 )
     */
    private static final ThreadPoolManager DEV_THREAD_POOL = new ThreadPoolManager(ThreadPoolManager.DevThreadPoolType.CALC_CPU);
    /**
     * 线程池数据
     */
    private static final Map<String, ThreadPoolManager> THREAD_MAPS = new LinkedHashMap<>();
    /**
     * 配置数据
     */
    private static final Map<String, Object> CONFIG_MAPS = new HashMap<>();

    /**
     * 获取 ThreadManager 实例
     *
     * @param threadNumber 线程数量
     * @return {@link ThreadPoolManager}
     */
    public static synchronized ThreadPoolManager getInstance(final int threadNumber) {
        // 初始化 key
        String key = "n_" + threadNumber;
        // 如果不为 null, 则直接返回
        ThreadPoolManager threadPoolManager = THREAD_MAPS.get(key);
        if (threadPoolManager != null) {
            return threadPoolManager;
        }
        threadPoolManager = new ThreadPoolManager(threadNumber);
        THREAD_MAPS.put(key, threadPoolManager);
        return threadPoolManager;
    }

    /**
     * 获取 ThreadManager 实例
     *
     * @param key 线程配置 key {@link ThreadPoolManager.DevThreadPoolType} or int-Integer
     * @return {@link ThreadPoolManager}
     */
    public static synchronized ThreadPoolManager getInstance(final String key) {
        // 如果不为 null, 则直接返回
        ThreadPoolManager threadPoolManager = THREAD_MAPS.get(key);
        if (threadPoolManager != null) {
            return threadPoolManager;
        }
        Object object = CONFIG_MAPS.get(key);
        if (object != null) {
            try {
                // 判断是否属于线程池类型
                if (object instanceof ThreadPoolManager.DevThreadPoolType) {
                    threadPoolManager = new ThreadPoolManager((ThreadPoolManager.DevThreadPoolType) object);
                } else if (object instanceof Integer) {
                    threadPoolManager = new ThreadPoolManager((Integer) object);
                } else { // 其他类型, 统一转换 Integer
                    threadPoolManager = new ThreadPoolManager(Integer.parseInt((String) object));
                }
                THREAD_MAPS.put(key, threadPoolManager);
                return threadPoolManager;
            } catch (Exception e) {
                return DEV_THREAD_POOL;
            }
        }
        return DEV_THREAD_POOL;
    }

    /**
     * 初始化配置信息
     *
     * @param mapConfigs 线程配置信息 Map
     */
    public static void initConfig(final Map<String, Object> mapConfigs) {
        if (mapConfigs != null) {
            CONFIG_MAPS.putAll(mapConfigs);
        }
    }

    /**
     * 添加配置信息
     *
     * @param key   线程配置 key
     * @param value 线程配置 value
     */
    public static void putConfig(
            final String key,
            final Object value
    ) {
        CONFIG_MAPS.put(key, value);
    }

    /**
     * 移除配置信息
     *
     * @param key 线程配置 key
     */
    public static void removeConfig(final String key) {
        CONFIG_MAPS.remove(key);
    }
}