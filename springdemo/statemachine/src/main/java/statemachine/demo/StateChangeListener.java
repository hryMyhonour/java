package statemachine.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;

@WithStateMachine
@Slf4j
public class StateChangeListener {

    @OnTransition(target = "PROCESSING")
    public void star() {
        log.info("Star to handling");
    }

    @OnTransition(target = "WAITING")
    public void init() {
        log.info("star state machine");
    }

    @OnTransition(target = "EXCEPTION")
    public void exception() {
        log.info("throw exception");
    }

    @OnTransition(target = "COMPLETE")
    public void end() {
        log.info("completed");
    }
}
