package com.hood.merch.service;

import com.hood.merch.models.Product;
import com.hood.merch.models.repo.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public void purchaseProduct(int productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Товар не найден"));

        if(product.getIn_stock() <= 0) {
            throw new RuntimeException("Товар закончился");
        }

        product.setIn_stock(product.getIn_stock() - quantity);
        if(product.getIn_stock() >= 0) {
            productRepository.save(product);
        } else {
            throw new RuntimeException("Товар закончился");
        }
    }
}
