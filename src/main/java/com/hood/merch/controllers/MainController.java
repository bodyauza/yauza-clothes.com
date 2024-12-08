package com.hood.merch.controllers;


import com.hood.merch.dto.ProductDTO;
import com.hood.merch.dto.OrderDTO;
import com.hood.merch.models.Order;
import com.hood.merch.models.Product;
import com.hood.merch.models.repo.OrderRepository;
import com.hood.merch.models.repo.ProductRepository;
import com.hood.merch.service.DefaultEmailService;
import com.hood.merch.service.ProductService;
import com.hood.merch.service.SessionService;
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

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DefaultEmailService emailService;

    private int id;

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
    protected String addToitem(HttpServletRequest request, @RequestParam String img, @RequestParam Integer id, @RequestParam String name, @RequestParam Integer price, @RequestParam String size, @RequestParam Integer quantity, @RequestParam String color, @RequestParam Integer in_stock)
            throws ServletException, IOException {

        Product stock_check = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Товар не найден"));

        if(stock_check.getIn_stock() >= quantity) {
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(-1);
            Set<ProductDTO> productDTO_set = new LinkedHashSet<>();
            List<String> attribs = Arrays.asList("item", "item1", "item2", "item3", "item4", "item5"); // и т.д.
            Set<String> missingAttrs = new LinkedHashSet<>();

            for (String attr : attribs) {
                ProductDTO productDTO = (ProductDTO) session.getAttribute(attr);
                if (productDTO == null) {
                    missingAttrs.add(attr); // Товары, которых нет в сессии.
                } else if (productDTO_set.add(productDTO)) { // Товар успешно добавлен в сет.
                    System.out.println("Товар " + productDTO.getName() + " в сессии и сете");
                }
            }

            for (String attr : missingAttrs) {
                ProductDTO productDTO_dto = new ProductDTO(id, name, price, size, quantity, img, color, in_stock);
                if (productDTO_set.add(productDTO_dto)) { // Товар успешно добавлен в сет.
                    System.out.println("Товар " + productDTO_dto.getName() + " добавлен в сессию и сет" + productDTO_dto.getSize());
                    session.setAttribute(attr, productDTO_dto);
                } else {
                    System.out.println("Найден товар-дубликат " + productDTO_dto.getName());
                    return "redirect:/basket";
                }
            }
        }

        return "redirect:/basket";
    }


    // Удаление из корзины единиц товара.
    @PostMapping("/item-remove")
    public String itemRemove(HttpServletRequest request, @RequestParam String attr_name) {
        HttpSession session = request.getSession();
        session.removeAttribute(attr_name);
        return "redirect:/basket";
    }


    // Изменение количества единиц товара в корзине.
    @PostMapping("/in-cart")
    public String inCart(HttpServletRequest request, @RequestParam String attr_name, @RequestParam String img, @RequestParam Integer id, @RequestParam String name, @RequestParam Integer price, @RequestParam String item_size, @RequestParam Integer quantity, @RequestParam String color, @RequestParam Integer in_stock)
            throws ServletException, IOException {

        Product stock_check = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Товар не найден"));
        HttpSession session = request.getSession();
        String referrer = request.getHeader("referer");

        if(stock_check.getIn_stock() >= quantity) {
            session.removeAttribute(attr_name);
            ProductDTO productDTO = (ProductDTO) session.getAttribute("attr_name");
            session.setMaxInactiveInterval(-1);
            productDTO = new ProductDTO(id, name, price, item_size, quantity, img, color, in_stock);
            session.setAttribute(attr_name, productDTO);
            return "redirect:/basket";
        } else {
            session.removeAttribute(attr_name);
        }
        return referrer;
    }


    // Новый заказ.
    @ResponseBody
    @RequestMapping(value = "/new-order", method = RequestMethod.POST)
    public String newOrder(HttpServletRequest request, HttpServletResponse response, @RequestBody OrderDTO orderDTO) {
        String address = orderDTO.getCountry() + ", " + orderDTO.getRegion() + ", " + orderDTO.getCity() + ", " + orderDTO.getStreet()
                + ", " + orderDTO.getHome() + ", " + orderDTO.getIndex();

        // Уменьшение кол-ва товаров на складе.
        for (ProductDTO productDTO : orderDTO.getOrdered_items()) {
            if (productService.purchaseProduct(productDTO.getId(), productDTO.getQuantity()).equals("Товар закончился")) {
                return "basket";
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

        sessionService.removeSessions(request, response);

        return "redirect:/pay";
    }


    @GetMapping("/basket")
    public String basket(Model model) {
        model.addAttribute("title", "Корзина");
        return "basket";
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
