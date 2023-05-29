package com.hood.merch.controllers;


import com.hood.merch.models.Offer;
import com.hood.merch.models.Product;
import com.hood.merch.models.repo.OfferRepository;
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
    private OfferRepository offerRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DefaultEmailService emailService;

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
    protected String doSet(HttpServletRequest request, HttpServletResponse response, @RequestParam String img, @RequestParam Integer id, @RequestParam String name, @RequestParam Integer price, @RequestParam String item_size, @RequestParam Integer quantity, @RequestParam String color, @RequestParam Integer in_stock)
            throws ServletException, IOException {

        Product stock_check = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Товар не найден"));

        if(stock_check.getIn_stock() >= quantity) {
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(-1);

            Set<Product> cart = new LinkedHashSet<>();
            List<String> attribs = Arrays.asList("cart", "cart1", "cart2", "cart3", "cart4", "cart5"); // и т.д.
            Set<String> missingAttrs = new LinkedHashSet<>();

            for (String attr : attribs) {
                Product product = (Product) session.getAttribute(attr);
                if (null == product) {
                    missingAttrs.add(attr);
                } else if (cart.add(product)) { // товар успешно добавлен в сет
                    System.out.println("Товар " + product.getName() + " в сессии и сете");
                }
            }

            for (String attr : missingAttrs) {
                Product product = new Product(id, name, price, item_size, quantity, img, color, in_stock);
                if (cart.add(product)) { // товар успешно добавлен в сет
                    System.out.println("Товар " + product.getName() + " добавлен в сессию и сет");
                    session.setAttribute(attr, product);
                } else {
                    System.out.println("Найден товар-дубликат " + product.getName() + " или добавлен другой размер");
                }
            }
        }

        return "redirect:/basket";
    }

    //Удаление из корзины
    @PostMapping("/cart/remove")
    public String Cart_remove(HttpServletRequest request, HttpServletResponse response, @RequestParam String attr_name, @RequestParam Integer id) {
        HttpSession session = request.getSession();
        session.removeAttribute(attr_name);
        return "redirect:/basket";
    }

    //Изменение количества
    @PostMapping("/in-cart")
   protected String inCart(HttpServletRequest request, HttpServletResponse response, @RequestParam String attr_name, @RequestParam String img, @RequestParam Integer id, @RequestParam String name, @RequestParam Integer price, @RequestParam String item_size, @RequestParam Integer quantity, @RequestParam String color, @RequestParam Integer in_stock)
            throws ServletException, IOException {

        Product stock = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Товар не найден"));
        HttpSession session = request.getSession();

        if(stock.getIn_stock() >= quantity) {
            session.removeAttribute(attr_name);
            Product product = (Product) session.getAttribute("attr_name");
            session.setMaxInactiveInterval(-1);
            product = new Product(id, name, price, item_size, quantity, img, color, in_stock);
            session.setAttribute(attr_name, product);
        } else {
            session.removeAttribute(attr_name);
        }
        return "redirect:/basket";
}

    //Новый заказ
    @PostMapping("/new-order")
    public String newOrder(HttpServletRequest request, HttpServletResponse response, @RequestParam String FIO, @RequestParam String email, @RequestParam String tel, @RequestParam String post, @RequestParam String street, @RequestParam String home, @RequestParam String country, @RequestParam String city, @RequestParam String region, @RequestParam String index, @RequestParam Integer id, @RequestParam String name, @RequestParam String item_size, @RequestParam Integer quantity, @RequestParam String color, @RequestParam Integer price, @RequestParam(value = "id1", required=false) Integer id1, @RequestParam(value = "name1", required=false) String name1, @RequestParam(value = "item_size1", required=false) String item_size1, @RequestParam(value = "quantity1", required=false) Integer quantity1, @RequestParam(value = "color1", required=false) String color1, @RequestParam(value = "price1", required=false) Integer price1) {
        String address = country + ", " + region + ", " + city + ", " + street + ", " + home + ", " + index;

        if (productService.purchaseProduct(id, quantity) == "Товар закончился") {
            return "basket";
        } else {

            Integer total_price = (quantity * price);

            String products = name + ", " + item_size + ", " + quantity.toString() + ", " + color + ";" + "\n" + "Сумма заказа:" + total_price.toString() + "руб.;";

            if (quantity1 != null) {

                productService.purchaseProduct(id1, quantity1);// == 0

                total_price = (quantity * price) + (quantity1 * price1);
                products = name + ", " + item_size + ", " + quantity.toString() + ", " + color + ";" + "\n" + name1 + ", " + item_size1 + ", " + quantity1.toString() + ", " + color1 + ";" + "\n" + "Сумма заказа:" + total_price.toString() + "руб.;";
            }

            Date date = new Date();
            String status = "не оплачено";
            Offer offer = new Offer(products, FIO, email, tel, post, address, status, total_price, date);
            //проблема с добавлением в репозиторий
            offerRepository.save(offer);
            System.out.println(products);
            System.out.println(address);

            try {
                emailService.sendEmailWithAttachment(email, "Оформление заказа", "Спасибо, что выбрали нас \uD83E\uDD70 \n" + products + address + "\nСкоро вам поступит звонок от менеджера",
                        "classpath:templates/post.html");
            } catch (MessagingException | FileNotFoundException mailException) {
                System.out.println("Unable to send email");
            }

            sessionService.removeSessions(request, response);

            return "redirect:/pay";
        }
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

    @GetMapping("/mail")
    public String offer(Model model) {
        model.addAttribute("title", "Email");
        return "sender";
    }
}
