package asyncframework;

import lombok.Getter;

import java.util.Date;

@Getter
public abstract class AsyncEvent {
    private Object data;
    private Date receiveTime;
    private Date startTime;

    /**
     * 异步事件的逻辑
     *
     * @return 异步事件返回的结果
     * @throws Exception 执行异常
     */
    public abstract Object run() throws Exception;

    void run0() throws Exception {
        this.startTime = new Date();
        this.data = this.run();
        this.receiveTime = new Date();
    }
}
