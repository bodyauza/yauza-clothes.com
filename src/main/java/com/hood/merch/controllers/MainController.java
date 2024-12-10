package com.hood.merch.controllers;


import com.hood.merch.dto.ProductDTO;
import com.hood.merch.dto.OrderDTO;
import com.hood.merch.models.Order;
import com.hood.merch.models.Product;
import com.hood.merch.models.repo.OrderRepository;
import com.hood.merch.models.repo.ProductRepository;
import com.hood.merch.service.DefaultEmailService;
import com.hood.merch.service.ProductService;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


@Controller
public class MainController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

//    @Autowired
//    private SessionService sessionService;

    @Autowired
    private DefaultEmailService emailService;

    // Динамический массив-корзина.
    ArrayList<ProductDTO> cart;


    @GetMapping("/auth")
    public String auth(Model model) {
        model.addAttribute("title", "Auth");
        return "auth";
    }


    @GetMapping("/pay")
    public String pay(Model model) {
        model.addAttribute("title", "Pay");
        return "payment";
    }


    @GetMapping("/creating-order")
    public String creatingOrder(Model model) {
        model.addAttribute("title", "Оформить заказ");
        return "personal_data";
    }


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Главная");
        return "home";
    }


    // Добавление товара в корзину.
    @PostMapping("/add-item")
    protected String addItem(HttpServletRequest request, @RequestParam String img, @RequestParam int id, @RequestParam String name, @RequestParam Integer price, @RequestParam String size, @RequestParam Integer quantity, @RequestParam String color, @RequestParam Integer in_stock)
            throws ServletException, IOException {

        Product stock_check = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Товар не найден"));

        if (stock_check.getIn_stock() >= quantity) {
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(-1);
            ArrayList<ProductDTO> item_in_session = (ArrayList<ProductDTO>) session.getAttribute("cart");
            ProductDTO added_item = new ProductDTO(id, name, price, size, quantity, img, color, in_stock);

            for (ProductDTO item : item_in_session) {
                if (item.equals(added_item)) {
                    System.out.println("Найден товар-дубликат " + item.getName());
                    return "redirect:/cart";
                }
            }
            cart.add(added_item);
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    // Удаление из корзины единиц товара.
    @PostMapping("/remove-item")
    public String removeItem(HttpServletRequest request, @RequestParam int id) {
        HttpSession session = request.getSession();
        cart.removeIf(item -> item.getId() == id);
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // Изменение количества единиц товара в корзине.
    @PostMapping("/in-cart")
    public String inCart(HttpServletRequest request, @RequestParam String img, @RequestParam int id, @RequestParam String name, @RequestParam Integer price, @RequestParam String size, @RequestParam Integer quantity, @RequestParam String color, @RequestParam Integer in_stock)
            throws ServletException, IOException {

        Product stock_check = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Товар не найден"));
        HttpSession session = request.getSession();
        String referrer = request.getHeader("referer");
        String attr_name = Integer.toString(id);

        if(stock_check.getIn_stock() >= quantity) {
//            session.removeAttribute(attr_name);
//            session.setMaxInactiveInterval(-1);
            ArrayList<ProductDTO> item_in_session = (ArrayList<ProductDTO>) session.getAttribute("cart");
            ProductDTO added_item = new ProductDTO(id, name, price, size, quantity, img, color, in_stock);
            for (ProductDTO item : item_in_session) {
                if (item.equals(added_item)) {
                    // Может понадобиться переопределение equals() в ProductDTO.
                    cart.set(item_in_session.indexOf(item), added_item);
                    session.setAttribute("cart", cart);
                    return "redirect:/cart";
                }
            }
        } else {
            cart.removeIf(item -> item.getId() == stock_check.getId());
            session.setAttribute("cart", cart);
        }
        return referrer;
    }


    // Новый заказ.
    @ResponseBody
    @RequestMapping(value = "/new-order", method = RequestMethod.POST)
    public String newOrder(HttpServletRequest request, HttpServletResponse response, @RequestBody OrderDTO orderDTO) {
        String address = orderDTO.getCountry() + ", " + orderDTO.getRegion() + ", " + orderDTO.getCity() + ", " + orderDTO.getStreet()
                + ", " + orderDTO.getHome() + ", " + orderDTO.getIndex();

        HttpSession session = request.getSession();
        
        // Уменьшение кол-ва товаров на складе.
        for (ProductDTO productDTO : orderDTO.getOrdered_items()) {
            if (productDTO.getQuantity() <= 0 ||
                    productService.purchaseProduct(productDTO.getId(), productDTO.getQuantity()).equals("Товар закончился")) {
                return "redirect:/cart";
            }
        }
        
        Integer total_price = null;
        StringBuilder products = new StringBuilder();

        for (ProductDTO productDTO : orderDTO.getOrdered_items()) {
            // Количество единиц оформляемого товара и id товара возвращает браузер.
            Product item = productRepository.findById(productDTO.getId()).orElseThrow(() -> new RuntimeException("Товар не найден"));
            total_price += (productDTO.getQuantity() * item.getPrice());
            products.append(item.getName() + ", " + item.getSize() + ", " + Integer.toString(productDTO.getQuantity())
                    + ", " + item.getColor() + ";");
        }

        Date date = new Date();
        String status = "не оплачено";
        Order order = new Order(products.toString(), orderDTO.getFIO(), orderDTO.getEmail(), orderDTO.getTel(), orderDTO.getPost(),
                address, status, total_price, date);
        //проблема с добавлением в репозиторий
        orderRepository.save(order);
        System.out.println(products);
        System.out.println(address);

        try {
            emailService.sendEmailWithAttachment(orderDTO.getEmail(), "Оформление заказа", "Спасибо, что выбрали нас \uD83E\uDD70 \n"
                            + products + address + "\nСкоро вам поступит звонок от менеджера",
                    "classpath:templates/post.html");
        } catch (MessagingException | FileNotFoundException mailException) {
            System.out.println("Unable to send email");
        }

        session.removeAttribute("cart");

        return "redirect:/pay";
    }


    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("title", "Корзина");
        return "cart";
    }


    @GetMapping("/scarf")
    public String scarf(Model model) {
        Product product = productRepository.findById(1).orElseThrow(() -> new RuntimeException("Товар не найден"));
        ArrayList<Product> res = new ArrayList<>();  // зачем?
        res.add(product);
        String size = product.getSize().getSize();
        model.addAttribute("product", res);
        model.addAttribute("size", size);
        return "scarf";
    }


//    @PostMapping("/oversize-size")
//    public String oversizeSize(Model model, @RequestParam String size) {
//        String str = "XS";
//        if (size.equals(str)) {
//            id = 2;
//        }
//        Optional<Product> products1 = productRepository.findById(id);
//        ArrayList<Product> res = new ArrayList<>();
//        products1.ifPresent(res::add);
//        model.addAttribute("products1", res);
//        return "oversize";
//    }


    @GetMapping("/oversize")
    public String oversize(Model model) {
        Product product = productRepository.findById(2).orElseThrow(() -> new RuntimeException("Товар не найден"));
        ArrayList<Product> res = new ArrayList<>();
        res.add(product);
        model.addAttribute("product1", res);
        return "oversize";
    }


    @GetMapping("/contacts")
    public String contacts(Model model) {
        model.addAttribute("title", "Контакты");
        return "contacts";
    }
}
