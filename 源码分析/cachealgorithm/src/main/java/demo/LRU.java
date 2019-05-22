package demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class LRU implements CacheAlgorithm {
    private LinkedHashMap<Object, CacheObject> cacheObjectLinkedHashMap = new LinkedHashMap<>();

    @Override
    public void insert(CacheObject cacheObject) {
        cacheObjectLinkedHashMap.put(cacheObject.key(), cacheObject);
    }

    @Override
    public void hit(CacheObject cacheObject) {
        cacheObjectLinkedHashMap.remove(cacheObject.key());
        cacheObjectLinkedHashMap.put(cacheObject.key(), cacheObject);
    }

    @Override
    public List<CacheObject> eliminate(int amount) {
        Iterator<CacheObject> itr = cacheObjectLinkedHashMap.values().iterator();
        List<CacheObject> r = new ArrayList<>(amount);
        while (amount > 0 && itr.hasNext()) {
            CacheObject o = itr.next();
            r.add(o);
            cacheObjectLinkedHashMap.remove(o.key());
            amount--;
        }
        return r;
    }
}
