package com.oms.order_service.statemachine.config;
import com.oms.order_service.statemachine.listerner.*;
import com.oms.order_service.domain.enums.OrderEvent;
import com.oms.order_service.domain.enums.OrderStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.*;
import org.springframework.statemachine.config.builders.*;

/**
 * Defines ALL valid state transitions.
 *
 * Think of this as a flowchart enforced in code:
 *   PENDING --[CAPTURE_PAYMENT]--> PAYMENT_CAPTURED
 *   PAYMENT_CAPTURED --[RESERVE_INVENTORY]--> INVENTORY_RESERVED
 *   ... and so on
 *
 * Any transition NOT defined here is ILLEGAL and will be rejected.
 */
@Slf4j
@Configuration
@EnableStateMachine
public class OrderStateMachineConfig
        extends StateMachineConfigurerAdapter<OrderStatus, OrderEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderEvent> states)
            throws Exception {
        states
            .withStates()
                .initial(OrderStatus.PENDING)
                .states(Set.of(OrderStatus.values()))
                .end(OrderStatus.DELIVERED)
                .end(OrderStatus.REFUNDED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderEvent> transitions)
            throws Exception {
        transitions
            // Normal happy path
            .withExternal()
                .source(OrderStatus.PENDING)
                .target(OrderStatus.PAYMENT_CAPTURED)
                .event(OrderEvent.CAPTURE_PAYMENT)
                .and()
            .withExternal()
                .source(OrderStatus.PAYMENT_CAPTURED)
                .target(OrderStatus.INVENTORY_RESERVED)
                .event(OrderEvent.RESERVE_INVENTORY)
                .and()
            .withExternal()
                .source(OrderStatus.INVENTORY_RESERVED)
                .target(OrderStatus.PROCESSING)
                .event(OrderEvent.START_PROCESSING)
                .and()
            .withExternal()
                .source(OrderStatus.PROCESSING)
                .target(OrderStatus.SHIPPED)
                .event(OrderEvent.SHIP_ORDER)
                .and()
            .withExternal()
                .source(OrderStatus.SHIPPED)
                .target(OrderStatus.DELIVERED)
                .event(OrderEvent.CONFIRM_DELIVERY)
                .and()
            // Cancellation — allowed from early states only
            .withExternal()
                .source(OrderStatus.PENDING)
                .target(OrderStatus.CANCELLED)
                .event(OrderEvent.CANCEL_ORDER)
                .and()
            .withExternal()
                .source(OrderStatus.PAYMENT_CAPTURED)
                .target(OrderStatus.CANCELLED)
                .event(OrderEvent.CANCEL_ORDER)
                .and()
            .withExternal()
                .source(OrderStatus.INVENTORY_RESERVED)
                .target(OrderStatus.CANCELLED)
                .event(OrderEvent.CANCEL_ORDER)
                .and()
            // Refund — only from CANCELLED
            .withExternal()
                .source(OrderStatus.CANCELLED)
                .target(OrderStatus.REFUNDED)
                .event(OrderEvent.REFUND_ORDER);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStatus, OrderEvent> config)
            throws Exception {
        config
            .withConfiguration()
                .autoStartup(true)
                .listener(new OrderStateMachineListener());
    }
}