package asyncframework;

import lombok.Data;

@Data
public class AsyncTaskConfig {
    public static final int DEFAULT_WAIT_RESPONSE_TIMEOUT_MS = 3 * 60 * 1000;
    private int waitResponseTimeoutMs = DEFAULT_WAIT_RESPONSE_TIMEOUT_MS;
}
