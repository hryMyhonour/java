/*
 * Copyright (C) 1994-2018 Microstar Electric Company Limited
 *
 * All Rights Reserved.
 *
 * LEGAL NOTICE: All information contained herein is, and
 * remains the property of Microstar Electric Company Limited.
 * The intellectual and technical concepts contained herein
 * are proprietary to Microstar Electric Company Limited, and
 * may be covered by patents, patents in process and are
 * protected by the trade secret or copyright laws. Commercial
 * use, or disclosure, or dissemination, or reproduction of
 * the information contained in this file are strictly
 * forbidden unless official specific written permissions are
 * obtained from Microstar Electric Company Limited.
 */
package asyncframework;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

@Slf4j
public final class AsyncTaskPublisher {

    private class TaskHolder {
        final AsyncTask task;
        final RuntimeContext context;

        TaskHolder(AsyncTask task) {
            this.task = task;
            this.context = new RuntimeContext(task);
        }

        void withException(Exception e) {
            try {
                AsyncTaskContext ctx = new AsyncTaskContext(context);
                for (ExceptionHandler handler : task.getExceptionHandlers()) {
                    handler.filterAndExecute(e, ctx);
                }
            } catch (Exception exception) {
                context.setStatus(AsyncTaskStatus.ERROR);
            }
        }

        @SuppressWarnings({"unchecked"})
        void run() {
            log.debug("task {} status is {}", task.getId(), context.getStatus());
            try {
                switch (context.getStatus()) {
                    case IDLE:
                        context.setStatus(AsyncTaskStatus.DISPATCHING);
                        TaskDispatchHandler dispatchHandler = task.getTaskDispatchHandler();
                        DispatchAsyncTaskContext dc = new DispatchAsyncTaskContext(context);
                        dispatchHandler.dispatch(dc);
                        if (dc.keepDispatchingStatus) {
                            log.debug("task {} keep {} status", task.getId(), AsyncTaskStatus.DISPATCHING);
                        } else if (dc.cancelTask) {
                            log.debug("task {} cancel", task.getId());
                            context.setStatus(AsyncTaskStatus.CANCEL);
                        } else {
                            log.debug("task {} execute async event", task.getId());
                            assert dc.asyncEvents != null;
                            context.setStatus(AsyncTaskStatus.WAITING_RESPONSE);
                            runAsyncEvent(dc.asyncEvents);
                        }
                        break;
                    case WAITING_RESPONSE:
                        Date startTime = context.asyncEventHolder.getLastEvent().getStartTime();
                        if (startTime == null) {
                            log.warn("take {} async event start time is null", task.getId());
                            break;
                        }
                        int timeoutMs = task.getConfig().getWaitResponseTimeoutMs();
                        if (startTime.getTime() + timeoutMs > System.currentTimeMillis()) {
                            throw new TimeoutException();
                        }
                        break;
                    case RECEIVE_RESPONSE:
                        for (DataInboundHandler<Object> handler : task.getInboundHandlers()) {
                            final Class<?> dataClass = getFirstGenericArgument(handler.getClass());
                            if (dataClass == null) {
                                log.warn("DataInboundHandler {} has not generic argument", task.getId(), handler.getClass().getName());
                                continue;
                            }
                            Object data = context.asyncEventHolder.getLastEvent().getData();
                            AsyncEventTaskContext ac = new AsyncEventTaskContext(context);
                            if (data.getClass().isAssignableFrom(dataClass)) {
                                handler.read(ac, context.asyncEventHolder, data);
                            }
                            runAsyncEvent(ac.asyncEvents);
                        }
                        break;
                    case ERROR:
                        tasks.remove(task.getId());
                        break;
                    case FINISH:
                        task.taskFinishedHook();
                        tasks.remove(task.getId());
                        break;
                    case CANCEL:
                        tasks.remove(task.getId());
                        break;
                    case REPLACE:
                        throw new ForceReplacedException();
                    default:
                        log.warn("Unknown status: {}", context.getStatus());
                }
            } catch (Exception e) {
                withException(e);
            }
        }

        private void runAsyncEvent(AsyncEvent event) {
            if (event == null) {
                return;
            }
            context.setStatus(AsyncTaskStatus.WAITING_RESPONSE);
            runTaskPool.submit(() -> {
                try {
                    event.run0();
                    context.setStatus(AsyncTaskStatus.RECEIVE_RESPONSE);
                } catch (Exception e) {
                    withException(e);
                }
            });
        }

        private Class<?> getFirstGenericArgument(Class<?> interfaceClass) {
            if (interfaceClass.isInterface()) {
                return (Class<?>) ((ParameterizedType) interfaceClass.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            }
            return null;
        }
    }

    private final Map<String, TaskHolder> tasks = new ConcurrentHashMap<>();

    private final ExecutorService runTaskPool;

    public AsyncTaskPublisher(ExecutorService threadPool) {
        assert threadPool != null;
        runTaskPool = threadPool;
        new Thread(this::run).start();
    }

    private void run() {
        log.info("Async device task publisher start.");
        while (true) {
            for (TaskHolder holder : tasks.values()) {
                try {
                    holder.run();
                } catch (Exception e) {
                    log.error("Execute holder of task {} exception", holder.task.getId(), e);
                }
            }
        }
    }

    /**
     * 发布异步任务
     *
     * @param task  异步任务
     * @param force 强制替换已有的任务
     * @return 是否将任务加入到队列中
     */
    public boolean publish(@NonNull AsyncTask task, boolean force) {
        assert task != null;
        boolean r = force;
        if (force) {
            TaskHolder pre = tasks.put(task.getId(), new TaskHolder(task));
            if (pre != null) {
                pre.context.setStatus(AsyncTaskStatus.REPLACE);
            }
        } else {
            r = tasks.putIfAbsent(task.getId(), new TaskHolder(task)) == null;
        }
        return r;
    }

}
