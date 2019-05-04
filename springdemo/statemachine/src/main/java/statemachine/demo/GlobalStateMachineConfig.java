package statemachine.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import statemachine.demo.constants.Event;
import statemachine.demo.constants.State;

import java.util.EnumSet;

@Configuration
//单例模式
@EnableStateMachine
//工程模式
//@EnableStateMachineFactory
public class GlobalStateMachineConfig extends EnumStateMachineConfigurerAdapter<State, Event> {
    @Override
    public void configure(StateMachineStateConfigurer<State, Event> states) throws Exception {
        states
                .withStates()
                .initial(State.WAITING)
                .states(EnumSet.allOf(State.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
        transitions
                .withExternal()
                .source(State.WAITING).target(State.PROCESSING).event(Event.ACCEPT)
                .and()
                .withExternal()
                .source(State.PROCESSING).target(State.EXCEPTION).event(Event.THROW_EXCEPTION)
                .and()
                .withExternal()
                .source(State.EXCEPTION).target(State.PROCESSING).event(Event.ACCEPT)
                .and()
                .withExternal()
                .source(State.PROCESSING).target(State.COMPLETE).event(Event.DONE);
    }
}
