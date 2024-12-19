package com.hood.merch.controllers;

import com.hood.merch.dto.ProductDTO;
import com.hood.merch.models.Product;
import com.hood.merch.models.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;

@Controller
public class DifferentSizesController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/oversize/{id}")
    public String oversize(Model model, @PathVariable("id") Integer id) {
        if (id == null) {
            id = 2; // Значение по умолчанию
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));
        String size = product.getSize().getSize();

        model.addAttribute("product1", Collections.singletonList(product));
        model.addAttribute("size1", size);

        return "oversize";
    }

    @GetMapping("/size-update/{id}")
    @ResponseBody
    public ResponseEntity<ProductDTO> sizeUpdate(@PathVariable("id") Integer id) {
        if (id == null) {
            id = 2; // Значение по умолчанию.
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));
        ProductDTO productDTO = new ProductDTO(product.getId(), product.getName(), product.getPrice(),
                product.getSize().getSize(), product.getQuantity(), product.getImg(), product.getColor(),
                product.getIn_stock());

        if (productDTO != null) {
            return ResponseEntity.ok(productDTO); // Возвращаем 200 OK с продуктом
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND) // Возвращаем 404 NOT FOUND, если продукт не найден
                    .body(null);
        }
    }
}
