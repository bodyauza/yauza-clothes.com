package com.hood.merch.controllers;


import com.hood.merch.models.Offer;
import com.hood.merch.models.Product;
import com.hood.merch.models.repo.OfferRepository;
import com.hood.merch.models.repo.ProductRepository;
import com.hood.merch.service.DefaultEmailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;


@Controller
public class MainController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    DefaultEmailService emailService;

    @GetMapping("/pay")
    public String pay(Model model) {
        model.addAttribute("title", "Pay");
        return "payment";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("", "Оформить заказ");
        return "offer";
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Главная");
        return "home";
    }

    //Добавление товара
    @PostMapping("/add-name")
    protected String doSet(HttpServletRequest request, HttpServletResponse response, @RequestParam String img, @RequestParam String name, @RequestParam Integer price, @RequestParam String item_size, @RequestParam Integer quantity, @RequestParam String color)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Product product = (Product) session.getAttribute("cart");
        session.setMaxInactiveInterval(-1);

        HttpSession session1 = request.getSession();
        Product product1 = (Product) session1.getAttribute("cart1");
        session1.setMaxInactiveInterval(-1);

        if (product == null && product1 == null) {
            product = new Product(name, price, item_size, quantity, img, color);
            session.setAttribute("cart", product);
        } else if (product1 == null) {
            product1 = new Product(name, price, item_size, quantity, img, color);
            if (!Objects.equals(product1.getName(), product.getName()))
                session1.setAttribute("cart1", product1);
        } else if (product == null) {
            product = new Product(name, price, item_size, quantity, img, color);
            if (!Objects.equals(product1.getName(), product.getName()))
                session.setAttribute("cart", product);
        }
        return "redirect:/basket";
    }

    //Изменение количества

    @PostMapping("/in-cart")
   protected String inCart(HttpServletRequest request, HttpServletResponse response, @RequestParam String img, @RequestParam String name, @RequestParam Integer price, @RequestParam String item_size, @RequestParam Integer quantity, @RequestParam String color)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        session.removeAttribute("cart");
        Product product = (Product) session.getAttribute("cart");
        session.setMaxInactiveInterval(-1);
        product = new Product(name, price, item_size, quantity, img, color);
        session.setAttribute("cart", product);
        return "redirect:/basket";
}

    @PostMapping("/in-cart1")
    protected String inCart1(HttpServletRequest request, HttpServletResponse response, @RequestParam String img, @RequestParam String name, @RequestParam Integer price, @RequestParam String item_size, @RequestParam Integer quantity, @RequestParam String color)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        session.removeAttribute("cart1");
        Product product = (Product) session.getAttribute("cart1");
        session.setMaxInactiveInterval(-1);
        product = new Product(name, price, item_size, quantity, img, color);
        session.setAttribute("cart1", product);
        return "redirect:/basket";
    }

    @PostMapping("/new-order")
    public String newOrder(@RequestParam String FIO, @RequestParam String email, @RequestParam String tel, @RequestParam String post, @RequestParam String street, @RequestParam String home, @RequestParam String country, @RequestParam String city, @RequestParam String region, @RequestParam String index, @RequestParam String name, @RequestParam String item_size, @RequestParam Integer quantity, @RequestParam String color, @RequestParam Integer price, @RequestParam(value = "name1", required=false) String name1, @RequestParam(value = "item_size1", required=false) String item_size1, @RequestParam(value = "quantity1", required=false) Integer quantity1, @RequestParam(value = "color1", required=false) String color1, @RequestParam(value = "price1", required=false) Integer price1) {
        String address = country + ", " + region + ", " + city + ", " + street + ", " + home + ", " + index;

        Integer total_price = (quantity * price);

        String products = name + ", " + item_size + ", " + quantity.toString() + ", " + color + ";" + "\n" + "Сумма заказа:" + total_price.toString() + "руб.;";

        if (quantity1 != null) {
            total_price = (quantity * price) + (quantity1 * price1);
            products = name + ", " + item_size + ", " + quantity.toString() + ", " + color + ";" + "\n" + name1 + ", " + item_size1 + ", " + quantity1.toString() + ", " + color1 + ";" + "\n" + "Сумма заказа:" + total_price.toString() + "руб.;";
        }

        Date date = new Date();
        String status = "не оплачено";
        Offer offer = new Offer(products, FIO, email, tel, post, address, status, total_price, date);
        offerRepository.save(offer);
        System.out.println(products);
        System.out.println(address);

        try {
            emailService.sendEmailWithAttachment(email, "Оформление заказа", "Спасибо, что выбрали нас \uD83E\uDD70 \nСкоро вам поступит звонок от менеджера",
                    "classpath:templates/post.html");
        } catch (MessagingException | FileNotFoundException mailException) {
            System.out.println("Unable to send email");
        }

        return "redirect:/pay";
    }

    //Удаление из корзины
    @PostMapping("/cart/remove")
    public String Cart_remove(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.removeAttribute("cart");
        return "redirect:/basket";
    }

    @PostMapping("/cart1/remove")
    public String Cart1_remove(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.removeAttribute("cart1");
        return "redirect:/basket";
    }


    @GetMapping("/basket")
    public String basket(Model model) {
        model.addAttribute("title", "Корзина");
        return "basket";
    }

    //Товары

    @GetMapping("/scarf")
    public String scarf(Model model) {
        Optional<Product> products = productRepository.findById(1);
        ArrayList<Product> res = new ArrayList<>();
        products.ifPresent(res::add);
        model.addAttribute("products", res);
        return "scarf";
    }

    @GetMapping("/oversize")
    public String black_scarf(Model model) {
        Optional<Product> products1 = productRepository.findById(2);
        ArrayList<Product> res = new ArrayList<>();
        products1.ifPresent(res::add);
        model.addAttribute("products1", res);
        return "oversize";
    }


    @GetMapping("/contacts")
    public String contacts(Model model) {
        model.addAttribute("title", "Контакты");
        return "contacts";
    }

    @GetMapping("/support")
    public String support(Model model) {
        model.addAttribute("title", "Поддержка");
        return "support";
    }

    @GetMapping("/mail")
    public String offer(Model model) {
        model.addAttribute("title", "Email");
        return "sender";
    }

}
