package com.giftshop.service;

import com.giftshop.model.Order;
import com.giftshop.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> all() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public void updateStatus(Long id, String status) {
        orderRepository.findById(id).ifPresent(o -> {
            o.setStatus(status);
            orderRepository.save(o);
        });
    }

    public long count() {
        return orderRepository.count();
    }

    public long countNew() {
        return orderRepository.countByStatus("NEW");
    }
}
