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

import lombok.extern.slf4j.Slf4j;

@Slf4j
class RuntimeContext extends DispatchAsyncTaskContext {
    final AsyncEventHolder asyncEventHolder = new AsyncEventHolder();

    RuntimeContext(AsyncTask asyncTask) {
        super(asyncTask);
    }
}
