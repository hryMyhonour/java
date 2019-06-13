package asyncframework;

import lombok.NonNull;

public class AsyncEventTaskContext extends AsyncTaskContext {
    AsyncEvent asyncEvents;

    AsyncEventTaskContext(AsyncTask asyncTask) {
        super(asyncTask);
    }

    AsyncEventTaskContext(RuntimeContext runtimeContext) {
        super(runtimeContext);
    }

    public AsyncEvent getAsyncEvents() {
        return asyncEvents;
    }

    public void setAsyncEvents(@NonNull AsyncEvent asyncEvents) {
        this.asyncEvents = asyncEvents;
    }
}
