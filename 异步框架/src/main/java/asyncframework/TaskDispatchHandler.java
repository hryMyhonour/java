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


public interface TaskDispatchHandler {

    /**
     * 执行分发逻辑
     *
     * @param taskCtx 任务上下文 NonNull
     * @throws Exception Any exception
     */
    void dispatch(DispatchAsyncTaskContext taskCtx) throws Exception;
}
