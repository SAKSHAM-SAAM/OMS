package main.java.com.oms.order_service.domain.repository;

import com.oms.orderservice.domain.entity.Order;
import com.oms.orderservice.domain.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderReference(String orderReference);

    List<Order> findByCustomerId(String customerId);

    List<Order> findByStatus(OrderStatus status);
}