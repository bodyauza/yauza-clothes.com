package com.yauza.clothes.service;

import com.yauza.clothes.models.Product;
import com.yauza.clothes.models.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public String purchaseProduct(Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Товар не найден"));

        product.setIn_stock(product.getIn_stock() - quantity);
        if(product.getIn_stock() >= 0) {
            productRepository.save(product);
        } else {
            return "Товар закончился";
        }
        return "Товар отгружен со склада";
    }

    public ArrayList<String> getAllIds() {
        // Получаем все сущности.
        ArrayList<Product> entities = (ArrayList<Product>) productRepository.findAll();
        // Извлекаем ID из сущностей и преобразуем их в строку.
        return entities.stream()
                .map(product -> String.valueOf(product.getId())) // Преобразуем ID в строку.
                .collect(Collectors.toCollection(ArrayList::new)); // Собираем в ArrayList.
    }

}
