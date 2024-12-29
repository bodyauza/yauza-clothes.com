package com.yauza.clothes.models.repo;

import com.yauza.clothes.models.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {
    Optional<Product> findById(int i);
}
