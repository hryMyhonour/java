package asyncframework;


import lombok.NonNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AsyncEventHolder {
    private AsyncEvent lastEvent;
    private LinkedList<AsyncEvent> history = new LinkedList<>();

    /**
     * 添加异步事件
     *
     * @param event NonNull
     */
    public synchronized void addResponse(@NonNull AsyncEvent event) {
        history.add(event);
        lastEvent = event;
    }

    /**
     * 获取最后一个的异步事件
     *
     * @return 最后一个异步事件
     * @throws NullPointerException 如果没有异步事件报空指针
     */
    public synchronized @NonNull AsyncEvent getLastEvent() throws NullPointerException {
        if (lastEvent == null) {
            throw new NullPointerException("The last event is null");
        }
        return lastEvent;
    }

    /**
     * 获取历史异步事件列表，数值的顺序和插入异步事件的顺序一致，列表不可变。
     *
     * @return 历史异步事件
     */
    public synchronized List<AsyncEvent> getHistory() {
        return Collections.unmodifiableList(history);
    }
}
