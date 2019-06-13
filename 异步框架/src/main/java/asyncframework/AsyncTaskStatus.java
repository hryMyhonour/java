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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 异步任务的状态
 */
public enum AsyncTaskStatus {
    /**
     * 任务等待被调度
     */
    IDLE(AsyncTaskStatus.DISPATCHING, AsyncTaskStatus.CANCEL, AsyncTaskStatus.REPLACE, AsyncTaskStatus.ERROR),
    /**
     * 任务正在调度中，即正在被TaskDispatchHandler处理
     */
    DISPATCHING(AsyncTaskStatus.DISPATCHING, AsyncTaskStatus.WAITING_RESPONSE, AsyncTaskStatus.CANCEL, AsyncTaskStatus.REPLACE, AsyncTaskStatus.ERROR),
    /**
     * 发出了异步任务，需要等待数据返回
     */
    WAITING_RESPONSE(AsyncTaskStatus.RECEIVE_RESPONSE, AsyncTaskStatus.CANCEL, AsyncTaskStatus.REPLACE, AsyncTaskStatus.ERROR),
    /**
     * 接受到异步任务返回的数据
     */
    RECEIVE_RESPONSE(AsyncTaskStatus.RECEIVE_RESPONSE, AsyncTaskStatus.FINISH, AsyncTaskStatus.CANCEL, AsyncTaskStatus.REPLACE, AsyncTaskStatus.ERROR),
    /**
     * 取消任务
     */
    CANCEL,
    /**
     * 被强制替换
     */
    REPLACE,
    /**
     * 完成
     */
    FINISH,
    /**
     * 任务过程中报错
     */
    ERROR,
    ;
    List<AsyncTaskStatus> accept = new ArrayList<>();

    AsyncTaskStatus(AsyncTaskStatus... accept) {
        this.accept.addAll(Arrays.asList(accept));
    }}
