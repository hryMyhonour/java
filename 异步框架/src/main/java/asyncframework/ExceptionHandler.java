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


import java.lang.reflect.ParameterizedType;

/**
 * 异常处理
 *
 * @param <T> 异常类型
 */
public abstract class ExceptionHandler<T extends Exception> {

    /**
     * 处理异常
     *
     * @param taskCtx   任务上下文 NonNull
     * @param exception 捕获的异常 NonNull
     * @throws Exception 抛出异常停止任务逻辑
     */
    public abstract void catching(AsyncTaskContext taskCtx, T exception) throws Exception;

    @SuppressWarnings({"unchecked"})
    void filterAndExecute(Exception exception, AsyncTaskContext taskCtx) throws Exception {
        final Class<?> t = (Class<?>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (!t.isAssignableFrom(exception.getClass())) {
            this.catching(taskCtx, (T) exception);
        }
    }
}
