package demo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {
    private final CacheAlgorithm algorithm;
    private final Map<Object, CacheObject> record = new ConcurrentHashMap<>();
    private final int limit;

    public CacheManager(CacheAlgorithm algorithm, int limit) {
        this.algorithm = algorithm;
        this.limit = limit;
    }

    /**
     * 新增缓存
     *
     * @param cacheObject 缓存
     */
    public void add(CacheObject cacheObject) {
        if (record.size() >= limit) {
            List<CacheObject> e = algorithm.eliminate(record.size() + 1 - limit);
            e.forEach(c -> record.remove(c.key()));
        }
        algorithm.insert(cacheObject);
        record.put(cacheObject.key(), cacheObject);
    }

    /**
     * 查询缓存
     *
     * @param key key
     * @return 缓存，如果不存在返回null
     */
    public CacheObject get(Object key) {
        CacheObject r = record.get(key);
        if (r != null) {
            algorithm.hit(r);
        }
        return r;
    }
}
