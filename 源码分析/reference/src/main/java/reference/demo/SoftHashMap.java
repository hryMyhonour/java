package reference.demo;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

/**
 * soft reference 适用于缓存一些非必须的数据
 */
public class SoftHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
    /**
     * The default initial capacity -- MUST be a power of two.
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    //null的key对引用来说存在歧义，这里用一个对象来代替null key
    private static final Object NULL_KEY = new Object();

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return null;
    }

    /**
     * map的Entry继承了SoftReference，key作为引用值
     *
     * @param <K> key 类型
     * @param <V> value 类型
     */
    private static class Entry<K, V> extends SoftReference<Object> implements Map.Entry<K, V> {
        private V val;
        private final int hash;
        private Entry<K, V> next;

        Entry(Object key, V val, ReferenceQueue<Object> queue, int hash, Entry<K, V> next) {
            super(key, queue);
            this.hash = hash;
            this.next = next;
            this.val = val;
        }

        @Override
        @SuppressWarnings("unchecked")
        public K getKey() {
            return (K) SoftHashMap.maskKey(get());
        }

        @Override
        public V getValue() {
            return val;
        }

        @Override
        public V setValue(V value) {
            V o = this.val;
            this.val = value;
            return o;
        }
    }

    private Entry<K, V>[] table;
    private int size;
    private int threshold;
    private final float loadFactor;

    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
    int modCount;

    public SoftHashMap(int initCapacity, float loadFactor) {
        this.loadFactor = loadFactor;
        int s = 1;
        while (s < initCapacity) {
            s <<= 1;
        }
        table = newTable(s);
        this.threshold = (int) (s * loadFactor);
    }

    @SuppressWarnings("unchecked")
    private Entry<K, V>[] newTable(int n) {
        return (Entry<K, V>[]) new Entry<?, ?>[n];
    }


    public SoftHashMap(int initCapacity) {
        this(initCapacity, DEFAULT_LOAD_FACTOR);
    }

    public SoftHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /**
     * 计算hash值，低16位=低16位按位与高16位，减少低位冲突
     *
     * @param key key值，not null
     * @return 对应的hash值
     */
    private int hash(Object key) {
        int code = key.hashCode();
        return code ^ code >>> 16;
    }

    /**
     * 处理null作为key的问题
     *
     * @param key 原key值
     * @return key
     */
    private static Object maskKey(Object key) {
        return key == null ? NULL_KEY : key;
    }

    /**
     * 程序内不直接获取table使用，而是通过方法获取，该方法会删除被回收的key后再返回table
     *
     * @return 更新后的table
     */
    private Entry<K, V>[] getTable() {
        expungeStaleEntries();
        return this.table;
    }

    @Override
    public V get(Object key) {
        Object k = maskKey(key);
        int hash = hash(k);
        Entry<K, V>[] table = getTable();
        int i = indexOf(hash, table.length);
        Entry<K, V> e = table[i];
        while (e != null && !eq(k, e.get())) {
            e = e.next;
        }
        return e == null ? null : e.val;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        V v = get(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public V put(K key, V value) {
        Object k = maskKey(key);
        int hash = hash(k);
        Entry<K, V>[] table = getTable();
        int i = indexOf(hash, table.length);
        // 替换值
        for (Entry<K, V> e = table[i]; e != null; e = e.next) {
            if (eq(k, e.get())) {
                V o = e.val;
                e.setValue(value);
                return o;
            }
        }
        //新增值
        modCount++;
        Entry<K, V> e = table[i];
        table[i] = new Entry<>(k, value, queue, hash, e);
        if (++size > threshold) {
            resize();
        }
        return null;
    }

    /**
     * 扩容
     */
    private void resize() {
        Entry<K, V>[] old = getTable();
        //去掉被回收的值后，阈值的一般大于size，不用扩容
        if (size < threshold / 2) {
            return;
        }
        int oldCapacity = old.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            this.threshold = Integer.MAX_VALUE;
            return;
        }
        //新的容量位原来的两倍
        int newCapacity = oldCapacity << 1;
        Entry<K, V>[] newTable = newTable(newCapacity);
        //复制旧的数据到新的数据表
        for (int i = 0; i < old.length; i++) {
            Entry<K, V> o = old[i];
            Entry<K, V> c = o;
            Entry<K, V> high = null;
            Entry<K, V> pre = c;
            while (c != null) {
                Object k = c.get();
                if (k == null){
                    c.val = null;
                }else{
                    if ((c.hash & oldCapacity) != 0) {
                        high = new Entry<>(k, c.val, queue, c.hash, high);
                        if (c == o) {
                            o = o.next;
                        } else {
                            pre.next = c.next;
                        }
                    }
                }
                pre = c;
                c = c.next;
            }
            newTable[i] = o;
            newTable[i + oldCapacity] = high;
        }
        table = newTable;
        threshold = (int) (newCapacity * loadFactor);
    }

    /**
     * 判断相同或者equals
     *
     * @param o1 not null
     * @param o2 nullable
     * @return 是否等同
     */
    private boolean eq(Object o1, Object o2) {
        return o1 == o2 || o1.equals(o2);
    }

    private int indexOf(int hash, int tableSize) {
        return hash & (tableSize - 1);
    }

    /**
     * 检查是否有被删除的key，有则删除对应的entry，该方法不会修改modCount
     */
    private void expungeStaleEntries() {
        for (Object o; (o = queue.poll()) != null; ) {
            synchronized (queue) {
                @SuppressWarnings("unchecked")
                Entry<K, V> e = (Entry<K, V>) o;
                int i = indexOf(e.hash, table.length);

                Entry<K, V> pre = table[i];
                Entry<K, V> cur = pre;
                //冲突是用链表来存储的，找到需要移除的entry在链表中的位置，然后断键
                while (cur != null) {
                    Entry<K, V> next = cur.next;
                    //找到entry
                    if (e == cur) {
                        //说明是链表头
                        if (pre == e) {
                            table[i] = next;
                        } else {
                            //断健
                            pre.next = next;
                        }
                        e.val = null;
                        size--;
                        break;
                    }
                    pre = cur;
                    cur = next;
                }
            }
        }
    }
}
