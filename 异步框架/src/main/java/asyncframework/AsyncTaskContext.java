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

import lombok.Getter;

@Getter
public class AsyncTaskContext {
    private AsyncTaskStatus status = AsyncTaskStatus.IDLE;
    final AsyncTask asyncTask;
    boolean cancelTask = false;

    AsyncTaskContext(AsyncTaskContext copy) {
        this.asyncTask = copy.asyncTask;
        this.status = copy.status;
    }

    AsyncTaskContext(AsyncTask asyncTask) {
        this.asyncTask = asyncTask;
    }

    void setStatus(AsyncTaskStatus status) {
        if (this.status.accept.contains(status)) {
            this.status = status;
        }
    }

    public void setCancelTask(boolean cancelTask) {
        this.cancelTask = cancelTask;
    }
}
