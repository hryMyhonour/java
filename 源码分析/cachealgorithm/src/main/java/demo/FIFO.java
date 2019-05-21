package demo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FIFO implements CacheAlgorithm {
    private Queue<CacheObject> cacheObjectQueue = new LinkedList<>();

    @Override
    public void insert(CacheObject cacheObject) {
        cacheObjectQueue.offer(cacheObject);
    }

    @Override
    public void hit(CacheObject cacheObject) {
        // do nothing
    }

    @Override
    public List<CacheObject> eliminate(int amount) {
        List<CacheObject> r = new ArrayList<>(amount);
        while (amount != 0 && !cacheObjectQueue.isEmpty()){
            r.add(cacheObjectQueue.poll());
            amount--;
        }
        return r;
    }
}
