package main.java.com.oms.order_service.domain.repository;

import com.oms.orderservice.domain.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    // Scheduler calls this to find events not yet sent to Kafka
    List<OutboxEvent> findByPublishedFalseOrderByCreatedAtAsc();
}