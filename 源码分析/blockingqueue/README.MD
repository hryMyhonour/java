# BlockingQueue特性
## ArrayBlockingQueue
### 特性
1. 底层用数组保存队列的数据。
2. 初始化的时候必须指定数组容量。
3. 使用一个锁来同步take\put操作，可以在初始化的时候设置为公平锁。

使用两个下标takeIndex\putIndex维护take\put方法的执行。dequeue\enqueue时当下标到数组末尾时，会将下标重置为0，组成一个环形结构，操作过程中不会生成新的对象。
由于基于数组结构，本身set\get速度已经足够快，所以为了实现逻辑的简单只用了一个锁来同步take\put操作，所以take\put操作是互斥的，但是由于执行的时间很短，可以认为两个操作不会相互阻塞。如果需要改成两个锁分别同步take\put操作，需要考虑takeIndex\putIndex相同的情况，要处理锁通信的问题，逻辑会变得很复杂。
## LinkedBlockingQueue
### 特性
1. 底层用链表保存队列的数据。
2. 最大长度为Integer.Max。
3. 使用两个锁同步take\put操作，两个操作可以同时进行。

队列保存链表的head用于take，保存链表的last用于put，操作过程中需要new一个链表节点来存储数据，考虑到节点生成和链表建立会用一定的时间，所以用了两个锁分别同步take\put操作，但这样就不支持公平锁。

## Syn