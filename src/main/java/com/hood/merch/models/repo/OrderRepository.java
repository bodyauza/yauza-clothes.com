package com.hood.merch.models.repo;

import com.hood.merch.models.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository <Order, Long> {
    Optional<Order> findById(int i);
}
