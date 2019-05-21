package demo;

import java.util.List;

public interface CacheAlgorithm {

    /**
     * 新插入缓存
     *
     * @param cacheObject 缓存对象
     */
    void insert(CacheObject cacheObject);

    /**
     * 使用命中了缓存
     *
     * @param cacheObject 缓存对象
     */
    void hit(CacheObject cacheObject);

    /**
     * 申请释放缓存
     *
     * @param amount 释放的数量
     * @return 释放的对象
     */
    List<CacheObject> eliminate(int amount);
}
