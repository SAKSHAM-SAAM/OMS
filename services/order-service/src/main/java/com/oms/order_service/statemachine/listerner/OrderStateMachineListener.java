package com.oms.order_service.statemachine.listerner;

import com.oms.order_service.domain.enums.OrderEvent;
import com.oms.order_service.domain.enums.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

/**
 * Listens to state machine events — purely for logging now.
 * In Phase 8 we'll hook metrics here.
 */
@Slf4j
public class OrderStateMachineListener
        extends StateMachineListenerAdapter<OrderStatus, OrderEvent> {

    @Override
    public void stateChanged(
            State<OrderStatus, OrderEvent> from,
            State<OrderStatus, OrderEvent> to) {
        if (from != null) {
            log.info("Order state transition: {} → {}",
                    from.getId(), to.getId());
        }
    }

    @Override
    public void transitionStarted(Transition<OrderStatus, OrderEvent> transition) {
        if(transition.getSource() != null && transition.getSource().getId() != null){
            log.debug("Transition started: {} → {}",
                    transition.getSource().getId(),
                    transition.getTarget().getId());
        }
    }
}