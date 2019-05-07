package statemachine.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import statemachine.demo.constants.Event;
import statemachine.demo.constants.State;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
//单例模式
@EnableStateMachine
//工程模式
//@EnableStateMachineFactory
@Slf4j
public class GlobalStateMachineConfig extends EnumStateMachineConfigurerAdapter<State, Event> {

    private final static Map<Integer, StateMachineContext<State, Event>> store = new ConcurrentHashMap<>();

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

    @Bean
    public StateMachinePersister<State, Event, Integer> persister() {
        return new DefaultStateMachinePersister<>(new StateMachinePersist<State, Event, Integer>() {
            @Override
            public void write(StateMachineContext<State, Event> context, Integer contextObj) throws Exception {
                log.info("write id: {}, state: {}, event:{}", contextObj, context.getState(), context.getEvent());
                store.put(contextObj, context);
            }

            @Override
            public StateMachineContext<State, Event> read(Integer contextObj) throws Exception {
                log.info("read id: {}", contextObj);
                return store.get(contextObj);
            }
        });
    }
}
