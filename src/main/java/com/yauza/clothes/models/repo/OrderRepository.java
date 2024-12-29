package com.yauza.clothes.models.repo;

import com.yauza.clothes.models.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository <Order, Long> {
    Optional<Order> findById(int i);
}
