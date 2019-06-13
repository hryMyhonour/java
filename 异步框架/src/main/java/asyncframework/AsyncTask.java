/*
 * Copyright (C) 1994-2016 Microstar Electric Company Limited
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

import java.util.LinkedList;
import java.util.UUID;

public interface AsyncTask {

    /**
     * 任务在系统中的id，具有唯一性，默认使用{@link UUID UUID}}
     *
     * @return 任务id NonNull
     */
    default String getId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 任务接收到回复数据的处理器
     *
     * @return 数据入站处理器
     * @see DataInboundHandler
     */
    LinkedList<DataInboundHandler> getInboundHandlers();

    /**
     * 异常处理器
     *
     * @return 异常处理器
     * @see ExceptionHandler
     */
    LinkedList<ExceptionHandler<? extends Throwable>> getExceptionHandlers();

    /**
     * 设备任务被调度时的处理器
     *
     * @return 任务处理器 NonNull
     * @see TaskDispatchHandler
     */
    TaskDispatchHandler getTaskDispatchHandler();

    /**
     * 任务的执行配置，默认配置见AsyncTaskConfig
     *
     * @return 配置 NonNull
     * @see AsyncTaskConfig
     */
    default AsyncTaskConfig getConfig() {
        return new AsyncTaskConfig();
    }

    /**
     * 任务执行完成钩子，结束后任务将被删除
     *
     * @throws Exception 处理异常
     */
    default void taskFinishedHook() throws Exception {
    }
}
