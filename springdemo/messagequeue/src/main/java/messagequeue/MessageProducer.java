package messagequeue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import java.util.Timer;
import java.util.TimerTask;

@Component
@Slf4j
public class MessageProducer {
    @Autowired
    private JmsTemplate jmsTemplate;

    public void produce() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                jmsTemplate.send("testPool", session -> {
                    MessageBean b = new MessageBean();
                    log.info("Send {}", b);
                    return session.createObjectMessage(b);
                });
            }
        }, 0, 10000);
    }
}
