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

/**
 * 接受到异步任务的数据的处理器
 *
 * @param <T> 异步任务数据类型
 */
public interface DataInboundHandler<T> {

    /**
     * 读取数据
     *
     * @param taskCtx          任务上下文 NonNull
     * @param asyncEventHolder 异步事件容器 NonNull
     * @param message          读取到的信息 Nullable
     * @throws Exception 处理异常
     */
    void read(AsyncEventTaskContext taskCtx, AsyncEventHolder asyncEventHolder, T message) throws Exception;
}
